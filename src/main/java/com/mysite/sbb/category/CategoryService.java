package com.mysite.sbb.category;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CategoryService {
    private final CategoryRepository categoryRepository;

    public Category getCategoryByName(String categoryName) {
        System.out.println("categoryName in getCategory: " + categoryName);
        return categoryRepository.findByName(categoryName);
    }

    public List<Category> getList() {
        return categoryRepository.findAll();
    }

    public Category getCategory(String categoryName) {
        return categoryRepository.findByName(categoryName);
    }
}
