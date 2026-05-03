package com.dox.config

import com.fasterxml.jackson.core.JsonParser
import com.fasterxml.jackson.databind.DeserializationContext
import com.fasterxml.jackson.databind.deser.std.StringDeserializer
import org.jsoup.Jsoup
import org.jsoup.safety.Safelist

class HtmlSanitizingDeserializer : StringDeserializer() {
    override fun deserialize(
        p: JsonParser,
        ctxt: DeserializationContext,
    ): String? {
        val value = super.deserialize(p, ctxt) ?: return null
        return Jsoup.clean(value, Safelist.none())
    }
}
