package com.movilepay.vertx.kotlin.example

import io.vertx.core.Vertx
import io.vertx.kotlin.core.deployVerticleAwait

suspend fun main() {
    val vertx: Vertx = Vertx.vertx()
    val apiMessageVerticleID: String = vertx.deployVerticleAwait(
        MessageApiVerticle::class.java.name
    )
    println("Verticle ${MessageApiVerticle::class.java.name} was deployed with ID $apiMessageVerticleID")
    val anotherVerticleID: String = vertx.deployVerticleAwait(
        AnotherVerticle::class.java.name
    )
    println("Verticle ${AnotherVerticle::class.java.name} was deployed with ID $anotherVerticleID")
}