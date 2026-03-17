package com.dox.application.port.output

import com.dox.domain.model.FormLink
import java.util.UUID

interface FormLinkPersistencePort {

    fun save(formLink: FormLink): FormLink

    fun findById(id: UUID): FormLink?

    fun findAll(): List<FormLink>

    fun findByCustomerId(customerId: UUID): List<FormLink>
}
