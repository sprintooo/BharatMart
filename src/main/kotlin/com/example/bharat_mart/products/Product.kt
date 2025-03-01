package com.example.bharat_mart.products

import com.example.bharat_mart.user.User
import jakarta.persistence.*

@Entity
@Table(name = "products")
data class Product(
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long = 0,
    val name: String,
    val price: Double,
    val stock: Long,

    @ManyToOne
    @JoinColumn(name = "seller_id", nullable = false)
    val seller: User? = null
)
