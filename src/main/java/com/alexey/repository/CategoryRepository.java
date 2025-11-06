package com.alexey.repository;

import com.alexey.model.Category;

import java.util.List;
import java.util.Optional;

public interface CategoryRepository {
    Optional<Category> findCategory(int categoryId);

    List<Category> getCategories();
}
