package com.generic.e_commerce.product.service;

import com.generic.e_commerce.category.model.Category;
import com.generic.e_commerce.category.repository.CategoryRepository;
import com.generic.e_commerce.product.dto.request.ProductRequest;
import com.generic.e_commerce.product.dto.response.ProductResponse;
import com.generic.e_commerce.product.model.Product;
import com.generic.e_commerce.product.repository.ProductRepository;
import com.generic.e_commerce.shared.exception.ResourceNotFoundException;
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
        if (productRepository.existsByName(request.getName())) {
            throw new IllegalArgumentException("Ya existe un producto con el nombre: " + request.getName());
        }
        Category category = categoryRepository.findById(request.getCategoryId())
                .orElseThrow(() -> new ResourceNotFoundException("No se encontró la categoría con el id: " + request.getCategoryId()));
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
                .orElseThrow(() -> new ResourceNotFoundException("No se encontró el producto con el id: " + id));
        if (request.getName() != null) product.setName(request.getName());
        if (request.getDescription() != null) product.setDescription(request.getDescription());
        if (request.getPrice() != null) product.setPrice(request.getPrice());
        if (request.getStock() != null) product.setStock(request.getStock());
        if (request.getCategoryId() != null) {
            Category category = categoryRepository.findById(request.getCategoryId())
                    .orElseThrow(() -> new ResourceNotFoundException("No se encontró la categoría con el id: " + request.getCategoryId()));
            product.setCategory(category);
        }
        product = productRepository.save(product);
        return ProductResponse.fromEntity(product);
    }

    @Override
    public ProductResponse getProductById(Long id) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("No se encontró el producto con el id: " + id));
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
            throw new ResourceNotFoundException("No se encontró el producto con el id: " + id);
        }
        productRepository.deleteById(id);
    }
}
