package com.dox.adapter.out.email.template

import com.dox.domain.email.EmailPalettes
import com.dox.domain.email.EmailTemplateId
import com.dox.domain.email.RenderedEmail
import org.springframework.stereotype.Component
import org.thymeleaf.TemplateEngine
import org.thymeleaf.context.Context
import java.util.Locale

@Component
class EmailTemplateRenderer(
    private val templateEngine: TemplateEngine,
) {
    fun render(
        template: EmailTemplateId,
        subject: String,
        variables: Map<String, Any?>,
    ): RenderedEmail {
        val context =
            Context(Locale.forLanguageTag("pt-BR")).apply {
                setVariable("subject", subject)
                setVariable("palette", EmailPalettes.resolve(template))
                setVariable("category", template.category.name)
                setVariable("followupLevel", template.followupLevel?.name)
                variables.forEach { (key, value) -> setVariable(key, value) }
            }

        val html = templateEngine.process("email/${template.templateName}", context)
        val text = htmlToPlainText(html)

        return RenderedEmail(
            subject = subject,
            html = html,
            text = text,
        )
    }

    private fun htmlToPlainText(html: String): String {
        val noTags =
            html
                .replace(Regex("(?is)<style.*?</style>"), "")
                .replace(Regex("(?is)<script.*?</script>"), "")
                .replace(Regex("(?is)<head.*?</head>"), "")
                .replace(Regex("(?i)<br\\s*/?>"), "\n")
                .replace(Regex("(?i)</p>"), "\n\n")
                .replace(Regex("(?i)</div>"), "\n")
                .replace(Regex("(?i)</tr>"), "\n")
                .replace(Regex("<[^>]+>"), "")
        return noTags
            .replace("&nbsp;", " ")
            .replace("&amp;", "&")
            .replace("&lt;", "<")
            .replace("&gt;", ">")
            .replace("&quot;", "\"")
            .replace("&#39;", "'")
            .replace(Regex("[ \\t]+"), " ")
            .replace(Regex("\\n{3,}"), "\n\n")
            .trim()
    }
}
