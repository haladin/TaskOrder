package com.example

import com.example.data.Task
import com.example.data.Tasks
import io.ktor.http.*
import com.fasterxml.jackson.databind.*
import kotlin.test.*
import io.ktor.server.testing.*

class ApplicationTests {

    private val tasks = Tasks().also {
        it.tasks = listOf<Task>(
        Task().also {
            it.name = "task-1"
            it.command = "touch /tmp/file1"
        },
        Task().also {
            it.name = "task-2"
            it.command = "cat /tmp/file1"
            it.requires = listOf("task-3")
        },
        Task().also {
            it.name = "task-3"
            it.command = "echo 'Hello World!' > /tmp/file1"
            it.requires = listOf("task-1")
        },
        Task().also {
            it.name = "task-4"
            it.command = "rm /tmp/file1"
            it.requires = listOf("task-2", "task3")
        })
    }

    private val expectedJson = "[ {\n" +
            "  \"name\" : \"task-1\",\n" +
            "  \"command\" : \"touch /tmp/file1\"\n" +
            "}, {\n" +
            "  \"name\" : \"task-2\",\n" +
            "  \"command\" : \"cat /tmp/file1\"\n" +
            "}, {\n" +
            "  \"name\" : \"task-3\",\n" +
            "  \"command\" : \"echo 'Hello World!' > /tmp/file1\"\n" +
            "}, {\n" +
            "  \"name\" : \"task-4\",\n" +
            "  \"command\" : \"rm /tmp/file1\"\n" +
            "} ]"

    private val expectedRaw = "#!/usr/bin/env bash\n" +
            "\n" +
            "touch /tmp/file1\n" +
            "cat /tmp/file1\n" +
            "echo 'Hello World!' > /tmp/file1\n" +
            "rm /tmp/file1"

    @Test
    fun testRoot() {
        withTestApplication({ module(testing = true) }) {
            handleRequest(HttpMethod.Post, "/").apply {
                assertEquals(HttpStatusCode.OK, response.status())
                // assertEquals("HELLO WORLD!", response.content)
            }
        }
    }

    @Test
    fun testJSONOutput() {
        withTestApplication({ module(testing = true) }) {
            handleRequest(HttpMethod.Post, "/") {
                addHeader(HttpHeaders.ContentType, ContentType.Application.Json.toString())
                addHeader(HttpHeaders.Accept, ContentType.Application.Json.toString())
                setBody(ObjectMapper().writeValueAsString(tasks))
            }.apply {
                assertEquals(HttpStatusCode.OK, response.status())
                assertEquals(expectedJson, response.content)
            }
        }
    }

    @Test
    fun testRawOutput() {
        withTestApplication({ module(testing = true) }) {
            handleRequest(HttpMethod.Post, "/") {
                addHeader(HttpHeaders.ContentType, ContentType.Application.Json.toString())
                setBody(ObjectMapper().writeValueAsString(tasks))
            }.apply {
                assertEquals(HttpStatusCode.OK, response.status())
                assertEquals(expectedRaw, response.content)
            }
        }
    }
}
