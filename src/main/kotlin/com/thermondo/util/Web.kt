package com.thermondo.util

import io.ktor.server.application.ApplicationCall
import io.ktor.server.auth.UserIdPrincipal
import io.ktor.server.auth.principal


fun ApplicationCall.userId() = principal<UserIdPrincipal>()?.name ?: throw AuthenticationException()

fun ApplicationCall.param(param: String) =
    parameters[param] ?: throw ValidationException(mapOf("param" to listOf("can't be empty")))
