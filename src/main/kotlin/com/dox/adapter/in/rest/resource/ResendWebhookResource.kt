package com.dox.adapter.`in`.rest.resource

import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.tags.Tag
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestHeader
import org.springframework.web.bind.annotation.RequestMapping

@Tag(name = "Webhooks · Resend", description = "Receptor de eventos do provedor de email")
@RequestMapping("/webhooks/resend")
interface ResendWebhookResource : BaseResource {
    @Operation(summary = "Recebe evento do Resend (delivered, bounced, complained, etc)", security = [])
    @PostMapping
    fun receive(
        @RequestHeader(name = "svix-id", required = false) svixId: String?,
        @RequestHeader(name = "svix-timestamp", required = false) svixTimestamp: String?,
        @RequestHeader(name = "svix-signature", required = false) svixSignature: String?,
        @RequestBody rawBody: String,
    ): ResponseEntity<Void>
}
