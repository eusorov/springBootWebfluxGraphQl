package com.example

import com.example.graphql.DeliveryProgressType
import org.hamcrest.Matchers.*
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestInstance
import org.junit.jupiter.api.TestInstance.Lifecycle.PER_CLASS
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.http.MediaType.APPLICATION_JSON
import org.springframework.test.web.reactive.server.WebTestClient

@SpringBootTest
@AutoConfigureWebTestClient
@TestInstance(PER_CLASS)
class QueryIT(@Autowired private val testClient: WebTestClient) {

    @Test
    fun `verify id query`() {
        val query = "findById( id :101) { deliveryId }"
        val expectedData = "101"

        testClient.post()
                .uri(GRAPHQL_ENDPOINT)
                .accept(APPLICATION_JSON)
                .contentType(GRAPHQL_MEDIA_TYPE)
                .bodyValue("query { $query }")
                .exchange()
                .expectStatus().isOk
                .expectBody()
                .jsonPath(DATA_JSON_PATH.plus(".findById.deliveryId")).isEqualTo(expectedData)
    }

    @Test
    fun `verify findAll query`() {
        val query = "findAll { deliveryId, deliveryProgressType }"
        val expectedId = "101"
        val expectedDeliveryType = DeliveryProgressType.WAREHOUSE.toString()

        testClient.post()
                .uri(GRAPHQL_ENDPOINT)
                .accept(APPLICATION_JSON)
                .contentType(GRAPHQL_MEDIA_TYPE)
                .bodyValue("query { $query }")
                .exchange()
                .expectStatus().isOk
                .expectBody()
                .jsonPath(DATA_JSON_PATH.plus(".findAll")).value(anyOf(nullValue(), instanceOf(List::class.java)))
                .jsonPath(DATA_JSON_PATH.plus(".findAll.length()")).isNumber()
                .jsonPath(DATA_JSON_PATH.plus(".findAll.[0].deliveryId")).isEqualTo(expectedId)
                .jsonPath(DATA_JSON_PATH.plus(".findAll.[0].deliveryProgressType")).isEqualTo(expectedDeliveryType)
    }

    @Test
    fun `verify findAllByFilter query`() {
        val query = "findAllByFilter ( delivery: { product: \"Bananas\" } ) { deliveryId, deliveryProgressType }"
        val expectedLength = 2
        val expectedIds = listOf(101, 104)

        testClient.post()
                .uri(GRAPHQL_ENDPOINT)
                .accept(APPLICATION_JSON)
                .contentType(GRAPHQL_MEDIA_TYPE)
                .bodyValue("query { $query }")
                .exchange()
                .expectStatus().isOk
                .expectBody()
                .jsonPath(DATA_JSON_PATH.plus(".findAllByFilter")).value(anyOf(nullValue(), instanceOf(List::class.java)))
                .jsonPath(DATA_JSON_PATH.plus(".findAllByFilter.length()")).isEqualTo(expectedLength)
                .jsonPath(DATA_JSON_PATH.plus(".findAllByFilter.[0].deliveryId")).isEqualTo(expectedIds[0].toString())
                .jsonPath(DATA_JSON_PATH.plus(".findAllByFilter.[1].deliveryId")).isEqualTo(expectedIds[1].toString())
    }
}