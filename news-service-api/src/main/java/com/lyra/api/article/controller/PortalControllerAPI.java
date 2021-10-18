package com.lyra.api.article.controller;

import com.lyra.result.GraceJSONResult;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import javax.servlet.http.HttpServletRequest;

@FeignClient(value = "article-service", path = "portal")
public interface PortalControllerAPI {
    @GetMapping("/article/list")
    public GraceJSONResult  list(@RequestParam String keyword,
                                @RequestParam(required = false) Integer category,
                                @RequestParam Integer page,
                                @RequestParam Integer pageSize);

    @GetMapping("/article/hotList")
    public GraceJSONResult hotList();

    @GetMapping("/article/queryArticleListOfWriter")
    public GraceJSONResult queryArticleListOfWriter(@RequestParam String writerId,
                                                    @RequestParam Integer page,
                                                    @RequestParam Integer pageSize);

    @GetMapping("/article/queryGoodArticleListOfWriter")
    public GraceJSONResult queryGoodArticleListOfWriter(@RequestParam String writerId);

    @GetMapping("/article/detail")
    public GraceJSONResult detail(@RequestParam String articleId);

    @PostMapping("/article/readArticle")
    public GraceJSONResult readArticle(@RequestParam String articleId, HttpServletRequest request);


    @GetMapping("/article/getReadCount")
    public Integer getReadCount(@RequestParam String articleId);
}
