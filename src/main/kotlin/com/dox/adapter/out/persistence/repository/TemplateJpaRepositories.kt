package com.dox.adapter.out.persistence.repository

import com.dox.adapter.out.persistence.entity.ChartTemplateJpaEntity
import com.dox.adapter.out.persistence.entity.ReportTemplateJpaEntity
import com.dox.adapter.out.persistence.entity.ScoreTableTemplateJpaEntity
import org.springframework.data.jpa.repository.JpaRepository
import java.util.UUID

interface ReportTemplateJpaRepository : JpaRepository<ReportTemplateJpaEntity, UUID>

interface ScoreTableTemplateJpaRepository : JpaRepository<ScoreTableTemplateJpaEntity, UUID>

interface ChartTemplateJpaRepository : JpaRepository<ChartTemplateJpaEntity, UUID>
