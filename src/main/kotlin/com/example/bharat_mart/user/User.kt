package com.example.bharat_mart.user

import jakarta.persistence.*
import org.springframework.context.support.BeanDefinitionDsl

@Entity
@Table(name = "users")
data class User(
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long = 0,
    val username: String,
    val password: String,
    val role: Role
)

enum class Role {
    USER, ADMIN, SELLER;

    companion object {
        fun fromString(role: String): Role {
            return try {
                valueOf(role.uppercase())
            } catch (e: IllegalArgumentException) {
                throw IllegalArgumentException("Invalid role: $role")
            }
        }
    }
}
