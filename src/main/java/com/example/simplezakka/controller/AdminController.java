package com.example.simplezakka.controller;

import com.example.simplezakka.dto.product.ProductDetail;
import com.example.simplezakka.service.AdminService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@RestController
@RequestMapping("/api/productsdetail")
public class AdminController {

    private final AdminService adminService;

    private static final Logger logger = LoggerFactory.getLogger(ProductController.class);

    
    public AdminController(AdminService adminService) {
        this.adminService = adminService;
    }
    
    @GetMapping
    public ResponseEntity<List<ProductDetail>> getAllProducts() {
        List<ProductDetail> products = adminService.findAllProducts();
        logger.info("getAllProducts is called。inputcode: {}", products);
        return ResponseEntity.ok(products);
    }
    
    @GetMapping("/{productId}")
    public ResponseEntity<ProductDetail> getProductById(@PathVariable Integer productId) {
        ProductDetail product = adminService.findProductById(productId);
        if (product == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(product);
    }
}