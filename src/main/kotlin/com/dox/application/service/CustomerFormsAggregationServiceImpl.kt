package com.dox.application.service

import com.dox.application.port.input.AggregatedFormGroup
import com.dox.application.port.input.AggregatedRespondent
import com.dox.application.port.input.ComparisonRespondent
import com.dox.application.port.input.ComparisonResult
import com.dox.application.port.input.CustomerFormsAggregationUseCase
import com.dox.application.port.input.FormSummary
import com.dox.application.port.input.FormVersionSummary
import com.dox.application.port.output.CustomerPersistencePort
import com.dox.application.port.output.FormLinkPersistencePort
import com.dox.application.port.output.FormPersistencePort
import com.dox.application.scoring.ScoringEngine
import com.dox.application.scoring.ScoringMapper
import com.dox.domain.enum.FormLinkStatus
import com.dox.domain.enum.RespondentType
import com.dox.domain.exception.BusinessException
import com.dox.domain.exception.ResourceNotFoundException
import com.dox.domain.model.CustomerContact
import com.dox.domain.model.FormLink
import com.dox.domain.model.FormResponse
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.time.LocalDateTime
import java.util.UUID

@Service
@Transactional(readOnly = true)
class CustomerFormsAggregationServiceImpl(
    private val formLinkPersistencePort: FormLinkPersistencePort,
    private val formPersistencePort: FormPersistencePort,
    private val customerPersistencePort: CustomerPersistencePort,
    private val scoringEngine: ScoringEngine,
    private val scoringMapper: ScoringMapper,
) : CustomerFormsAggregationUseCase {
    override fun aggregatedByCustomer(customerId: UUID): List<AggregatedFormGroup> {
        val customer =
            customerPersistencePort.findById(customerId)
                ?: throw ResourceNotFoundException("Cliente", customerId.toString())

        val links = formLinkPersistencePort.findByCustomerId(customerId)
        if (links.isEmpty()) return emptyList()

        val versionIds = links.map { it.formVersionId }.toSet()
        val formIds = links.map { it.formId }.toSet()
        val versionsById =
            formPersistencePort.findVersionsByFormIds(formIds)
                .filter { it.id in versionIds }
                .associateBy { it.id }

        val contactIds = links.mapNotNull { it.customerContactId }.toSet()
        val contactsById =
            customerPersistencePort.findContactsByIds(contactIds)
                .associateBy { it.id }

        val groupedKeys = links.map { it.formId to it.formVersionId }.toSet()
        val responsesByLinkId =
            groupedKeys.flatMap { (formId, versionId) ->
                val groupLinks = links.filter { it.formId == formId && it.formVersionId == versionId }
                val responses = formPersistencePort.findResponsesByCustomerAndFormVersion(customerId, formId, versionId)
                mapResponsesToLinks(groupLinks, responses).entries
            }.associate { it.key to it.value }

        val customerName = customer.displayName()

        return links
            .groupBy { it.formId to it.formVersionId }
            .map { (key, groupLinks) ->
                val (formId, versionId) = key
                val version = versionsById[versionId]
                val sentAt = groupLinks.minOfOrNull { it.createdAt ?: LocalDateTime.now() } ?: LocalDateTime.now()

                AggregatedFormGroup(
                    form = FormSummary(id = formId, title = version?.title ?: "Formulário"),
                    version =
                        FormVersionSummary(
                            id = versionId,
                            version = version?.version ?: 1,
                            title = version?.title ?: "",
                        ),
                    sentAt = sentAt,
                    respondents =
                        groupLinks
                            .sortedBy { it.createdAt }
                            .map { link ->
                                buildAggregatedRespondent(
                                    link = link,
                                    customerName = customerName,
                                    contact = link.customerContactId?.let { contactsById[it] },
                                    response = responsesByLinkId[link.id],
                                )
                            },
                )
            }
            .sortedByDescending { it.sentAt }
    }

    override fun comparison(
        customerId: UUID,
        formId: UUID,
        formVersionId: UUID,
    ): ComparisonResult {
        val customer =
            customerPersistencePort.findById(customerId)
                ?: throw ResourceNotFoundException("Cliente", customerId.toString())

        val form =
            formPersistencePort.findFormById(formId)
                ?: throw ResourceNotFoundException("Formulário", formId.toString())

        val version =
            formPersistencePort.findVersionById(formVersionId)
                ?: throw ResourceNotFoundException("Versão do formulário", formVersionId.toString())

        if (version.formId != formId) {
            throw BusinessException("Versão não pertence ao formulário informado")
        }

        val links = formLinkPersistencePort.findByCustomerAndFormVersion(customerId, formId, formVersionId)
        if (links.isEmpty()) {
            throw ResourceNotFoundException(
                "Links do cliente para a versão",
                "$customerId:$formId:$formVersionId",
            )
        }

        val contactIds = links.mapNotNull { it.customerContactId }.toSet()
        val contactsById =
            customerPersistencePort.findContactsByIds(contactIds)
                .associateBy { it.id }

        val responses = formPersistencePort.findResponsesByCustomerAndFormVersion(customerId, formId, formVersionId)
        val responsesByLinkId = mapResponsesToLinks(links, responses)

        val fieldsDef = scoringMapper.parseFields(version.fields)
        val scoringConfig = scoringMapper.parseScoringConfig(version.scoringConfig)
        val customerName = customer.displayName()

        val respondents =
            links.sortedBy { it.createdAt }.map { link ->
                val contact = link.customerContactId?.let { contactsById[it] }
                val response = responsesByLinkId[link.id]
                val answersRaw = response?.answers ?: emptyList()
                val answersTyped = scoringMapper.parseAnswers(answersRaw)
                val scores =
                    if (response != null) {
                        scoringEngine.calculateAllScores(scoringConfig, fieldsDef, answersTyped)
                    } else {
                        emptyList()
                    }

                ComparisonRespondent(
                    linkId = link.id,
                    respondentType = link.respondentType,
                    respondentName = resolveRespondentName(link.respondentType, customerName, contact),
                    customerContactId = link.customerContactId,
                    relationType = contact?.relationType?.name?.lowercase(),
                    status = link.status,
                    submittedAt = response?.updatedAt,
                    answers = answersRaw,
                    scoreBreakdown = scores,
                )
            }

        return ComparisonResult(
            form = FormSummary(id = form.id, title = version.title),
            version = FormVersionSummary(id = version.id, version = version.version, title = version.title),
            fields = version.fields,
            scoringConfig = version.scoringConfig,
            respondents = respondents,
        )
    }

    private fun mapResponsesToLinks(
        links: List<FormLink>,
        responses: List<FormResponse>,
    ): Map<UUID, FormResponse> {
        if (responses.isEmpty()) return emptyMap()
        val byContact = responses.groupBy { it.customerContactId }
        val used = mutableSetOf<UUID>()
        val result = mutableMapOf<UUID, FormResponse>()
        for (link in links.sortedBy { it.createdAt }) {
            val candidates = byContact[link.customerContactId] ?: continue
            val match = candidates.firstOrNull { it.id !in used } ?: continue
            used.add(match.id)
            result[link.id] = match
        }
        return result
    }

    private fun buildAggregatedRespondent(
        link: FormLink,
        customerName: String?,
        contact: CustomerContact?,
        response: FormResponse?,
    ): AggregatedRespondent =
        AggregatedRespondent(
            linkId = link.id,
            respondentType = link.respondentType,
            respondentName = resolveRespondentName(link.respondentType, customerName, contact),
            customerContactId = link.customerContactId,
            relationType = contact?.relationType?.name?.lowercase(),
            status = link.status,
            submittedAt = if (link.status == FormLinkStatus.ANSWERED) response?.updatedAt else null,
            expiresAt = link.expiresAt,
        )

    private fun resolveRespondentName(
        type: RespondentType,
        customerName: String?,
        contact: CustomerContact?,
    ): String? =
        when (type) {
            RespondentType.CUSTOMER -> customerName
            RespondentType.CONTACT -> contact?.name
            RespondentType.PROFESSIONAL -> null
        }
}
