package com.dox.config

import com.fasterxml.jackson.core.JsonGenerator
import com.fasterxml.jackson.databind.DeserializationFeature
import com.fasterxml.jackson.databind.MapperFeature
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.databind.SerializationFeature
import com.fasterxml.jackson.databind.SerializerProvider
import com.fasterxml.jackson.databind.json.JsonMapper
import com.fasterxml.jackson.databind.module.SimpleModule
import com.fasterxml.jackson.databind.ser.std.StdSerializer
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule
import com.fasterxml.jackson.module.kotlin.kotlinModule
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
class JacksonConfig {
    @Bean
    fun objectMapper(): ObjectMapper {
        val enumModule = SimpleModule().apply {
            addSerializer(Enum::class.java, LowercaseEnumSerializer())
        }

        val sanitizingModule = SimpleModule().apply {
            addDeserializer(String::class.java, HtmlSanitizingDeserializer())
        }

        return JsonMapper.builder()
            .addModule(JavaTimeModule())
            .addModule(kotlinModule())
            .addModule(enumModule)
            .addModule(sanitizingModule)
            .disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS)
            .disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES)
            .enable(MapperFeature.ACCEPT_CASE_INSENSITIVE_ENUMS)
            .build()
    }
}

class LowercaseEnumSerializer : StdSerializer<Enum<*>>(Enum::class.java) {
    override fun serialize(value: Enum<*>, gen: JsonGenerator, provider: SerializerProvider) {
        gen.writeString(value.name.lowercase())
    }
}
