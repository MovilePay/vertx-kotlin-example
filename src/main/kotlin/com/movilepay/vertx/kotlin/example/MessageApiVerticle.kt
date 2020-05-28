package com.movilepay.vertx.kotlin.example

import io.netty.handler.codec.http.HttpResponseStatus
import io.vertx.core.Handler
import io.vertx.core.eventbus.Message
import io.vertx.core.eventbus.ReplyException
import io.vertx.core.http.HttpHeaders
import io.vertx.core.http.HttpServer
import io.vertx.core.http.HttpServerResponse
import io.vertx.core.json.JsonObject
import io.vertx.ext.web.Router
import io.vertx.ext.web.RoutingContext
import io.vertx.ext.web.handler.BodyHandler
import io.vertx.kotlin.core.eventbus.requestAwait
import io.vertx.kotlin.core.http.listenAwait
import io.vertx.kotlin.core.json.json
import io.vertx.kotlin.core.json.jsonObjectOf
import io.vertx.kotlin.core.json.obj
import io.vertx.kotlin.coroutines.CoroutineVerticle
import io.vertx.kotlin.coroutines.dispatcher
import kotlinx.coroutines.launch

class MessageApiVerticle : CoroutineVerticle() {

    override suspend fun start() {
        val router: Router = Router.router(vertx)
        router.route().handler(globalHandler)
        router.get("/messages").handler(buildMessageByQueryString)
        router.post("/messages").handler(BodyHandler.create()).handler(buildMessageByPOST)
        router.get("/messages/verticle").handler(buildMessageByAnotherVerticle)
        val server: HttpServer = vertx
            .createHttpServer()
            .requestHandler(router)
            .listenAwait(8080)
        println("<<< Server is running at port ${server.actualPort()}")
    }

    private val globalHandler: Handler<RoutingContext> = Handler { routingContext: RoutingContext ->
        val response: HttpServerResponse = routingContext.response()
        response.putHeader(HttpHeaders.CONTENT_TYPE.toString(), "application/json")
        routingContext.next()
    }

    private val buildMessageByQueryString: Handler<RoutingContext> = Handler { routingContext: RoutingContext ->
        when (val name: String? = routingContext.queryParam("name").firstOrNull()) {
            null -> {
                routingContext.response()
                    .setStatusCode(HttpResponseStatus.BAD_REQUEST.code())
                    .end(
                        json {
                            obj(
                                "error_message" to "You must provide 'name' query string parameter"
                            ).encode()
                        }
                    )
            }
            else -> {
                routingContext.response()
                    .setStatusCode(HttpResponseStatus.OK.code())
                    .end(
                        json {
                            obj(
                                "message" to "Hello, $name!"
                            ).encode()
                        }
                    )
            }
        }
    }

    private val buildMessageByPOST: Handler<RoutingContext> = Handler { routingContext: RoutingContext ->
        when (val name: String? = routingContext.bodyAsJson.getString("name")) {
            null -> {
                routingContext.response()
                    .setStatusCode(HttpResponseStatus.BAD_REQUEST.code())
                    .end(
                        json {
                            obj(
                                "error_message" to "You must provide 'name' query string parameter"
                            ).encode()
                        }
                    )
            }
            else -> {
                routingContext.response()
                    .setStatusCode(HttpResponseStatus.OK.code())
                    .end(
                        json {
                            obj(
                                "message" to "Hello, $name!"
                            ).encode()
                        }
                    )
            }
        }
    }

    private val buildMessageByAnotherVerticle: Handler<RoutingContext> = Handler { routingContext: RoutingContext ->
        launch(vertx.dispatcher()) {
            try {
                val name: String? = routingContext.queryParam("name").firstOrNull()
                val message: Message<JsonObject> = vertx.eventBus().requestAwait(
                    EVENT_BUS_ADDRESS,
                    jsonObjectOf(
                        "name" to name
                    )
                )
                routingContext.response()
                    .setStatusCode(HttpResponseStatus.OK.code())
                    .end(message.body().toBuffer())
            } catch (re: ReplyException) {
                when (re.failureCode()) {
                    Int.MAX_VALUE ->
                        routingContext.response()
                            .setStatusCode(HttpResponseStatus.BAD_REQUEST.code())
                            .end(
                                json {
                                    obj(
                                        "error_message" to "You must provide 'name' query string parameter. The name also must have 5 or more characters"
                                    ).encode()
                                }
                            )
                    else ->
                        routingContext.response()
                            .setStatusCode(HttpResponseStatus.INTERNAL_SERVER_ERROR.code())
                            .end(
                                json {
                                    obj(
                                        "error_message" to re.message
                                    ).encode()
                                }
                            )
                }
            }
        }
    }
}
