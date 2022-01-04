package com.example.graphql

import com.expediagroup.graphql.server.operations.Query
import com.example.Repository
import kotlinx.coroutines.reactive.awaitFirst
import kotlinx.coroutines.reactor.awaitSingle
import kotlinx.coroutines.reactor.awaitSingleOrNull
import org.springframework.stereotype.Component
import reactor.core.publisher.Mono

@Component
class DeliveryQuery (val repository: Repository): Query {

    suspend fun findById(id: Int): Delivery? {
        return repository.findOne(id).awaitSingle()
    }

    suspend fun findAll(): List<Delivery> {
        return repository.findAll().collectList().awaitFirst()
    }

    suspend fun findAllByFilter(delivery: Delivery?): List<Delivery> {
        return repository.findAllByFilter(delivery).collectList().awaitFirst()
    }
}