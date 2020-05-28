package com.movilepay.vertx.kotlin.example

import io.vertx.core.Handler
import io.vertx.core.eventbus.Message
import io.vertx.core.json.JsonObject
import io.vertx.kotlin.core.json.jsonObjectOf
import io.vertx.kotlin.coroutines.CoroutineVerticle

const val EVENT_BUS_ADDRESS: String = "ANOTHER_VERTICLE_HANDLE_MESSAGE"

class AnotherVerticle : CoroutineVerticle() {

    override suspend fun start() {
        vertx.eventBus().consumer(EVENT_BUS_ADDRESS, handleMessage)
    }

    private val handleMessage: Handler<Message<JsonObject>> = Handler { message: Message<JsonObject> ->
        val name: String = message.body().getString("name", "")
        if (name.isNullOrEmpty() || name.length < 5) {
            message.fail(Int.MAX_VALUE, "The name cannot be null, empty or have less than 5 characters")
        } else {
            message.reply(
                jsonObjectOf(
                    "message" to "Hello, $name!"
                )
            )
        }
    }

}