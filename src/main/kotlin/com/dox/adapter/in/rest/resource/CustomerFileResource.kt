package com.dox.adapter.`in`.rest.resource

import com.dox.adapter.`in`.rest.dto.file.CustomerFileDownloadUrlResponse
import com.dox.adapter.`in`.rest.dto.file.CustomerFileResponse
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.tags.Tag
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.DeleteMapping
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.multipart.MultipartFile
import java.util.UUID

@Tag(name = "Arquivos do Cliente", description = "Upload, listagem e download de anexos de clientes")
@RequestMapping("/customers/{customerId}/files")
interface CustomerFileResource : BaseResource {
    @Operation(summary = "Listar arquivos do cliente")
    @GetMapping
    fun list(
        @PathVariable customerId: UUID,
        @RequestParam(required = false) category: String?,
    ): ResponseEntity<List<CustomerFileResponse>>

    @Operation(summary = "Fazer upload de arquivo (multipart/form-data)")
    @PostMapping(consumes = ["multipart/form-data"])
    fun upload(
        @PathVariable customerId: UUID,
        @RequestParam("file") file: MultipartFile,
        @RequestParam(required = false) category: String?,
    ): ResponseEntity<CustomerFileResponse>

    @Operation(summary = "Buscar metadados do arquivo")
    @GetMapping("/{fileId}")
    fun getById(
        @PathVariable customerId: UUID,
        @PathVariable fileId: UUID,
    ): ResponseEntity<CustomerFileResponse>

    @Operation(summary = "Obter URL assinada de download (TTL curto)")
    @GetMapping("/{fileId}/download-url")
    fun getDownloadUrl(
        @PathVariable customerId: UUID,
        @PathVariable fileId: UUID,
    ): ResponseEntity<CustomerFileDownloadUrlResponse>

    @Operation(summary = "Excluir arquivo (soft delete + remove do storage)")
    @DeleteMapping("/{fileId}")
    fun delete(
        @PathVariable customerId: UUID,
        @PathVariable fileId: UUID,
    ): ResponseEntity<Void>
}
