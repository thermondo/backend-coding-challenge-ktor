package com.thermondo.models

import java.util.*
import org.jetbrains.exposed.dao.UUIDEntity
import org.jetbrains.exposed.dao.UUIDEntityClass
import org.jetbrains.exposed.dao.id.EntityID
import org.jetbrains.exposed.dao.id.UUIDTable

object Users : UUIDTable() {
    val email = varchar("email", 255).uniqueIndex()
    val username = varchar("username", 255).uniqueIndex()
    val password = varchar("password", 255)
}

class User(id: EntityID<UUID>) : UUIDEntity(id) {
    companion object : UUIDEntityClass<User>(Users)

    var email by Users.email
    var username by Users.username
    var password by Users.password
}

data class RegisterUser(val user: User) {
    data class User(val email: String, val username: String, val password: String)
}

data class LoginUser(val user: User) {
    data class User(val email: String, val password: String)
}

data class UpdateUser(val user: User) {
    data class User(
        val email: String? = null,
        val username: String? = null,
        val password: String? = null
    )
}

data class UserResponse(val user: User) {
    data class User(
        val email: String,
        val token: String = "",
        val username: String
    )

    companion object {
        fun fromUser(user: com.thermondo.models.User, token: String = ""): UserResponse = UserResponse(
            user = User(
                email = user.email,
                token = token,
                username = user.username
            )
        )
    }
}
