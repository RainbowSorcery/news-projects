package com.lyra.article.service;

import com.lyra.pojo.Article;
import com.lyra.pojo.Category;
import com.lyra.pojo.bo.NewArticleBO;
import com.lyra.result.PageGridResult;

public interface ArticleService {
    public void addArticle(NewArticleBO articleBO, Category category);

    public void updateArticleAppoint();

    PageGridResult queryArticleListByCondition(String userId, String keyword, Integer status, String startDate, String endDate, Integer page, Integer pageSize);

    void updateArticleStatus(Article article);


    PageGridResult findArticlePageByStatus(Integer status, Integer page, Integer pageSize);

    void doReviewArticle(String articleId, Integer passOrNot);

    void updateArticleWithout(String userId, String articleId);

    void deleteArticle(String userId, String articleId);

}
