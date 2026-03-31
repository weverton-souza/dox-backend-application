package com.dox.application.port.output

import com.dox.domain.model.AiGenerationSource
import java.util.UUID

interface AiGenerationSourcePersistencePort {
    fun saveAll(sources: List<AiGenerationSource>): List<AiGenerationSource>

    fun findByReportId(reportId: UUID): List<AiGenerationSource>

    fun findByGenerationId(generationId: UUID): List<AiGenerationSource>
}
