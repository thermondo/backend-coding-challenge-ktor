package com.thermondo.service

import com.thermondo.models.RegisterUser
import com.thermondo.models.UpdateUser
import com.thermondo.models.User
import com.thermondo.models.Users
import com.thermondo.util.UserDoesNotExists
import com.thermondo.util.UserExists
import java.util.*
import org.jetbrains.exposed.sql.and
import org.jetbrains.exposed.sql.or

interface AuthService {
    suspend fun register(registerUser: RegisterUser): User

    suspend fun getAllUsers(): List<User>

    suspend fun getUserByEmail(email: String): User?

    suspend fun getUserById(id: String): User

    suspend fun loginAndGetUser(email: String, password: String): User

    suspend fun updateUser(userId: String, updateUser: UpdateUser): User
}

class AuthServiceImpl(private val databaseFactory: DatabaseFactory) : AuthService {

    override suspend fun register(registerUser: RegisterUser): User {
        return databaseFactory.dbQuery {
            val userInDatabase =
                User.find { (Users.username eq registerUser.user.username) or (Users.email eq registerUser.user.email) }
                    .firstOrNull()
            if (userInDatabase != null) throw UserExists()
            User.new {
                username = registerUser.user.username
                email = registerUser.user.email
                password = registerUser.user.password
            }
        }
    }

    override suspend fun getAllUsers(): List<User> {
        return databaseFactory.dbQuery {
            User.all().toList()
        }
    }

    override suspend fun getUserByEmail(email: String): User? {
        return databaseFactory.dbQuery {
            User.find { Users.email eq email }.firstOrNull()
        }
    }

    override suspend fun getUserById(id: String): User {
        return databaseFactory.dbQuery {
            getUser(id)
        }
    }

    override suspend fun loginAndGetUser(email: String, password: String): User {
        return databaseFactory.dbQuery {
            User.find { (Users.email eq email) and (Users.password eq password) }.firstOrNull()
                ?: throw UserDoesNotExists()
        }
    }

    override suspend fun updateUser(userId: String, updateUser: UpdateUser): User {
        return databaseFactory.dbQuery {
            val user = getUser(userId)
            user.apply {
                email = updateUser.user.email ?: email
                password = updateUser.user.password ?: password
                username = updateUser.user.username ?: username
            }
        }
    }
}

fun getUser(id: String) = User.findById(UUID.fromString(id)) ?: throw UserDoesNotExists()

fun getUserByUsername(username: String) = User.find { Users.username eq username }.firstOrNull()
