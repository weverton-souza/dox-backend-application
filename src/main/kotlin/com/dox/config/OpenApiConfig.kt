package com.dox.config

import io.swagger.v3.oas.models.Components
import io.swagger.v3.oas.models.OpenAPI
import io.swagger.v3.oas.models.Operation
import io.swagger.v3.oas.models.info.Info
import io.swagger.v3.oas.models.media.Content
import io.swagger.v3.oas.models.media.MediaType
import io.swagger.v3.oas.models.media.Schema
import io.swagger.v3.oas.models.security.SecurityRequirement
import io.swagger.v3.oas.models.security.SecurityScheme
import org.springdoc.core.customizers.GlobalOpenApiCustomizer
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

private const val PROBLEM_DETAIL_SCHEMA = "ProblemDetail"
private const val PROBLEM_DETAIL_REF = "#/components/schemas/$PROBLEM_DETAIL_SCHEMA"

private data class ErrorExample(
    val errorCode: String,
    val title: String,
    val detail: String,
)

private val ERROR_EXAMPLES =
    mapOf(
        "400" to ErrorExample("VALIDATION_ERROR", "Erro de validação", "Os campos enviados não são válidos"),
        "401" to ErrorExample("UNAUTHORIZED", "Não autenticado", "Token inválido ou expirado"),
        "403" to ErrorExample("ACCESS_DENIED", "Sem permissão", "Você não tem permissão para acessar este recurso"),
        "404" to ErrorExample("NOT_FOUND", "Recurso não encontrado", "O recurso solicitado não foi encontrado"),
        "409" to ErrorExample("DUPLICATE_RESOURCE", "Conflito de recurso", "Já existe um recurso com esse identificador"),
        "422" to ErrorExample("BUSINESS_RULE_VIOLATION", "Regra de negócio violada", "A operação não pode ser concluída no estado atual"),
        "500" to ErrorExample("INTERNAL_ERROR", "Erro interno", "Erro inesperado. Referência: 9285c573-93b3-4aa6-b2fc-0120b661f24a"),
    )

private val FALLBACK_4XX = ErrorExample("CLIENT_ERROR", "Erro do cliente", "Requisição inválida")
private val FALLBACK_5XX = ErrorExample("INTERNAL_ERROR", "Erro interno", "Erro inesperado no servidor")

@Configuration
class OpenApiConfig {
    @Bean
    fun customOpenAPI(): OpenAPI {
        val schemeName = "bearerAuth"

        return OpenAPI()
            .info(
                Info()
                    .title("Dox - Pense diferente. Faça melhor")
                    .description(
                        "DOX é uma plataforma para profissionais autônomos e equipes que precisam entregar relatórios técnicos com qualidade. " +
                            "Reúne em um só lugar a gestão de clientes, criação de relatórios com apoio de IA, formulários customizáveis, agenda, " +
                            "biblioteca de conteúdo, assinaturas e cobrança.\n\n" +
                            "Atende múltiplas áreas profissionais: saúde, jurídico, educação, engenharia, contabilidade, ambiental, " +
                            "segurança do trabalho, tecnologia, nutrição, veterinária, perícia, serviço social e agronomia.",
                    )
                    .version("1.0.0"),
            )
            .addSecurityItem(SecurityRequirement().addList(schemeName))
            .components(
                Components()
                    .addSecuritySchemes(
                        schemeName,
                        SecurityScheme()
                            .type(SecurityScheme.Type.HTTP)
                            .scheme("bearer")
                            .bearerFormat("JWT"),
                    ),
            )
    }

    @Bean
    fun problemDetailResponsesCustomizer(): GlobalOpenApiCustomizer =
        GlobalOpenApiCustomizer { openApi ->
            val components = openApi.components ?: Components().also { openApi.components = it }
            val schemas = components.schemas ?: linkedMapOf<String, Schema<*>>().also { components.schemas = it }
            schemas.putIfAbsent(PROBLEM_DETAIL_SCHEMA, buildProblemDetailSchema())

            openApi.paths?.values?.forEach { pathItem ->
                pathItem.readOperations().forEach { operation ->
                    overrideErrorResponses(operation)
                }
            }
        }

    private fun buildProblemDetailSchema(): Schema<*> {
        val schema = Schema<Any>()
        schema.type = "object"
        schema.description = "RFC 7807 Problem Details com extensões DOX (errorCode, timestamp e propriedades específicas por exceção)"
        schema.properties =
            linkedMapOf(
                "type" to Schema<String>().type("string").example("urn:dox:error:VALIDATION_ERROR"),
                "title" to Schema<String>().type("string").example("Erro de validação"),
                "status" to Schema<Int>().type("integer").format("int32").example(400),
                "detail" to Schema<String>().type("string").example("Os campos enviados não são válidos"),
                "instance" to Schema<String>().type("string").example("/content-library"),
                "errorCode" to Schema<String>().type("string").example("VALIDATION_ERROR"),
                "timestamp" to Schema<String>().type("string").format("date-time").example("2026-04-30T16:41:36.500Z"),
            )
        schema.required = listOf("type", "title", "status", "detail")
        return schema
    }

    private fun overrideErrorResponses(operation: Operation) {
        val responses = operation.responses ?: return
        responses.forEach { (code, response) ->
            if (code.startsWith("4") || code.startsWith("5")) {
                val statusInt = code.toIntOrNull() ?: return@forEach
                val example = resolveExample(code, statusInt)
                response.content =
                    Content().addMediaType(
                        "application/json",
                        MediaType()
                            .schema(Schema<Any>().`$ref`(PROBLEM_DETAIL_REF))
                            .example(example),
                    )
            }
        }
    }

    private fun resolveExample(
        code: String,
        statusInt: Int,
    ): Map<String, Any> {
        val template =
            ERROR_EXAMPLES[code]
                ?: if (code.startsWith("5")) FALLBACK_5XX else FALLBACK_4XX
        return linkedMapOf(
            "type" to "urn:dox:error:${template.errorCode}",
            "title" to template.title,
            "status" to statusInt,
            "detail" to template.detail,
            "instance" to "/content-library",
            "errorCode" to template.errorCode,
            "timestamp" to "2026-04-30T16:41:36.500Z",
        )
    }
}
