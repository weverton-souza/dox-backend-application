package com.dox.application.service

import com.dox.application.port.input.AggregatedFormGroup
import com.dox.application.port.input.AggregatedRespondent
import com.dox.application.port.input.ComparisonRespondent
import com.dox.application.port.input.ComparisonResult
import com.dox.application.port.input.CustomerFormsAggregationUseCase
import com.dox.application.port.input.FormSummary
import com.dox.application.port.input.FormVersionSummary
import com.dox.application.port.output.CustomerPersistencePort
import com.dox.application.port.output.FormDraftPersistencePort
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
    private val formDraftPersistencePort: FormDraftPersistencePort,
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

        val pendingLinkIds = links.filter { it.status == FormLinkStatus.PENDING }.map { it.id }
        val draftsByLinkId =
            formDraftPersistencePort.findByFormLinkIds(pendingLinkIds).associateBy { it.formLinkId }

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
                            versionMajor = version?.versionMajor ?: 1,
                            versionMinor = version?.versionMinor ?: 0,
                            title = version?.title ?: "",
                        ),
                    sentAt = sentAt,
                    respondents =
                        groupLinks
                            .sortedBy { it.createdAt }
                            .map { link ->
                                buildAggregatedRespondent(
                                    link = link,
                                    customer = customer,
                                    contact = link.customerContactId?.let { contactsById[it] },
                                    response = responsesByLinkId[link.id],
                                    draft = draftsByLinkId[link.id],
                                    versionFields = version?.fields ?: emptyList(),
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
                    responseId = response?.id,
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
            version =
                FormVersionSummary(
                    id = version.id,
                    versionMajor = version.versionMajor,
                    versionMinor = version.versionMinor,
                    title = version.title,
                ),
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
        customer: com.dox.domain.model.Customer,
        contact: CustomerContact?,
        response: FormResponse?,
        draft: com.dox.domain.model.FormDraft? = null,
        versionFields: List<Map<String, Any?>> = emptyList(),
    ): AggregatedRespondent {
        val progress =
            if (draft != null && link.status == FormLinkStatus.PENDING) {
                calculateProgress(draft.partialResponse, versionFields)
            } else {
                null
            }

        return AggregatedRespondent(
            linkId = link.id,
            responseId = response?.id,
            respondentType = link.respondentType,
            respondentName = resolveRespondentName(link.respondentType, customer.displayName(), contact),
            recipientEmail = resolveRecipientEmail(link.respondentType, customer, contact),
            customerContactId = link.customerContactId,
            relationType = contact?.relationType?.name?.lowercase(),
            status = link.status,
            submittedAt = if (link.status == FormLinkStatus.ANSWERED) response?.updatedAt else null,
            firstViewedAt = link.firstViewedAt,
            manualResendCount = link.manualResendCount,
            progressPercent = progress?.percent,
            currentPageIndex = progress?.currentPage,
            totalPages = progress?.totalPages,
            lastDraftSavedAt = draft?.savedAt,
            expiresAt = link.expiresAt,
        )
    }

    private data class DraftProgress(
        val percent: Int,
        val currentPage: Int?,
        val totalPages: Int?,
    )

    @Suppress("UNCHECKED_CAST")
    private fun calculateProgress(
        partial: Map<String, Any?>,
        fields: List<Map<String, Any?>>,
    ): DraftProgress? {
        if (fields.isEmpty()) return null
        val answeredCount = (partial["answers"] as? List<Map<String, Any?>>)?.size ?: 0
        val totalAnswerable = fields.count { (it["type"] as? String) != "section-header" }
        val percent =
            if (totalAnswerable > 0) {
                ((answeredCount.toDouble() / totalAnswerable) * 100).toInt().coerceIn(0, 100)
            } else {
                0
            }
        val currentPage = (partial["currentPage"] as? Number)?.toInt()
        val totalPages = computeTotalPages(fields)
        return DraftProgress(percent = percent, currentPage = currentPage, totalPages = totalPages)
    }

    private fun computeTotalPages(fields: List<Map<String, Any?>>): Int {
        if (fields.isEmpty()) return 0
        val sectionHeaders = fields.count { (it["type"] as? String) == "section-header" }
        return if (sectionHeaders == 0) 1 else sectionHeaders
    }

    private fun resolveRecipientEmail(
        type: RespondentType,
        customer: com.dox.domain.model.Customer,
        contact: CustomerContact?,
    ): String? =
        when (type) {
            RespondentType.CUSTOMER -> (customer.data["email"] as? String)?.trim()?.ifBlank { null }
            RespondentType.CONTACT -> contact?.email?.trim()?.ifBlank { null }
            RespondentType.PROFESSIONAL -> null
        }

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
