package com.thermondo.config

import com.thermondo.util.SimpleJWT
import io.ktor.server.auth.AuthenticationConfig
import io.ktor.server.auth.UserIdPrincipal
import io.ktor.server.auth.jwt.jwt

fun AuthenticationConfig.jwtConfig(simpleJWT: SimpleJWT) {

    jwt {
        authSchemes("Bearer")
        verifier(simpleJWT.verifier)
        validate {
            UserIdPrincipal(it.payload.getClaim("id").asString())
        }
    }
}
