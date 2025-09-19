package com.tillylabs.gameofthroneswiki.models

import kotlinx.serialization.Serializable

@Serializable
data class Book(
    val url: String,
    val name: String,
    val isbn: String,
    val authors: List<String>,
    val numberOfPages: Int,
    val publisher: String,
    val country: String,
    val mediaType: String,
    val released: String,
    val characters: List<String>,
    val povCharacters: List<String>,
)
