package com.dox.adapter.out.persistence.adapter

import com.dox.adapter.out.persistence.entity.FormDraftJpaEntity
import com.dox.adapter.out.persistence.repository.FormDraftJpaRepository
import com.dox.application.port.output.FormDraftPersistencePort
import com.dox.domain.model.FormDraft
import org.springframework.stereotype.Component
import java.util.UUID

@Component
class FormDraftPersistenceAdapter(
    private val repository: FormDraftJpaRepository,
) : FormDraftPersistencePort {
    override fun findByFormLinkId(formLinkId: UUID): FormDraft? = repository.findById(formLinkId).orElse(null)?.toDomain()

    override fun save(draft: FormDraft): FormDraft {
        val entity =
            repository.findById(draft.formLinkId).orElse(null)
                ?: FormDraftJpaEntity().apply { formLinkId = draft.formLinkId }
        entity.partialResponse = draft.partialResponse
        return repository.save(entity).toDomain()
    }

    override fun deleteByFormLinkId(formLinkId: UUID) {
        if (repository.existsById(formLinkId)) {
            repository.deleteById(formLinkId)
        }
    }

    private fun FormDraftJpaEntity.toDomain() =
        FormDraft(
            formLinkId = formLinkId,
            partialResponse = partialResponse,
            savedAt = savedAt,
        )
}
