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
        val enabled: Boolean = false,
        val serviceDescription: String = "Assinatura DOX",
        val municipalServiceId: String = "",
        val municipalServiceCode: String = "",
        val municipalServiceName: String = "",
        val retainIss: Boolean = false,
        val issPercentage: String = "0",
    )
}
