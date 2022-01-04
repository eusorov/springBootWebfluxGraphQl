package com.example.graphql

import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.KSerializer
import kotlinx.serialization.Serializable
import kotlinx.serialization.Serializer
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter


@Serializable
data class Delivery(
        val deliveryId: Int?,
        var product: String?,
        var supplier: String?,
        var quantity: Int?,
        @Serializable(with = DateSerializer::class)
        var expectedDate: LocalDateTime?,
        var expectedWarehouse: String?,
        var deliveryProgressType: DeliveryProgressType? = DeliveryProgressType.WAREHOUSE,
        @Serializable(with = DateSerializer::class)
        var receivedOn: LocalDateTime? =null
) {
    override fun toString(): String {
        return "id={$deliveryId}, deliveryType={$deliveryProgressType}, receivedOn={$receivedOn}"
    }
}

enum class DeliveryProgressType {
    WAREHOUSE, PROGRESS, RECEIVED
}

@OptIn(ExperimentalSerializationApi::class)
@Serializer(forClass = LocalDateTime::class)
object DateSerializer : KSerializer<LocalDateTime> {
    override fun serialize(encoder: Encoder, value: LocalDateTime) {
        return encoder.encodeString(value.toString())
    }

    override fun deserialize(decoder: Decoder): LocalDateTime {
        return LocalDateTime.parse(decoder.decodeString(), DateTimeFormatter.ISO_DATE_TIME);
    }
}

