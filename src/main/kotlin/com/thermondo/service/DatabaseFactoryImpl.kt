package com.thermondo.service

import com.thermondo.models.NoteTags
import com.thermondo.models.Notes
import com.thermondo.models.Tags
import com.thermondo.models.Users
import com.zaxxer.hikari.HikariConfig
import com.zaxxer.hikari.HikariDataSource
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.jetbrains.exposed.sql.Database
import org.jetbrains.exposed.sql.SchemaUtils.create
import org.jetbrains.exposed.sql.SchemaUtils.drop
import org.jetbrains.exposed.sql.transactions.transaction

interface DatabaseFactory {
    fun init()

    suspend fun <T> dbQuery(block: () -> T): T

    suspend fun drop()
}

class DatabaseFactoryImpl : DatabaseFactory {

    override fun init() {
        Database.connect(hikari())
        transaction {
            create(Users, Notes, Tags, NoteTags)
        }
    }

    private fun hikari(): HikariDataSource {
        val config = HikariConfig().apply {
            driverClassName = "org.h2.Driver"
            jdbcUrl = "jdbc:h2:mem:~test"
            maximumPoolSize = 3
            isAutoCommit = false
            transactionIsolation = "TRANSACTION_READ_COMMITTED"
        }
        return HikariDataSource(config)
    }

    override suspend fun <T> dbQuery(block: () -> T): T = withContext(Dispatchers.IO) {
        transaction { block() }
    }

    override suspend fun drop() {
        dbQuery { drop(Users, Notes, Tags, NoteTags) }
    }
}
