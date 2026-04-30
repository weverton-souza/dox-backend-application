package com.dox.application.port.output

data class IssueInvoiceCommand(
    val asaasPaymentId: String,
    val serviceDescription: String? = null,
)

data class IssueInvoiceResult(
    val asaasInvoiceId: String,
    val status: String,
    val pdfUrl: String?,
    val xmlUrl: String?,
)

interface InvoiceIssuerPort {
    fun isEnabled(): Boolean

    fun issueInvoice(command: IssueInvoiceCommand): IssueInvoiceResult
}
