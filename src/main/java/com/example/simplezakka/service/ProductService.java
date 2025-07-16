package com.example.simplezakka.service;

import com.example.simplezakka.dto.product.ProductDetail;
import com.example.simplezakka.dto.product.ProductListItem;
import com.example.simplezakka.entity.Product;
import com.example.simplezakka.repository.ProductRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class ProductService {

    private final ProductRepository productRepository;
    
    public ProductService(ProductRepository productRepository) {
        this.productRepository = productRepository;
    }
    
    public List<ProductListItem> findAllProducts() {
        return productRepository.findAll().stream()
                .map(this::convertToListItem)
                .collect(Collectors.toList());
    }
    
    public ProductDetail findProductById(Integer productId) {
        Optional<Product> productOpt = productRepository.findById(productId);
        return productOpt.map(this::convertToDetail).orElse(null);
    }
    
    private ProductListItem convertToListItem(Product product) {
        return new ProductListItem(
                product.getProductId(),
                product.getName(),
                product.getPrice(),
                product.getImageUrl(),
                product.getCategory(),
                product.getMaterial()
        );
    }
    
    private ProductDetail convertToDetail(Product product) {
        return new ProductDetail(
                product.getProductId(),
                product.getName(),
                product.getPrice(),
                product.getDescription(),
                product.getIsRecommended(),
                product.getStock(),
                product.getImageUrl(),
                product.getCategory(),
                product.getMaterial()
        );
    }

    @Transactional
    public ProductDetail createProduct(ProductDetail productDetail) {
        Product product = new Product();
        product.setName(productDetail.getName());
        product.setPrice(productDetail.getPrice());
        product.setDescription(productDetail.getDescription());
        product.setIsRecommended(productDetail.getIsRecommended());
        product.setStock(productDetail.getStock());
        product.setImageUrl(productDetail.getImageUrl());
        product.setCategory(productDetail.getCategory());
        product.setMaterial(productDetail.getMaterial());

        Product savedProduct = productRepository.save(product);
        return convertToDetail(savedProduct);
    }

    @Transactional
    public ProductDetail updateProduct(Integer productId, ProductDetail productDetail) {
        Optional<Product> productOpt = productRepository.findById(productId);
        if (productOpt.isEmpty()) {
            return null;
        }
        Product product = productOpt.get();
        product.setName(productDetail.getName());
        product.setPrice(productDetail.getPrice());
        product.setDescription(productDetail.getDescription());
        product.setIsRecommended(productDetail.getIsRecommended());
        product.setStock(productDetail.getStock());
        product.setImageUrl(productDetail.getImageUrl());
        product.setCategory(productDetail.getCategory());
        product.setMaterial(productDetail.getMaterial());

        Product updatedProduct = productRepository.save(product);
        return convertToDetail(updatedProduct);
    }

    @Transactional
    public boolean deleteProduct(Integer productId) {
        if (!productRepository.existsById(productId)) {
            return false;
        }
        productRepository.deleteById(productId);
        return true;
    }
}