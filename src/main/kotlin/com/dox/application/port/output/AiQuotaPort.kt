package com.dox.application.port.output

import com.dox.domain.model.AiQuota

interface AiQuotaPort {
    fun findQuota(): AiQuota?

    fun save(quota: AiQuota): AiQuota
}
