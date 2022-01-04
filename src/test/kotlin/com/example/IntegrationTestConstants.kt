package com.example

import org.springframework.http.MediaType

const val DATA_JSON_PATH = "$.data"
const val GRAPHQL_ENDPOINT = "/graphql"
val GRAPHQL_MEDIA_TYPE = MediaType("application", "graphql")