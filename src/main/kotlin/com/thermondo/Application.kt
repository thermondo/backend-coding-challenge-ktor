package com.thermondo

import com.fasterxml.jackson.core.util.DefaultIndenter
import com.fasterxml.jackson.core.util.DefaultPrettyPrinter
import com.fasterxml.jackson.databind.SerializationFeature
import com.thermondo.config.api
import com.thermondo.config.cors
import com.thermondo.config.jwtConfig
import com.thermondo.config.statusPages
import com.thermondo.config.swaggerUi
import com.thermondo.service.DatabaseFactory
import com.thermondo.util.SimpleJWT
import io.github.smiley4.ktorswaggerui.SwaggerUI
import io.ktor.serialization.jackson.jackson
import io.ktor.server.application.Application
import io.ktor.server.application.install
import io.ktor.server.auth.Authentication
import io.ktor.server.plugins.callloging.CallLogging
import io.ktor.server.plugins.contentnegotiation.ContentNegotiation
import io.ktor.server.plugins.cors.routing.CORS
import io.ktor.server.plugins.defaultheaders.DefaultHeaders
import io.ktor.server.plugins.statuspages.StatusPages
import io.ktor.server.routing.routing
import org.koin.ktor.ext.inject
import org.koin.ktor.plugin.Koin
import org.slf4j.event.Level

fun main(args: Array<String>): Unit = io.ktor.server.netty.EngineMain.main(args)

fun Application.module(testing: Boolean = false) {

    install(Koin) {
        modules(serviceKoinModule)
        modules(databaseKoinModule)
    }

    install(ContentNegotiation) {
        jackson {
            configure(SerializationFeature.INDENT_OUTPUT, true)
            setDefaultPrettyPrinter(DefaultPrettyPrinter().apply {
                indentArraysWith(DefaultPrettyPrinter.FixedSpaceIndenter.instance)
                indentObjectsWith(DefaultIndenter("  ", "\n"))
            })
        }
    }

    install(DefaultHeaders)

    install(CORS) {
        cors()
    }

    install(CallLogging) {
        level = Level.INFO
    }

    val simpleJWT = SimpleJWT(
        secret = environment.config.property("jwt.secret").getString()
    )

    install(Authentication) {
        jwtConfig(simpleJWT)
    }

    val factory: DatabaseFactory by inject()
    factory.init()

    install(StatusPages) {
        statusPages()
    }

    routing {
        api(simpleJWT)
    }

    install(SwaggerUI) {
        swaggerUi()
    }
}
