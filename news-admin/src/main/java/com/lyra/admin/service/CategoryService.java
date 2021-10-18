package com.lyra.admin.service;

import com.lyra.pojo.Category;

import java.util.List;

public interface CategoryService {
    public List<Category> queryCategoryByList();

    public Category queryCategoryByName(String name);

    void insertCategory(Category category);

    void updateCategory(Category category);

    List<Category> getArticlePageCats();
}
