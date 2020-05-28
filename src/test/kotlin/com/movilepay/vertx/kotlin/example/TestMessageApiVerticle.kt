package com.movilepay.vertx.kotlin.example

import io.vertx.core.Vertx
import io.vertx.junit5.VertxExtension
import io.vertx.junit5.VertxTestContext
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith

@ExtendWith(VertxExtension::class)
class TestMessageApiVerticle {

  @BeforeEach
  fun deploy_verticle(vertx: Vertx, testContext: VertxTestContext) {
    vertx.deployVerticle(MessageApiVerticle(), testContext.succeeding<String> { _ -> testContext.completeNow() })
  }

  @Test
  fun verticle_deployed(vertx: Vertx, testContext: VertxTestContext) {
    testContext.completeNow()
  }
}
