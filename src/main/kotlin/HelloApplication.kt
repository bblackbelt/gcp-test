package com.example.demo

import io.ktor.application.Application
import io.ktor.application.call
import io.ktor.application.install
import io.ktor.features.CallLogging
import io.ktor.features.ContentNegotiation
import io.ktor.features.DefaultHeaders
import io.ktor.features.StatusPages
import io.ktor.gson.gson
import io.ktor.http.ContentType
import io.ktor.http.HttpStatusCode
import io.ktor.response.respond
import io.ktor.response.respondText
import io.ktor.response.respondTextWriter
import io.ktor.routing.Routing
import io.ktor.routing.get
import io.ktor.routing.routing
import io.ktor.server.engine.embeddedServer
import io.ktor.server.netty.Netty
import io.reactivex.Flowable
import kotlinx.coroutines.delay
import kotlinx.coroutines.reactive.awaitLast
import kotlinx.coroutines.reactive.collect
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import java.util.concurrent.TimeUnit

private val logger: Logger = LoggerFactory.getLogger("test")

fun Application.main() {
    logger.debug("APPSTART")
    install(DefaultHeaders)
    install(CallLogging)
    embeddedServer(Netty, port = 10000) {
        install(ContentNegotiation) {
            gson {
            }
        }
        routing {
            get("/") {
/*                val r = Flowable.range(1, 10)
                    .map { it * it }
                    .delay(300L, TimeUnit.MILLISECONDS)
                    .awaitLast()
                call.respondText("LAST ITEM: $r")*/
                call.respond(Response(status = "OK"))
            }
            get("/utkan") {
                call.respondTextWriter(ContentType.Text.Plain) {
                    val writer = this
                    Flowable.range(1, 10)
                        .map { it * it }
                        .delay(300L, TimeUnit.MILLISECONDS)
                        .collect {
                            logger.debug("collect $it")
                            writer.write("$it, \n")
                            writer.flush()
                            delay(100L)
                        }
                }
            }
        }
    }.start()

        .start(wait = true)
}

data class Response(val status: String)