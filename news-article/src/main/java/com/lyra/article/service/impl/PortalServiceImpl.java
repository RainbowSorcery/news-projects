package com.lyra.article.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.lyra.api.user.controller.UserControllerAPI;
import com.lyra.article.mapper.ArticleMapper;
import com.lyra.article.service.PortalService;
import com.lyra.enums.AppointStatusEnums;
import com.lyra.enums.ArticleStatusEnums;
import com.lyra.enums.YesOrNoEnums;
import com.lyra.exception.GraceException;
import com.lyra.pojo.Article;
import com.lyra.pojo.vo.AccountBasicInfoVO;
import com.lyra.pojo.vo.ArticleDetailVO;
import com.lyra.result.GraceJSONResult;
import com.lyra.result.PageGridResult;
import com.lyra.result.ResponseStatusEnum;
import org.apache.commons.lang3.StringUtils;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class PortalServiceImpl implements PortalService {
    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private UserControllerAPI userControllerAPI;

    @Autowired
    private ArticleMapper articleMapper;

    @Override
    public PageGridResult queryListPageByCondition(String keyword, Integer category, Integer page, Integer pageSize) {
        QueryWrapper<Article> queryWrapper = new QueryWrapper<>();

        if (keyword != null) {
            queryWrapper.like("title", keyword);
        }

        if (category != null) {
            queryWrapper.eq("category_id", category);
        }

        setCommonQueryMapper(queryWrapper);

        queryWrapper.orderByDesc("publish_time");

        Page<Article> articlePage = new Page<>(page, pageSize);
        articleMapper.selectPage(articlePage, queryWrapper);

        System.out.println(articlePage.getPages());

        return setPageGridResult(articlePage);
    }

    @NotNull
    public PageGridResult
    setPageGridResult(Page<Article> articlePage) {
        PageGridResult pageGridResult = new PageGridResult();

        pageGridResult.setPage(articlePage.getCurrent());
        pageGridResult.setTotal(articlePage.getPages());
        pageGridResult.setRecords(articlePage.getTotal());
        pageGridResult.setRows(articlePage.getRecords());

        return pageGridResult;
    }

    @Override
    public List<Article> getHostList() {
        QueryWrapper<Article> queryWrapper = new QueryWrapper<>();
        setCommonQueryMapper(queryWrapper);
        queryWrapper.orderByDesc("read_counts");

        Page<Article> articlePage = new Page<>(0, 5);

        articleMapper.selectPage(articlePage, queryWrapper);

        return articlePage.getRecords();
    }

    @Override
    public PageGridResult queryGoodArticleListOfWriter(String writerId) {
        if (StringUtils.isBlank(writerId)) {
            GraceException.display(ResponseStatusEnum.USER_NOT_EXIST_ERROR);
        }


        QueryWrapper<Article> queryWrapper = new QueryWrapper<>();
        setCommonQueryMapper(queryWrapper);
        queryWrapper.orderByDesc("read_counts");
        queryWrapper.eq("publish_user_id", writerId);

        Page<Article> articlePage = new Page<>(0, 5);

        articleMapper.selectPage(articlePage, queryWrapper);

        return setPageGridResult(articlePage);
    }

    @Override
    public ArticleDetailVO  queryArticleDetaill(String articleId) {
        // 先根据articleId将article心弦查询出来 再根据article中的publishId 将publish信息查询出来
        // 根据最后将publish name设置到vo 返回
        QueryWrapper<Article> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("id", articleId);
        setCommonQueryMapper(queryWrapper);


        Article article = articleMapper.selectOne(queryWrapper);
        if (article == null) {
            GraceException.display(ResponseStatusEnum.ARTICLE_QUERY_PARAMS_ERROR);
        }

        ArticleDetailVO articleDetailVO =
                new ArticleDetailVO();

        BeanUtils.copyProperties(article, articleDetailVO);

        String publishUserId = articleDetailVO.getPublishUserId();

//        String queryUserInfoUrl = "http://user.imoocnews.com:8003/user/getUserInfo?userId=" + publishUserId;
//        ResponseEntity<GraceJSONResult> queryUserInfoResponseEntity = restTemplate.postForEntity(queryUserInfoUrl, null, GraceJSONResult.class);

//
//        if (queryUserInfoResponseEntity.getStatusCode() != HttpStatus.OK) {
//            GraceException.display(ResponseStatusEnum.ARTICLE_QUERY_PARAMS_ERROR);
//        }

//        GraceJSONResult queryUserInfoResponseBody = queryUserInfoResponseEntity.getBody();

        GraceJSONResult queryUserInfoResponseBody = userControllerAPI.getUserInfo(publishUserId);

        if (queryUserInfoResponseBody == null) {
            GraceException.display(ResponseStatusEnum.USER_NOT_EXIST_ERROR);
        }

        AccountBasicInfoVO accountBasicInfoVO = null;
        accountBasicInfoVO = objectMapper.convertValue(queryUserInfoResponseBody.getData(), AccountBasicInfoVO.class);


        articleDetailVO.setPublishUserName(accountBasicInfoVO.getNickname());

        articleDetailVO.setCategoryName("天琴心弦");

        return articleDetailVO;
    }

    @Override
    public PageGridResult queryArticleListOfWriter(String writerId, Integer page, Integer pageSize) {
        if (StringUtils.isBlank(writerId)) {
            GraceException.display(ResponseStatusEnum.USER_NOT_EXIST_ERROR);
        }

        QueryWrapper<Article> queryWrapper = new QueryWrapper<>();
        setCommonQueryMapper(queryWrapper);
        queryWrapper.eq("publish_user_id", writerId);

        Page<Article> articlePage = new Page<>();
        articleMapper.selectPage(articlePage, queryWrapper);

        return setPageGridResult(articlePage);
    }

    private void setCommonQueryMapper(@NotNull QueryWrapper<Article> queryWrapper) {
        queryWrapper.eq("is_appoint", AppointStatusEnums.not_regular_update.getType());
        queryWrapper.eq("article_status", ArticleStatusEnums.audit_success.getType());
        queryWrapper.eq("is_delete", YesOrNoEnums.NO);

    }
    
}
