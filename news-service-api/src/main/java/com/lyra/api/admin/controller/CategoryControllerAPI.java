package com.lyra.api.admin.controller;

import com.lyra.pojo.bo.SaveCategoryBO;
import com.lyra.result.GraceJSONResult;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.validation.Valid;

@RequestMapping("/categoryMng")
public interface CategoryControllerAPI {
    @PostMapping("/getCatList")
    public GraceJSONResult getCatList();

    @PostMapping("/saveOrUpdateCategory")
    public GraceJSONResult saveOrUpdateCategory(@RequestBody @Valid SaveCategoryBO saveCategoryBO, BindingResult bindingResult);

    @GetMapping("/getCats")
    public GraceJSONResult getCats();
}
