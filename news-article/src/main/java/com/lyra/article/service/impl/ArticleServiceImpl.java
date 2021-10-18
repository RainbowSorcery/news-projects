package com.lyra.article.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.Query;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.lyra.api.article.controller.PortalControllerAPI;
import com.lyra.article.controller.PortalController;
import com.lyra.article.mapper.ArticleCustomMapper;
import com.lyra.article.mapper.ArticleMapper;
import com.lyra.article.service.ArticleService;
import com.lyra.enums.AppointStatusEnums;
import com.lyra.enums.ArticleStatusEnums;
import com.lyra.enums.YesOrNoEnums;
import com.lyra.exception.GraceException;
import com.lyra.pojo.Article;
import com.lyra.pojo.Category;
import com.lyra.pojo.bo.NewArticleBO;
import com.lyra.pojo.vo.ArticleDetailVO;
import com.lyra.result.GraceJSONResult;
import com.lyra.result.PageGridResult;
import com.lyra.result.ResponseStatusEnum;
import com.lyra.utils.ContentDetectionUtils;
import com.lyra.utils.JsonUtil;
import freemarker.template.Configuration;
import freemarker.template.Template;
import org.apache.commons.lang3.StringUtils;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;

import java.io.File;
import java.io.FileWriter;
import java.io.Writer;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

@Service
public class ArticleServiceImpl implements ArticleService {
    @Autowired
    private PortalControllerAPI portalControllerAPI;


    @Autowired
    private ArticleMapper articleMapper;

    @Autowired
    private ArticleCustomMapper articleCustomMapper;

    @Autowired
    private ContentDetectionUtils contentDetectionUtils;

    @Value("${spring.freemarker.article}")
    private String articleTargetPath;

    @Autowired
    private ObjectMapper objectMapper;

    @Override
    public void addArticle(NewArticleBO articleBO, Category category) {
        Article article = new Article();
        BeanUtils.copyProperties(articleBO, article);
        article.setArticleStatus(ArticleStatusEnums.auditing.getType());

        // 设置初始值
        article.setReadCounts(0);
        article.setCommentCounts(0);
        article.setIsDelete(YesOrNoEnums.NO.getType());
        article.setCreateTime(new Date());
        article.setUpdateTime(new Date());
        article.setCategoryId(category.getId());

        // 判断定时任务类型
        if (Objects.equals(articleBO.getIsAppoint(), AppointStatusEnums.regular_update.getType())) {
            article.setPublishTime(articleBO.getPublishTime());
        } else {
            article.setPublishTime(new Date());
        }

        // 进行入库操作
        int result = articleMapper.insert(article);

        if (result != 1) {
            GraceException.display(ResponseStatusEnum.ARTICLE_CREATE_ERROR);
        }

        Integer contentDetection = contentDetectionUtils.contentDetection(articleBO.getContent());

        if (Objects.equals(contentDetection, ArticleStatusEnums.audit_failed.getType())) {
            article.setArticleStatus(ArticleStatusEnums.audit_failed.getType());
        } else if (Objects.equals(contentDetection, ArticleStatusEnums.audit_success.getType())) {
            article.setArticleStatus(ArticleStatusEnums.audit_success.getType());
        } else if (Objects.equals(contentDetection, ArticleStatusEnums.manual_audit.getType())) {
            article.setArticleStatus(ArticleStatusEnums.audit_success.getType());
        }
        this.updateArticleStatus(article);

        // 判断文章是否审核通过 若审核通过 则创建静态页面
        if (Objects.equals(article.getArticleStatus(), ArticleStatusEnums.audit_success.getType())) {
            try {
                createArticleDetailHtml(article.getId());
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public void createArticleDetailHtml(String articleId) throws Exception {
        // 配置freemarker
        Configuration configuration =
                new Configuration(Configuration.getVersion());
        String classPath = Objects.requireNonNull(this.getClass().getResource("/")).getPath();
        // 设置模板所在目录
        configuration.setDirectoryForTemplateLoading(new File(classPath + "/templates/"));

        // 获取模板
        Template template = configuration.getTemplate("detail.ftl", "utf-8");


        File targetFile = new File(articleTargetPath);
        if (!targetFile.exists()) {
            targetFile.mkdirs();
        }
        Writer out = new FileWriter(targetFile + File.separator + articleId + ".html");

        ArticleDetailVO articleDetail = getArticleDetail(articleId);

        Map<String, ArticleDetailVO> articleDetailVOMap = new HashMap<>();
        articleDetailVOMap.put("articleDetail", articleDetail);

        // 设置冬天数据并进行输出
        template.process(articleDetailVOMap, out);
        out.close();

    }

    public ArticleDetailVO getArticleDetail(String articleId) {
//        String getFileBase64Url = "http://article.imoocnews.com:8001/portal/article/detail?articleId=" + articleId;
//        GraceJSONResult articleGraceJsonResult = restTemplate.getForObject(getFileBase64Url, GraceJSONResult.class);

        GraceJSONResult articleGraceJsonResult = portalControllerAPI.detail(articleId);

        if (articleGraceJsonResult == null) {
            GraceException.display(ResponseStatusEnum.FAILED);
        }

        String articleDetailJson = JsonUtil.objectForJson(articleGraceJsonResult.getData());

        ArticleDetailVO articleDetailVO = null;
        try {
            articleDetailVO = objectMapper.readValue(articleDetailJson, ArticleDetailVO.class);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }


        return articleDetailVO;
    }

    @Override
    public void updateArticleAppoint() {
        articleCustomMapper.updateArticleAppoint();
      }

    @Override
    public void deleteArticle(String userId, String articleId) {
        Article article = articleMapper.selectById(articleId);

        if (article == null) {
            GraceException.display(ResponseStatusEnum.ARTICLE_WITHDRAW_ERROR);
        }

        article.setIsDelete(YesOrNoEnums.YES.getType());

        QueryWrapper<Article> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("id", articleId);
        queryWrapper.eq("publish_user_id", userId);

        articleMapper.update(article, queryWrapper);
    }

    @Override
    @Transactional
    public void updateArticleWithout(String userId, String articleId) {
        Article article = articleMapper.selectById(articleId);

        if (article == null) {
            GraceException.display(ResponseStatusEnum.ARTICLE_WITHDRAW_ERROR);
        }

        article.setArticleStatus(ArticleStatusEnums.article_without.getType());

        QueryWrapper<Article> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("id", articleId);
        queryWrapper.eq("publish_user_id", userId);

        articleMapper.update(article, queryWrapper);
    }

    @Override
    @Transactional
    public void doReviewArticle(String articleId, Integer passOrNot) {
        Article article = articleMapper.selectById(articleId);

        if (article == null) {
            GraceException.display(ResponseStatusEnum.ARTICLE_REVIEW_ERROR);
        }

        if (Objects.equals(passOrNot, YesOrNoEnums.YES.getType())) {
            article.setArticleStatus(ArticleStatusEnums.audit_success.getType());
        } else if (Objects.equals(passOrNot, YesOrNoEnums.NO.getType())) {
            article.setArticleStatus(ArticleStatusEnums.audit_failed.getType());
        } else {
            GraceException.display(ResponseStatusEnum.ARTICLE_REVIEW_ERROR);
        }

        articleMapper.updateById(article);

    }

    @Override
    public PageGridResult findArticlePageByStatus(Integer status, Integer page, Integer pageSize) {
        QueryWrapper<Article> queryWrapper = new QueryWrapper<>();

        if (status != null && status != 12 && status != 0) {
            queryWrapper.eq("article_status", status);
        }

        if (status != null && status == 12) {
            queryWrapper.eq("article_status", ArticleStatusEnums.auditing.getType());
        }



        return getPageGridResult(page, pageSize, queryWrapper);
    }

    @NotNull
    private PageGridResult getPageGridResult(Integer page, Integer pageSize, QueryWrapper<Article> queryWrapper) {
        Page<Article> articlePage = new Page<>(page, pageSize);

        articleMapper.selectPage(articlePage, queryWrapper);

        PageGridResult pageGridResult = new PageGridResult();


        pageGridResult.setPage(articlePage.getCurrent());
        pageGridResult.setRows(articlePage.getRecords());
        pageGridResult.setTotal(articlePage.getTotal());
        pageGridResult.setRecords(articlePage.getTotal());

        return pageGridResult;
    }

    @Override
    public PageGridResult queryArticleListByCondition(String userId, String keyword, Integer status, String startDate, String endDate, Integer page, Integer pageSize) {
        if (userId == null) {
            GraceException.display(ResponseStatusEnum.USER_NOT_EXIST_ERROR);
        }


        QueryWrapper<Article> queryWrapper = new QueryWrapper<>();


        queryWrapper.eq("publish_user_id", userId);

        if (StringUtils.isNotBlank(keyword)) {
            queryWrapper.like("title", keyword);
        }

        if (status != null && status != 12 && status != 0) {
            queryWrapper.eq("article_status", status);
        }

        if (status != null && status == 12) {
            queryWrapper.eq("article_status", ArticleStatusEnums.auditing.getType());
        }

        if (StringUtils.isNotBlank(startDate) && StringUtils.isNotBlank(endDate)) {
            queryWrapper.gt("create_time", startDate);
            queryWrapper.lt("create_time", endDate);
        }

        queryWrapper.orderByAsc("create_time");
        queryWrapper.eq("is_delete", YesOrNoEnums.NO.getType());

        return getPageGridResult(page, pageSize, queryWrapper);
    }

    @Override
    @Transactional
    public void updateArticleStatus(Article article) {

        if (article == null) {
            GraceException.display(ResponseStatusEnum.ARTICLE_REVIEW_ERROR);
        }

        articleMapper.updateById(article);
    }
}
