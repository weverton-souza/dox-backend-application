package com.dox.adapter.out.ai.config

import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.context.annotation.Configuration

@Configuration
@ConfigurationProperties(prefix = "dox.ai")
class AiConfig {
    var enabled: Boolean = true
    var defaultModel: String = "claude-sonnet-4-6"
    var maxConcurrentPerTenant: Int = 5
    var maxRegenerationsPerReport: Int = 3
    var cost: CostConfig = CostConfig()

    class CostConfig {
        var sonnetInputPerMillion: Double = 3.00
        var sonnetOutputPerMillion: Double = 15.00
        var sonnetCacheReadPerMillion: Double = 0.30
        var sonnetCacheWritePerMillion: Double = 3.75
        var brlUsdRate: Double = 5.80
    }
}
