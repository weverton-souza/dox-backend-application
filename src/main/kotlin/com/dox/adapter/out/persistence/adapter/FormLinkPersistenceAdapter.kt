package com.dox.adapter.out.persistence.adapter

import com.dox.adapter.out.persistence.entity.FormLinkJpaEntity
import com.dox.adapter.out.persistence.repository.FormLinkJpaRepository
import com.dox.application.port.output.FormLinkPersistencePort
import com.dox.domain.model.FormLink
import org.springframework.stereotype.Component
import java.util.UUID

@Component
class FormLinkPersistenceAdapter(
    private val formLinkJpaRepository: FormLinkJpaRepository,
) : FormLinkPersistencePort {
    override fun save(formLink: FormLink): FormLink {
        val entity =
            formLinkJpaRepository.findById(formLink.id).orElse(null)
                ?: FormLinkJpaEntity().apply { id = formLink.id }
        entity.formId = formLink.formId
        entity.formVersionId = formLink.formVersionId
        entity.customerId = formLink.customerId
        entity.customerContactId = formLink.customerContactId
        entity.respondentType = formLink.respondentType
        entity.createdBy = formLink.createdBy
        entity.status = formLink.status
        entity.expiresAt = formLink.expiresAt
        return formLinkJpaRepository.save(entity).toDomain()
    }

    override fun findById(id: UUID): FormLink? = formLinkJpaRepository.findById(id).orElse(null)?.toDomain()

    override fun findAll(): List<FormLink> = formLinkJpaRepository.findAllByOrderByCreatedAtDesc().map { it.toDomain() }

    override fun findByCustomerId(customerId: UUID): List<FormLink> = formLinkJpaRepository.findByCustomerIdOrderByCreatedAtDesc(customerId).map { it.toDomain() }

    private fun FormLinkJpaEntity.toDomain() =
        FormLink(
            id = id,
            formId = formId,
            formVersionId = formVersionId,
            customerId = customerId,
            customerContactId = customerContactId,
            respondentType = respondentType,
            createdBy = createdBy,
            status = status,
            expiresAt = expiresAt,
            createdAt = createdAt,
            updatedAt = updatedAt,
        )
}
