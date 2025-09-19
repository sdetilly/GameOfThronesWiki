package com.tillylabs.gameofthroneswiki

interface Platform {
    val name: String
}

expect fun getPlatform(): Platform