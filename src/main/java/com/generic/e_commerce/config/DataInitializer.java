package com.generic.e_commerce.config;

import com.generic.e_commerce.category.model.Category;
import com.generic.e_commerce.category.repository.CategoryRepository;
import com.generic.e_commerce.product.model.Product;
import com.generic.e_commerce.product.repository.ProductRepository;
import com.generic.e_commerce.user.model.User;
import com.generic.e_commerce.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;

@Component
@RequiredArgsConstructor
public class DataInitializer implements CommandLineRunner {

    private final UserRepository userRepository;
    private final CategoryRepository categoryRepository;
    private final ProductRepository productRepository;

    @Override
    public void run(String... args) {
        if (userRepository.count() > 0) return;

        User admin = User.builder()
                .email("admin@test.com")
                .password("123")
                .name("Admin")
                .role(User.Role.ADMIN)
                .build();
        userRepository.save(admin);

        User user = User.builder()
                .email("cliente@test.com")
                .password("123")
                .name("Cliente")
                .role(User.Role.USER)
                .build();
        userRepository.save(user);

        Category cat = Category.builder()
                .name("Electronica")
                .description("Productos electronicos")
                .build();
        categoryRepository.save(cat);

        Product prod = Product.builder()
                .name("Notebook Pro")
                .description("15 pulgadas 16GB RAM")
                .price(new BigDecimal("1500.00"))
                .stock(10)
                .category(cat)
                .build();
        productRepository.save(prod);

        Product prod2 = Product.builder()
                .name("Mouse Inalambrico")
                .description("Ergonomico")
                .price(new BigDecimal("45.99"))
                .stock(20)
                .category(cat)
                .build();
        productRepository.save(prod2);
    }
}
