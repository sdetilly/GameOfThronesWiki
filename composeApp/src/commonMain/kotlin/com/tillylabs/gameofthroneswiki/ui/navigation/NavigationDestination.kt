package com.tillylabs.gameofthroneswiki.ui.navigation

import kotlinx.serialization.Serializable

@Serializable
object Books

@Serializable
data class BookDetails(
    val bookUrl: String,
)

@Serializable
object Characters

@Serializable
object Houses
