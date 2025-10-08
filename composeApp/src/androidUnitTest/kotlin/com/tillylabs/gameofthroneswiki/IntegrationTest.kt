package com.tillylabs.gameofthroneswiki

/**
 * Marker annotation for integration tests that make real API calls.
 * These tests are excluded from the default test suite.
 * Run them explicitly with: ./gradlew testDebugUnitTest -Pinclude-integration-tests
 */
@Target(AnnotationTarget.CLASS)
@Retention(AnnotationRetention.RUNTIME)
annotation class IntegrationTest
