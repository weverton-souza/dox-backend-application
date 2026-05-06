package com.dox.application.port.output

import com.dox.domain.model.FormDraft
import java.util.UUID

interface FormDraftPersistencePort {
    fun findByFormLinkId(formLinkId: UUID): FormDraft?

    fun findByFormLinkIds(formLinkIds: Collection<UUID>): List<FormDraft>

    fun save(draft: FormDraft): FormDraft

    fun deleteByFormLinkId(formLinkId: UUID)
}
