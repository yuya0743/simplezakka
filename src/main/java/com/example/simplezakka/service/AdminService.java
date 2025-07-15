package com.example.simplezakka.service;

import com.example.simplezakka.dto.product.ProductDetail;

import com.example.simplezakka.entity.Product;
import com.example.simplezakka.repository.AdminRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class AdminService {

    private final AdminRepository adminRepository;
    

    public AdminService(AdminRepository adminRepository) {
        this.adminRepository = adminRepository;
    }
    
    public List<ProductDetail> findAllProducts() {
        return adminRepository.findAll().stream()
                .map(this::convertToDetail)
                .collect(Collectors.toList());
    }
    
    public ProductDetail findProductById(Integer productId) {
        Optional<Product> productOpt = adminRepository.findById(productId);
        return productOpt.map(this::convertToDetail).orElse(null);
    }
    
    
    
    private ProductDetail convertToDetail(Product product) {
        return new ProductDetail(
                product.getProductId(),
                product.getName(),
                product.getPrice(),
                product.getDescription(),
                product.getStock(),
                product.getImageUrl(),
                product.getCategory(),
                product.getMaterial()
       );
    }
}