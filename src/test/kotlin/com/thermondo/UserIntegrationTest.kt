package com.thermondo

import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.client.statement.bodyAsText
import io.ktor.http.ContentType
import io.ktor.http.HttpStatusCode
import io.ktor.http.contentType
import io.ktor.serialization.jackson.jackson
import io.ktor.server.testing.testApplication
import java.time.LocalDateTime
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotNull
import kotlin.test.assertTrue

class UserIntegrationTest {

    companion object {
        private val username = "u${LocalDateTime.now().nano}"
        private val email = "$username@mail.com"
        private const val password = "pass1234"
    }

    @Test
    fun `Register User`() = testApplication {
        val client = createClient {
            install(ContentNegotiation) {
                jackson()
            }
        }

        val response = client.post("/api/v1/users") {
            contentType(ContentType.Application.Json)
            setBody(
                mapOf(
                    "user" to mapOf(
                        "email" to email,
                        "password" to password,
                        "username" to username
                    )
                )
            )
        }

        assertEquals(HttpStatusCode.OK, response.status)
        response.bodyAsText().also { content ->
            assertNotNull(content)
            assertTrue(content.contains("email"))
            assertTrue(content.contains("username"))
            assertTrue(content.contains("token"))
        }
    }


    @Test
    fun `Login User`() = testApplication {
        val client = createClient {
            install(ContentNegotiation) {
                jackson()
            }
        }

        val response = client.post("/api/v1/users/login") {
            contentType(ContentType.Application.Json)
            setBody(
                mapOf(
                    "user" to mapOf(
                        "email" to email,
                        "password" to password
                    )
                )
            )
        }

        assertEquals(HttpStatusCode.OK, response.status)
        response.bodyAsText().also { content ->
            assertNotNull(content)
            assertTrue(content.contains("email"))
            assertTrue(content.contains("username"))
            assertTrue(content.contains("token"))
        }
    }
}
