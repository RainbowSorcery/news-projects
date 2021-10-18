package com.lyra.article.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.lyra.api.article.controller.ArticleControllerAPI;
import com.lyra.api.user.controller.BaseController;
import com.lyra.article.service.ArticleService;
import com.lyra.enums.ArticleTypeEnum;
import com.lyra.exception.GraceException;
import com.lyra.pojo.Category;
import com.lyra.pojo.bo.NewArticleBO;
import com.lyra.result.GraceJSONResult;
import com.lyra.result.PageGridResult;
import com.lyra.result.ResponseStatusEnum;
import com.lyra.utils.JsonUtil;
import com.lyra.utils.RedisOperator;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;
import java.util.Objects;

@RestController
public class ArticleController extends BaseController implements ArticleControllerAPI {
    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private ArticleService articleService;

    @Autowired
    private RedisOperator redisOperator;

    @Override
    public GraceJSONResult queryAllList(Integer status, Integer page, Integer pageSize) {
        if (page == null) {
            page = 0;
        }

        if (pageSize == null) {
            pageSize = 10;
        }

        PageGridResult pageGridResult = articleService.findArticlePageByStatus(status, page, pageSize);

        return GraceJSONResult.ok(pageGridResult);
    }

    @Override
    public GraceJSONResult doReview(String articleId, Integer passOrNot) {
        if (StringUtils.isBlank(articleId) || passOrNot == null) {
            GraceException.display(ResponseStatusEnum.ARTICLE_QUERY_PARAMS_ERROR);
        }

        articleService.doReviewArticle(articleId, passOrNot);

        return GraceJSONResult.ok();
    }

    @Override
    public GraceJSONResult createArticle(NewArticleBO newArticleBO, BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            Map<String, String> bindResultErrors = super.getBindResultErrors(bindingResult);

            return GraceJSONResult.errorMap(bindResultErrors);
        }

        // 判断文章类型是否为单封面状态 若为单封面状态必须传入 articleCover
        if (Objects.equals(newArticleBO.getArticleType(), ArticleTypeEnum.ONE_IMAGE.getType())) {
            if (StringUtils.isBlank(newArticleBO.getArticleCover())) {
                GraceException.display(ResponseStatusEnum.ARTICLE_COVER_NOT_EXIST_ERROR);
            }
        } else {
            newArticleBO.setArticleCover("");
        }

        // 判断用户选中的文章类型是否存在
        String categoryCacheJson = redisOperator.get(CATEGORY_CACHE);
        if (StringUtils.isBlank(categoryCacheJson)) {
            GraceException.display(ResponseStatusEnum.FAILED);
        }

        Category temp = null;
        List categoryMapList = JsonUtil.jsonToObject(categoryCacheJson, List.class);

        // 将redis中的数据进行遍历 对比传入的类别id是否可以匹配到
        for (Object categoryObject : categoryMapList) {
            try {
                Category category = objectMapper.readValue(objectMapper.writeValueAsString(categoryObject), Category.class);

                // 匹配直接跳出循环
                if (Objects.equals(category.getId(), newArticleBO.getCategoryId())) {
                    temp = category;

                    break;
                }
            } catch (JsonProcessingException e) {
                e.printStackTrace();
            }
        }

        if (temp == null) {
            GraceException.display(ResponseStatusEnum.ARTICLE_CATEGORY_NOT_EXIST_ERROR);
        }


        // 进行添加操作
        articleService.addArticle(newArticleBO, temp);

        return GraceJSONResult.ok();
    }

    @Override
    public GraceJSONResult withdraw(String userId, String articleId) {
        if (StringUtils.isBlank(userId) || StringUtils.isBlank(articleId)) {
            GraceException.display(ResponseStatusEnum.ARTICLE_QUERY_PARAMS_ERROR);
        }

        articleService.updateArticleWithout(userId, articleId);

        return GraceJSONResult.ok();
    }

    @Override
    public GraceJSONResult delete(String userId, String articleId) {
        if (StringUtils.isBlank(userId) || StringUtils.isBlank(articleId)) {
            GraceException.display(ResponseStatusEnum.ARTICLE_QUERY_PARAMS_ERROR);
        }

        articleService.deleteArticle(userId, articleId);

        return GraceJSONResult.ok();
    }

    @Override
    public GraceJSONResult queryMyList(String userId, String keyword, Integer status, String startDate, String endDate, Integer page, Integer pageSize) {

        if (page == null) {
            page = 0;
        }

        if (pageSize == null) {
            pageSize = 10;
        }

        PageGridResult articles = articleService.queryArticleListByCondition(userId, keyword, status, startDate, endDate, page, pageSize);


        return GraceJSONResult.ok(articles);
    }
}
