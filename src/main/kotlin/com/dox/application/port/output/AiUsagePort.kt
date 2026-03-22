package com.dox.application.port.output

import com.dox.domain.model.AiUsage
import com.dox.domain.model.TokenSummary
import java.util.UUID

interface AiUsagePort {

    fun save(usage: AiUsage): AiUsage

    fun countByProfessionalAndMonth(professionalId: UUID, month: Int, year: Int): Int

    fun countByReportId(reportId: UUID): Int

    fun findByProfessionalAndMonth(professionalId: UUID, month: Int, year: Int): List<AiUsage>

    fun sumTokensByProfessionalAndMonth(professionalId: UUID, month: Int, year: Int): TokenSummary

    fun findByReportId(reportId: UUID): List<AiUsage>
}
