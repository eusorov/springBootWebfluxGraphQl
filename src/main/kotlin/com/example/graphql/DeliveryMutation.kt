package com.example.graphql

import com.expediagroup.graphql.server.operations.Mutation
import com.example.Repository
import kotlinx.coroutines.reactive.awaitSingle
import org.springframework.stereotype.Component
import java.time.LocalDateTime

@Component
class DeliveryMutation(val repository: Repository) : Mutation {

    suspend fun add(delivery: Delivery): Int {
        return repository.save(delivery).awaitSingle()
    }

    suspend fun deliveryReceivedOn(id: Int, receivedOn: LocalDateTime): Delivery? {
        return repository.deliveryReceivedOn(id, receivedOn).awaitSingle()
    }
}