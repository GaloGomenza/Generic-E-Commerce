package com.ecommerce.product.service;

import com.ecommerce.category.model.Category;
import com.ecommerce.category.repository.CategoryRepository;
import com.ecommerce.product.dto.request.ProductRequest;
import com.ecommerce.product.dto.response.ProductResponse;
import com.ecommerce.product.model.Product;
import com.ecommerce.product.repository.ProductRepository;
import com.ecommerce.shared.exception.ResourceNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ProductServiceImpl implements ProductService {

    private final ProductRepository productRepository;
    private final CategoryRepository categoryRepository;

    @Override
    public ProductResponse createProduct(ProductRequest request) {
        Category category = categoryRepository.findById(request.getCategoryId())
                .orElseThrow(() -> new ResourceNotFoundException("Category not found with id: " + request.getCategoryId()));
        Product product = Product.builder()
                .name(request.getName())
                .description(request.getDescription())
                .price(request.getPrice())
                .stock(request.getStock())
                .category(category)
                .build();
        product = productRepository.save(product);
        return ProductResponse.fromEntity(product);
    }

    @Override
    public ProductResponse updateProduct(Long id, ProductRequest request) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Product not found with id: " + id));
        if (request.getName() != null) product.setName(request.getName());
        if (request.getDescription() != null) product.setDescription(request.getDescription());
        if (request.getPrice() != null) product.setPrice(request.getPrice());
        if (request.getStock() != null) product.setStock(request.getStock());
        if (request.getCategoryId() != null) {
            Category category = categoryRepository.findById(request.getCategoryId())
                    .orElseThrow(() -> new ResourceNotFoundException("Category not found with id: " + request.getCategoryId()));
            product.setCategory(category);
        }
        product = productRepository.save(product);
        return ProductResponse.fromEntity(product);
    }

    @Override
    public ProductResponse getProductById(Long id) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Product not found with id: " + id));
        return ProductResponse.fromEntity(product);
    }

    @Override
    public List<ProductResponse> getAllProducts() {
        return productRepository.findAll().stream()
                .map(ProductResponse::fromEntity)
                .toList();
    }

    @Override
    public List<ProductResponse> searchProducts(String name, Long categoryId, BigDecimal minPrice, BigDecimal maxPrice) {
        List<Product> products = productRepository.findAll();

        if (name != null && !name.isBlank()) {
            products = products.stream()
                    .filter(p -> p.getName().toLowerCase().contains(name.toLowerCase()))
                    .toList();
        }
        if (categoryId != null) {
            products = products.stream()
                    .filter(p -> p.getCategory().getId().equals(categoryId))
                    .toList();
        }
        if (minPrice != null) {
            products = products.stream()
                    .filter(p -> p.getPrice().compareTo(minPrice) >= 0)
                    .toList();
        }
        if (maxPrice != null) {
            products = products.stream()
                    .filter(p -> p.getPrice().compareTo(maxPrice) <= 0)
                    .toList();
        }

        return products.stream()
                .map(ProductResponse::fromEntity)
                .toList();
    }

    @Override
    public void deleteProduct(Long id) {
        if (!productRepository.existsById(id)) {
            throw new ResourceNotFoundException("Product not found with id: " + id);
        }
        productRepository.deleteById(id);
    }
}
