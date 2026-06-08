package com.generic.e_commerce.category.service;

import com.ecommerce.category.dto.request.CategoryRequest;
import com.ecommerce.category.dto.response.CategoryResponse;

import java.util.List;

public interface CategoryService {
    CategoryResponse createCategory(CategoryRequest request);
    CategoryResponse updateCategory(Long id, CategoryRequest request);
    CategoryResponse getCategoryById(Long id);
    List<CategoryResponse> getAllCategories();
    List<CategoryResponse> searchCategories(String name);
    void deleteCategory(Long id);
}
