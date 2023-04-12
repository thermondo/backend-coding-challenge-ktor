package com.thermondo.config

import com.thermondo.util.AuthenticationException
import com.thermondo.util.AuthorizationException
import com.thermondo.util.NoteDoesNotExist
import com.thermondo.util.UserDoesNotExists
import com.thermondo.util.UserExists
import com.thermondo.util.ValidationException
import io.ktor.http.HttpStatusCode
import io.ktor.server.plugins.statuspages.StatusPagesConfig
import io.ktor.server.response.respond

fun StatusPagesConfig.statusPages() {
    exception<Throwable> { call, cause ->
        when (cause) {
            is AuthenticationException -> call.respond(HttpStatusCode.Unauthorized)
            is AuthorizationException -> call.respond(HttpStatusCode.Forbidden)
            is ValidationException -> call.respond(HttpStatusCode.UnprocessableEntity, mapOf("errors" to cause.params))
            is UserExists -> call.respond(
                HttpStatusCode.UnprocessableEntity,
                mapOf("errors" to mapOf("user" to listOf("exists")))
            )

            is UserDoesNotExists, is NoteDoesNotExist -> call.respond(HttpStatusCode.NotFound)
        }
    }
}
