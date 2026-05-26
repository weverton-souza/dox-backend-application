package com.dox.domain.exception

sealed class StorageException(
    detail: String,
) : BusinessException(detail) {
    class FileSizeExceeded(
        val maxSizeMb: Long,
        val actualSizeBytes: Long,
    ) : StorageException(
            "Arquivo excede o tamanho máximo de ${maxSizeMb}MB (recebido: ${actualSizeBytes / 1024} KB)",
        )

    class MimeTypeNotAllowed(
        val mimeType: String?,
        val allowed: List<String>,
    ) : StorageException(
            "Tipo de arquivo '${mimeType ?: "desconhecido"}' não permitido. Aceitos: ${allowed.joinToString(", ")}",
        )

    class UploadFailed(
        val reason: String,
    ) : StorageException("Falha ao enviar arquivo: $reason")

    class StorageObjectNotFound(
        val key: String,
    ) : StorageException("Objeto '$key' não encontrado no storage")
}
