package com.lyra.admin.controller;

import com.lyra.admin.service.CategoryService;
import com.lyra.api.admin.controller.CategoryControllerAPI;
import com.lyra.api.user.controller.BaseController;
import com.lyra.pojo.Category;
import com.lyra.pojo.bo.SaveCategoryBO;
import com.lyra.result.GraceJSONResult;
import com.lyra.result.ResponseStatusEnum;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

@RestController
public class CategoryController extends BaseController implements CategoryControllerAPI {
    @Autowired
    private CategoryService categoryService;


    @Override
    public GraceJSONResult getCatList() {
        List<Category> categories =
                categoryService.queryCategoryByList();

        return GraceJSONResult.ok(categories);
    }

    @Override
    public GraceJSONResult saveOrUpdateCategory(SaveCategoryBO saveCategoryBO, BindingResult bindingResult) {
        // 0. 首先判断参数是否合法
        if (bindingResult.hasErrors()) {
            Map<String, String> bindResultErrors =
                    super.getBindResultErrors(bindingResult);

            GraceJSONResult.errorMap(bindResultErrors);
        }

        // 1. 判断分类是否存在
        Category category = new Category();
        BeanUtils.copyProperties(saveCategoryBO, category);

        Category dataBaseCategory = categoryService.queryCategoryByName(category.getName());

        if (dataBaseCategory != null) {
            GraceJSONResult.errorCustom(ResponseStatusEnum.CATEGORY_EXIST_ERROR);
        }

        if (category.getId() == null) {
            // 2. 若不存在直接添加 且id未传入
            categoryService.insertCategory(category);
        } else {
            categoryService.updateCategory(category);
        }

        return GraceJSONResult.ok();
    }

    @Override
    public GraceJSONResult getCats() {
        List<Category> articlePageCats = categoryService.getArticlePageCats();

        return GraceJSONResult.ok(articlePageCats);
    }
}
