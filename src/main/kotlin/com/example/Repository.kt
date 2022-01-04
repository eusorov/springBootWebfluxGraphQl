package com.example

import com.example.graphql.Delivery
import com.example.graphql.DeliveryProgressType
import graphql.ErrorClassification
import graphql.GraphQLError
import graphql.language.SourceLocation
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.decodeFromStream
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Value
import org.springframework.core.io.Resource
import org.springframework.stereotype.Component
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono
import java.time.LocalDateTime
import javax.annotation.PostConstruct


/**
 * Repository with in memory immutable Arraylist of delivery objects
 */

@OptIn(ExperimentalSerializationApi::class)
@Component
class Repository {
    final val logger = LoggerFactory.getLogger(this::class.java);

    lateinit var deliveries: List<Delivery>

    @Value("classpath:/deliveries.json")
    final lateinit var resFile: Resource

    @PostConstruct
    fun readJson() {
        deliveries = Json {
            ignoreUnknownKeys = true
        }.decodeFromStream<List<Delivery>>(resFile.inputStream)

        logger.debug("deliveries size = {$deliveries.size}")
    }

    /**
     * find all without any filter
     */
    fun findAll(): Flux<Delivery> {
        return Flux.fromIterable(deliveries)
    }

    /**
     * find all with filter
     */
    fun findAllByFilter(delivery: Delivery?): Flux<Delivery> {
        val filtered = deliveries.filter { d ->
            if (delivery != null) {
                val pred1 = if (delivery.deliveryId != null) d.deliveryId == delivery.deliveryId else true
                val pred2 = if (delivery.product != null) d.product == delivery.product else true
                val pred3 = if (delivery.deliveryProgressType != null) d.deliveryProgressType == delivery.deliveryProgressType else true
                val pred4 = if (delivery.receivedOn != null) d.receivedOn == delivery.receivedOn else true
                return@filter pred1 && pred2 && pred3 && pred4
            } else {
                return@filter true
            }
        }

        return Flux.fromIterable(filtered)
    }

    fun findOne(id: Int): Mono<Delivery?> {
        return Mono.just(id).mapNotNull {
            deliveries.find { d -> d.deliveryId == id }
        }
    }

    @Synchronized
    fun deliveryReceivedOn(id: Int, receivedOn: LocalDateTime): Mono<Delivery?> {
        val deliveryUpdated = findOne(id).mapNotNull { //            {
            it?.receivedOn = receivedOn
            it?.deliveryProgressType = DeliveryProgressType.RECEIVED
            return@mapNotNull it
        }
        return deliveryUpdated
    }

    @Synchronized
    fun save(delivery: Delivery): Mono<Int> {
        val maxId = deliveries.map { it.deliveryId }.reduce { acc, cur -> if ((cur ?: 0) > (acc ?: 0)) cur else acc }
        val nexId = (maxId?:0) + 1
        val newDelivery = delivery.copy(deliveryId = nexId)

        deliveries = deliveries.plus(newDelivery)
        return Mono.just(nexId)
    }
}
