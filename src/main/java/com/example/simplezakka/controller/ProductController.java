package com.example.simplezakka.controller;

import com.example.simplezakka.dto.product.ProductDetail;
import com.example.simplezakka.dto.product.ProductListItem;
import com.example.simplezakka.service.ProductService;

import jakarta.validation.Valid;
 
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@RestController
@RequestMapping("/api/products")
public class ProductController {

    private final ProductService productService;

    private static final Logger logger = LoggerFactory.getLogger(ProductController.class);


    public ProductController(ProductService productService) {
        this.productService = productService;
    }
    
    @GetMapping
    public ResponseEntity<List<ProductListItem>> getAllProducts() {
        List<ProductListItem> products = productService.findAllProducts();
        logger.info("getAllProducts is called。inputcode: {}", products);
        return ResponseEntity.ok(products);
    }
    
    @GetMapping("/{productId}")
    public ResponseEntity<ProductDetail> getProductById(@PathVariable Integer productId) {
        ProductDetail product = productService.findProductById(productId);
        if (product == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(product);
    }

     // 商品の追加 

     @PostMapping 
     public ResponseEntity<ProductDetail> createProduct(@Valid @RequestBody ProductDetail productDetail) { 
         ProductDetail createdProduct = productService.createProduct(productDetail); 
         logger.info("Product created: {}", createdProduct); 
         return ResponseEntity.status(HttpStatus.CREATED).body(createdProduct); 
    } 

     // 商品の更新 
    @PutMapping("/{productId}") 
    public ResponseEntity<ProductDetail> updateProduct( 
        @PathVariable Integer productId, 
        @Valid @RequestBody ProductDetail productDetail) { 
            ProductDetail updatedProduct = productService.updateProduct(productId, productDetail); 
            if (updatedProduct == null) { 
                return ResponseEntity.notFound().build(); 
            } 
            logger.info("Product updated: {}", updatedProduct); 
            return ResponseEntity.ok(updatedProduct); 
         } 

    // 商品の削除 
    @DeleteMapping("/{productId}") 
    public ResponseEntity<Void> deleteProduct(@PathVariable Integer productId) { 
        boolean deleted = productService.deleteProduct(productId); 
        if (!deleted) { 
            return ResponseEntity.notFound().build(); 
        } 
        logger.info("Product deleted: {}", productId); 
        return ResponseEntity.noContent().build(); 
    } 

} 