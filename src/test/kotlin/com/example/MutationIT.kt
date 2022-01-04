package com.example

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
class MutationIT(@Autowired private val testClient: WebTestClient) {

    @Test
    fun `verify deliveryReceivedOn mutation`() {
        val query = "deliveryReceivedOn (id :102 , receivedOn: \"2027-01-08T07:17:48.237Z\") { deliveryId, product, receivedOn }"
        val expectedId = "102"
        val expectedProduct = "Saiyans"
        val expectedReceivedOn = "2027-01-08T07:17:48.237"

        testClient.post()
                .uri(GRAPHQL_ENDPOINT)
                .accept(APPLICATION_JSON)
                .contentType(GRAPHQL_MEDIA_TYPE)
                .bodyValue("mutation { $query }")
                .exchange()
                .expectStatus().isOk
                .expectBody()
                .jsonPath(DATA_JSON_PATH.plus(".deliveryReceivedOn.deliveryId")).isEqualTo(expectedId)
                .jsonPath(DATA_JSON_PATH.plus(".deliveryReceivedOn.product")).isEqualTo(expectedProduct)
                .jsonPath(DATA_JSON_PATH.plus(".deliveryReceivedOn.receivedOn")).isEqualTo(expectedReceivedOn)
    }

    @Test
    fun `verify add mutation`() {
        val query = "add ( delivery: {product: \"Testproduct\"})"
        val expectedId = "109"
        val expectedProduct = "Testproduct"

        testClient.post()
                .uri(GRAPHQL_ENDPOINT)
                .accept(APPLICATION_JSON)
                .contentType(GRAPHQL_MEDIA_TYPE)
                .bodyValue("mutation { $query }")
                .exchange()
                .expectStatus().isOk
                .expectBody()
                .jsonPath(DATA_JSON_PATH.plus(".add")).isEqualTo(expectedId)

        val findAllByFilter = "findAllByFilter ( delivery: { product: \"Testproduct\" } ) { deliveryId }"
        testClient.post()
                .uri(GRAPHQL_ENDPOINT)
                .accept(APPLICATION_JSON)
                .contentType(GRAPHQL_MEDIA_TYPE)
                .bodyValue("query { $findAllByFilter }")
                .exchange()
                .expectStatus().isOk
                .expectBody()
                .jsonPath(DATA_JSON_PATH.plus(".findAllByFilter")).value(anyOf(nullValue(), instanceOf(List::class.java)))
                .jsonPath(DATA_JSON_PATH.plus(".findAllByFilter.length()")).isEqualTo(1)
                .jsonPath(DATA_JSON_PATH.plus(".findAllByFilter.[0].deliveryId")).isEqualTo(expectedId)
    }
}