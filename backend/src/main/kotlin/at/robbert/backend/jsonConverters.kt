package at.robbert.backend

import at.robbert.redirector.data.Link
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.kotlin.readValue
import io.r2dbc.postgresql.codec.Json
import io.r2dbc.spi.ConnectionFactory
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.core.convert.converter.Converter
import org.springframework.data.convert.ReadingConverter
import org.springframework.data.convert.WritingConverter
import org.springframework.data.r2dbc.config.AbstractR2dbcConfiguration
import org.springframework.data.r2dbc.convert.R2dbcCustomConversions


@Configuration
class ReactivePostgresConfiguration(
    @Qualifier("connectionFactory") val connectionFactory: ConnectionFactory,
    val objectMapper: ObjectMapper
) :
    AbstractR2dbcConfiguration() {
    override fun connectionFactory(): ConnectionFactory = TODO()

    @Bean
    override fun r2dbcCustomConversions(): R2dbcCustomConversions {
        val converters = mutableListOf<Converter<*, *>>()
        converters.add(JsonToLinkListConverter(objectMapper))
        converters.add(LinkListToJsonConverter(objectMapper))
        return R2dbcCustomConversions(storeConversions, converters)
    }
}

@ReadingConverter
class JsonToLinkListConverter(private val objectMapper: ObjectMapper) : Converter<Json, List<Link>> {
    override fun convert(source: Json): List<Link> {
        return objectMapper.readValue(source.asString())
    }
}

@WritingConverter
class LinkListToJsonConverter(private val objectMapper: ObjectMapper) : Converter<List<Link>, Json> {
    override fun convert(source: List<Link>): Json {
        return Json.of(objectMapper.writeValueAsString(source))
    }
}
