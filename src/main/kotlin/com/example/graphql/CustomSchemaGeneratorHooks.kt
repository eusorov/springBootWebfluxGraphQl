package com.example.graphql

import com.expediagroup.graphql.generator.hooks.SchemaGeneratorHooks
import graphql.language.StringValue
import graphql.schema.Coercing
import graphql.schema.GraphQLScalarType
import graphql.schema.GraphQLType
import org.springframework.stereotype.Component
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import kotlin.reflect.KClass
import kotlin.reflect.KType

@Component
class CustomSchemaGeneratorHooks : SchemaGeneratorHooks {

    override fun willGenerateGraphQLType(type: KType): GraphQLType? = when (type.classifier as? KClass<*>) {
        LocalDateTime::class -> graphqlLocalDateTime
        else -> null
    }

    val graphqlLocalDateTime = GraphQLScalarType.newScalar()
            .name("LocalDateTime")
            .description("A type for LocalDateTime")
            .coercing(localDateCoercing)
            .build()

    object localDateCoercing : Coercing<LocalDateTime, String> {
        override fun parseValue(input: Any): LocalDateTime {
            return LocalDateTime.parse(serialize(input), DateTimeFormatter.ISO_DATE_TIME)
        }

        override fun parseLiteral(input: Any): LocalDateTime {
            val v = (input as? StringValue)?.value
            return LocalDateTime.parse(v, DateTimeFormatter.ISO_DATE_TIME)
        }

        override fun serialize(dataFetcherResult: Any): String {
            return dataFetcherResult.toString()
        }
    }
}


