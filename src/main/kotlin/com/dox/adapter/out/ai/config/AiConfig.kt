package com.dox.adapter.out.ai.config

import com.dox.application.port.output.AiConfigPort
import com.dox.application.port.output.AiCostConfig
import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.context.annotation.Configuration

@Configuration
@ConfigurationProperties(prefix = "dox.ai")
class AiConfig : AiConfigPort {
    var enabled: Boolean = true
    var defaultModel: String = "claude-sonnet-4-6"
    var maxConcurrentPerTenant: Int = 5
    var maxRegenerationsPerReport: Int = 3
    var ssePoolSize: Int = 10
    var cost: CostConfig = CostConfig()

    override fun isEnabled(): Boolean = enabled

    override fun defaultModel(): String = defaultModel

    override fun concurrencyLimit(): Int = maxConcurrentPerTenant

    override fun regenerationLimit(): Int = maxRegenerationsPerReport

    override fun ssePoolSize(): Int = ssePoolSize

    override fun costConfig(): AiCostConfig =
        AiCostConfig(
            sonnetInputPerMillion = cost.sonnetInputPerMillion,
            sonnetOutputPerMillion = cost.sonnetOutputPerMillion,
            sonnetCacheReadPerMillion = cost.sonnetCacheReadPerMillion,
            sonnetCacheWritePerMillion = cost.sonnetCacheWritePerMillion,
            brlUsdRate = cost.brlUsdRate,
        )

    class CostConfig {
        var sonnetInputPerMillion: Double = 3.00
        var sonnetOutputPerMillion: Double = 15.00
        var sonnetCacheReadPerMillion: Double = 0.30
        var sonnetCacheWritePerMillion: Double = 3.75
        var brlUsdRate: Double = 5.80
    }
}
