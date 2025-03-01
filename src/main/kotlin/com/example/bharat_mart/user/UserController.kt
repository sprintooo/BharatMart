package com.example.bharat_mart.user

import jakarta.transaction.Transactional
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.security.authentication.AuthenticationManager
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.core.userdetails.UserDetails
import org.springframework.security.core.userdetails.UserDetailsService
import org.springframework.security.core.userdetails.UsernameNotFoundException
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.stereotype.Repository
import org.springframework.stereotype.Service
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/api/users")
class UserController(
    private val userService: UserService
) {
    @PostMapping("/register")
    fun register(@RequestBody request: Map<String, String>): User {
        val username = request["username"] ?: throw IllegalArgumentException("Username is required")
        val password = request["password"] ?: throw IllegalArgumentException("Password is required")
        val role = request["role"] ?: throw IllegalArgumentException("Role is required")
        return userService.registerUser(username, password, role)
    }

    @PostMapping("/login")
    fun login(@RequestBody request: Map<String, String>): User? {
        val username = request["username"] ?: throw IllegalArgumentException("Username is required")
        val password = request["password"] ?: throw IllegalArgumentException("Password is required")
        return userService.loginUser(username, password)
    }

}
@Service
@Transactional
class UserService(
    private val userRepository: UserRepository,
    private val authenticationManager: AuthenticationManager,
    private val passwordEncoder: PasswordEncoder
){
    fun registerUser(username: String, password: String, role: String): User {
        val hashedPassword = passwordEncoder.encode(password)
        val user = User(username = username, password = hashedPassword, role = Role.fromString(role))
        return userRepository.save(user)
    }

    fun loginUser(username: String, password: String): User? {
        val authentication = authenticationManager.authenticate(
            UsernamePasswordAuthenticationToken(username, password)
        )
        return if (authentication.isAuthenticated) userRepository.findByUsername(username) else null
    }
}

@Service
class CustomUserDetailsService(
    private val userRepository: UserRepository
) : UserDetailsService {
    override fun loadUserByUsername(username: String): UserDetails {
        val user = userRepository.findByUsername(username)
            ?: throw UsernameNotFoundException("User not found")

        return org.springframework.security.core.userdetails.User
            .withUsername(user.username)
            .password(user.password)
            .roles(user.role.name)
            .build()
    }
}

@Repository
interface UserRepository: JpaRepository<User, Long> {
    fun findByUsername(username: String): User?
}