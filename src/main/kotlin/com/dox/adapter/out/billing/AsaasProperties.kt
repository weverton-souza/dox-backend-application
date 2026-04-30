package com.dox.adapter.out.billing

import org.springframework.boot.context.properties.ConfigurationProperties

@ConfigurationProperties(prefix = "asaas")
data class AsaasProperties(
    val apiKey: String = "",
    val webhookToken: String = "",
    val baseUrl: String = "https://sandbox.asaas.com/api/v3",
    val nfse: NfseProperties = NfseProperties(),
) {
    data class NfseProperties(
        val templateId: String = "",
    )
}
