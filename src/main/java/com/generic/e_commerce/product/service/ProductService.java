package com.generic.e_commerce.product.service;

import com.generic.e_commerce.product.dto.request.ProductRequest;
import com.generic.e_commerce.product.dto.response.ProductResponse;

import java.math.BigDecimal;
import java.util.List;

public interface ProductService {
    ProductResponse createProduct(ProductRequest request);
    ProductResponse updateProduct(Long id, ProductRequest request);
    ProductResponse getProductById(Long id);
    List<ProductResponse> getAllProducts();
    List<ProductResponse> searchProducts(String name, Long categoryId, BigDecimal minPrice, BigDecimal maxPrice);
    void deleteProduct(Long id);
}
