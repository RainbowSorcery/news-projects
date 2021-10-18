package com.lyra.api.article.controller;

import com.lyra.pojo.bo.NewArticleBO;
import com.lyra.result.GraceJSONResult;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@RequestMapping("/article")
public interface ArticleControllerAPI {
    @PostMapping("/createArticle")
    public GraceJSONResult createArticle(@RequestBody @Valid NewArticleBO newArticleBO, BindingResult bindingResult);

    @PostMapping("/queryMyList")
    public GraceJSONResult queryMyList(@RequestParam String userId,
                                       @RequestParam String keyword,
                                       @RequestParam(required = false) Integer status,
                                       @RequestParam String startDate,
                                       @RequestParam String endDate,
                                       @RequestParam Integer page,
                                       @RequestParam Integer pageSize);
    @PostMapping("/queryAllList")
    public GraceJSONResult queryAllList(@RequestParam(required = false) Integer status, @RequestParam Integer page, @RequestParam Integer pageSize);

    @PostMapping("/doReview")
    public GraceJSONResult doReview(@RequestParam String articleId, @RequestParam Integer passOrNot);

    @PostMapping("/withdraw")
    public GraceJSONResult withdraw(@RequestParam String userId, @RequestParam String articleId);

    @PostMapping("/delete")
    public GraceJSONResult delete(@RequestParam String userId, @RequestParam String articleId);

}
