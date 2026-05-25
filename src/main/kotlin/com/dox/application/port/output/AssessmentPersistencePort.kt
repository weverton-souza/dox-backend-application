package com.dox.application.port.output

import com.dox.domain.model.Assessment
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import java.util.UUID

interface AssessmentPersistencePort {
    fun save(assessment: Assessment): Assessment

    fun findById(id: UUID): Assessment?

    fun findByCustomerId(
        customerId: UUID,
        pageable: Pageable,
    ): Page<Assessment>

    fun softDelete(id: UUID)

    fun findInstrumentNamesByQuery(query: String): List<String>
}
