package com.thermondo.api

import com.thermondo.models.LoginUser
import com.thermondo.models.RegisterUser
import com.thermondo.models.UpdateUser
import com.thermondo.models.UserResponse
import com.thermondo.service.AuthService
import com.thermondo.util.SimpleJWT
import com.thermondo.util.userId
import io.github.smiley4.ktorswaggerui.dsl.get
import io.github.smiley4.ktorswaggerui.dsl.post
import io.github.smiley4.ktorswaggerui.dsl.put
import io.ktor.http.HttpStatusCode
import io.ktor.server.application.call
import io.ktor.server.auth.authenticate
import io.ktor.server.request.receive
import io.ktor.server.response.respond
import io.ktor.server.routing.Route

fun Route.auth(authService: AuthService, simpleJWT: SimpleJWT) {

    post("/users", {
        description = "User Registration"
        request {
            body<RegisterUser>()
        }
        response {
            HttpStatusCode.OK to {
                body<UserResponse>()
            }
        }
    }) {
        val registerUser = call.receive<RegisterUser>()
        val newUser = authService.register(registerUser)
        call.respond(UserResponse.fromUser(newUser, token = simpleJWT.sign(newUser.id)))
    }

    post("/users/login", {
        description = "User Authentication"
        request {
            body<LoginUser>()
        }
        response {
            HttpStatusCode.OK to {
                body<UserResponse>()
            }
        }
    }) {
        val loginUser = call.receive<LoginUser>()
        val user = authService.loginAndGetUser(loginUser.user.email, loginUser.user.password)
        call.respond(UserResponse.fromUser(user, token = simpleJWT.sign(user.id)))
    }

    authenticate {
        get("/user", {
            description = "Get Current User"
            response {
                HttpStatusCode.OK to {
                    body<UserResponse>()
                }
            }
        }) {
            val user = authService.getUserById(call.userId())
            call.respond(UserResponse.fromUser(user))
        }

        put("/user", {
            description = "Update User"
            request {
                body<UpdateUser>()
            }
            response {
                HttpStatusCode.OK to {
                    body<UserResponse>()
                }
            }
        }) {
            val updateUser = call.receive<UpdateUser>()
            val user = authService.updateUser(call.userId(), updateUser)
            call.respond(UserResponse.fromUser(user, token = simpleJWT.sign(user.id)))
        }
    }
}
