package com.dox.application.port.output

data class AiCostConfig(
    val sonnetInputPerMillion: Double,
    val sonnetOutputPerMillion: Double,
    val sonnetCacheReadPerMillion: Double,
    val sonnetCacheWritePerMillion: Double,
    val brlUsdRate: Double
)

interface AiConfigPort {
    fun isEnabled(): Boolean
    fun defaultModel(): String
    fun concurrencyLimit(): Int
    fun regenerationLimit(): Int
    fun costConfig(): AiCostConfig
}
