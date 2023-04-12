package com.thermondo.config

import com.thermondo.api.auth
import com.thermondo.api.note
import com.thermondo.service.AuthService
import com.thermondo.service.DatabaseFactory
import com.thermondo.service.NoteService
import com.thermondo.util.SimpleJWT
import io.github.smiley4.ktorswaggerui.dsl.route
import io.ktor.server.routing.Routing
import org.koin.ktor.ext.inject

fun Routing.api(simpleJWT: SimpleJWT) {

    val authService: AuthService by inject()
    val articleService: NoteService by inject()
    val databaseFactory: DatabaseFactory by inject()

    route("/api/v1") {
        auth(authService, simpleJWT)
        note(articleService)
    }
}
