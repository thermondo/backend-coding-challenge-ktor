package com.thermondo

import com.thermondo.models.User
import com.thermondo.service.AuthService
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.client.statement.bodyAsText
import io.ktor.http.ContentType
import io.ktor.http.HttpStatusCode
import io.ktor.http.contentType
import io.ktor.serialization.jackson.jackson
import io.ktor.server.testing.testApplication
import io.mockk.coEvery
import io.mockk.every
import io.mockk.mockk
import org.jetbrains.exposed.sql.Database
import org.jetbrains.exposed.sql.Transaction
import org.jetbrains.exposed.sql.transactions.TransactionManager
import kotlin.test.Ignore
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotNull
import kotlin.test.assertTrue

// todo: update, delete, filter notes
class NoteIntegrationTest {

    private val authService = mockk<AuthService>()

    companion object {
        private const val noteTitle = "title"
        private const val noteBody = "body"
        private val tagList = listOf("tag1")
    }

    @Test
    @Ignore
    fun `Create Note`() = testApplication {
        mockDatabase()
        val client = createClient {
            install(ContentNegotiation) {
                jackson()
            }
        }
        coEvery { authService.loginAndGetUser("email", "password") } returns User.new {
            username = "username"
            email = "email@mail.com"
            password = "password"
        }

        val response = client.post("/api/v1/notes") {
            contentType(ContentType.Application.Json)
            setBody(
                mapOf(
                    "note" to mapOf(
                        "title" to noteTitle,
                        "body" to noteBody,
                        "tagList" to tagList
                    )
                )
            )
        }

        assertEquals(HttpStatusCode.OK, response.status)
        response.bodyAsText().also { content ->
            assertNotNull(content)
            assertTrue(content.contains("title"))
            assertTrue(content.contains("body"))
            assertTrue(content.contains("tagList"))
        }
    }

    internal class TestTransactionManager : TransactionManager {
        override var defaultIsolationLevel = 0
        override var defaultRepetitionAttempts = 0
        private val mockedDatabase: Database = mockk(relaxed = true)

        override fun bindTransactionToThread(transaction: Transaction?) {

        }

        override fun currentOrNull(): Transaction {
            return transaction()
        }

        private fun transaction(): Transaction {
            return mockk(relaxed = true) {
                every { db } returns mockedDatabase
            }
        }

        override fun newTransaction(isolation: Int, outerTransaction: Transaction?): Transaction {
            return transaction()
        }

        fun apply() {
            TransactionManager.registerManager(mockedDatabase, this@TestTransactionManager)
            Database.connect({ mockk(relaxed = true) }, null, { this })
        }

        fun reset() {
            TransactionManager.resetCurrent(null)
            TransactionManager.closeAndUnregister(mockedDatabase)
        }
    }

    fun mockDatabase() = TestTransactionManager().apply()
}
