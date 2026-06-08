package com.generic.e_commerce.category.repository;

import com.ecommerce.category.model.Category;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CategoryRepository extends JpaRepository<Category, Long> {
    boolean existsByName(String name);
    List<Category> findByNameContainingIgnoreCase(String name);
}
