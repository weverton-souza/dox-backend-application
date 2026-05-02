package com.dox.adapter.out.persistence.adapter

import com.dox.adapter.out.persistence.entity.FormDraftJpaEntity
import com.dox.adapter.out.persistence.repository.FormDraftJpaRepository
import com.dox.application.port.output.FormDraftPersistencePort
import com.dox.domain.model.FormDraft
import org.springframework.stereotype.Component
import java.util.UUID

@Component
class FormDraftPersistenceAdapter(
    private val formDraftJpaRepository: FormDraftJpaRepository,
) : FormDraftPersistencePort {
    override fun findByFormLinkId(formLinkId: UUID): FormDraft? = formDraftJpaRepository.findByFormLinkId(formLinkId)?.toDomain()

    override fun save(draft: FormDraft): FormDraft {
        val entity =
            formDraftJpaRepository.findByFormLinkId(draft.formLinkId)
                ?: FormDraftJpaEntity(formLinkId = draft.formLinkId)
        entity.partialResponse = draft.partialResponse
        return formDraftJpaRepository.save(entity).toDomain()
    }

    override fun deleteByFormLinkId(formLinkId: UUID) = formDraftJpaRepository.deleteByFormLinkId(formLinkId)

    private fun FormDraftJpaEntity.toDomain() =
        FormDraft(
            id = id,
            formLinkId = formLinkId,
            partialResponse = partialResponse,
            savedAt = savedAt,
        )
}
