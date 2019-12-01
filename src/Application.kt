package com.example

import com.example.data.Response
import com.example.data.Tasks
import io.ktor.application.*
import io.ktor.response.*
import io.ktor.request.*
import io.ktor.features.*
import io.ktor.routing.*
import io.ktor.http.*
import com.fasterxml.jackson.databind.*
import io.ktor.jackson.*

fun main(args: Array<String>): Unit = io.ktor.server.netty.EngineMain.main(args)

@Suppress("unused") // Referenced in application.conf
@kotlin.jvm.JvmOverloads
fun Application.module(testing: Boolean = true) {
    install(CORS) {
        method(HttpMethod.Options)
        method(HttpMethod.Put)
        method(HttpMethod.Delete)
        method(HttpMethod.Patch)
        header(HttpHeaders.Authorization)
        // header("MyCustomHeader")
        allowCredentials = true
        anyHost() // @TODO: Don't do this in production if possible. Try to limit it.
    }

    install(ContentNegotiation) {
        jackson {
            enable(SerializationFeature.INDENT_OUTPUT)
        }
    }

//    install(CallLogging) {
//        level = org.slf4j.event.Level.INFO
//    }

    routing {
        accept(ContentType.Application.Json) {
            post("/") {
                val post = try {
                    call.receive<Tasks>()
                }
                catch (e: Exception) {
                    call.respond(mapOf("Error" to e.message))
                    return@post
                }
                if (call.request.headers["Accept"] != null) {
                    if (call.request.headers["Accept"]!!.contains("application/json")) {
                        post.tasks?.let {
                            when (val tasks = Tasks.getOrderedTasks(post.tasks!!)) {
                                is Response.Success -> {
                                    call.respond(tasks.data)
                                }
                                is Response.Error -> {
                                    call.respond(mapOf("Error" to tasks.message))
                                }
                            }
                        } ?: run {
                            call.respond(mapOf("Error" to "Error reading tasks"))
                        }
                        return@post
                    }
                }
                post.tasks?.let {
                    when (val tasks = Tasks.getOrderedTasks(post.tasks!!)) {
                        is Response.Success -> {
                            val commands = tasks.data.joinToString(separator = "\n") { "${it.command}" }
                            call.respondText("#!/usr/bin/env bash\n\n${commands}", contentType = ContentType.Text.Plain)
                        }
                        is Response.Error -> {
                            call.respondText("echo \"${tasks.message}\"", contentType = ContentType.Text.Plain)
                        }
                    }
                } ?: run {
                    call.respondText("echo \"Error reading tasks\"", contentType = ContentType.Text.Plain)
                }
            }
        }
    }
}