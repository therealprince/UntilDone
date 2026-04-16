package com.example.untildone.data

import java.security.MessageDigest

data class User(
    val id: Long = 0,
    val name: String,
    val email: String,
    val passwordHash: String,
    val createdAt: Long = System.currentTimeMillis()
)

fun hashPassword(password: String): String {
    val digest = MessageDigest.getInstance("SHA-256")
    val hashBytes = digest.digest(password.toByteArray())
    return hashBytes.joinToString("") { "%02x".format(it) }
}
