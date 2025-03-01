package com.example.bharat_mart.products

import com.example.bharat_mart.user.UserRepository
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.security.core.userdetails.UsernameNotFoundException
import org.springframework.stereotype.Service
import org.springframework.web.bind.annotation.*
import java.security.Principal

@RestController
@RequestMapping("/products")
class ProductController(
    private val productService: ProductService,
    private val userRepository: UserRepository
) {

    @PostMapping
    fun createProduct(
        @RequestBody product: Product,
        principal: Principal
    ): Product {
        val user = userRepository.findByUsername(principal.name) ?: throw UsernameNotFoundException("User not found")
        return productService.createProduct(product.copy(seller = user))
    }

    @GetMapping
    fun getProducts(): List<Product> = productService.getProducts()

    @GetMapping("/{id}")
    fun getProductById(@PathVariable id: Long): Product = productService.getProductById(id)

    @PutMapping("/{id}")
    fun updateProduct(@PathVariable id: Long, @RequestBody updatedProduct: Product): Product = productService.updateProduct(id, updatedProduct)

    @DeleteMapping("/{id}")
    fun deleteProduct(@PathVariable id: Long) = productService.deleteProduct(id)
}


@Service
class ProductService(private val productRepository: ProductRepository) {
    fun createProduct(product: Product): Product = productRepository.save(product)

    fun getProducts(): List<Product> = productRepository.findAll()

    fun getProductById(id: Long): Product = productRepository.findById(id).orElseThrow { IllegalArgumentException("Product not found") }

    fun updateProduct(id: Long, updatedProduct: Product): Product {
        val existingProduct = getProductById(id)
        return productRepository.save(existingProduct.copy(
            name = updatedProduct.name,
            price = updatedProduct.price,
            stock = updatedProduct.stock
        ))
    }

    fun deleteProduct(id: Long) = productRepository.deleteById(id)
}

interface ProductRepository : JpaRepository<Product, Long>