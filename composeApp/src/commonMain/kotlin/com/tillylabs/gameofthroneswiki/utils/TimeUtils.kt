package com.tillylabs.gameofthroneswiki.utils

import kotlin.time.Clock
import kotlin.time.ExperimentalTime

@OptIn(ExperimentalTime::class)
fun getCurrentTimeMillis(): Long = Clock.System.now().toEpochMilliseconds()
