package com.lyra.article.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ser.Serializers;
import com.fasterxml.jackson.databind.type.TypeFactory;
import com.lyra.api.article.controller.PortalControllerAPI;
import com.lyra.api.user.controller.BaseController;
import com.lyra.api.user.controller.UserControllerAPI;
import com.lyra.article.service.PortalService;
import com.lyra.exception.GraceException;
import com.lyra.pojo.Article;
import com.lyra.pojo.bo.IndexArticleBO;
import com.lyra.pojo.vo.ArticleDetailVO;
import com.lyra.pojo.vo.PublishUserVO;
import com.lyra.result.GraceJSONResult;
import com.lyra.result.PageGridResult;
import com.lyra.result.ResponseStatusEnum;
import com.lyra.utils.IpUtil;
import com.lyra.utils.JsonUtil;
import com.lyra.utils.RedisOperator;
import org.apache.commons.lang3.StringUtils;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.client.ServiceInstance;
import org.springframework.cloud.client.discovery.DiscoveryClient;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@RestController
@RequestMapping("/portal")
public class PortalController extends BaseController implements PortalControllerAPI {
    @Autowired
    private PortalService portalService;

    @Autowired
    private ObjectMapper objectMapper;


    @Autowired
    private RedisOperator redisOperator;

    @Autowired
    private UserControllerAPI userControllerAPI;

    @Autowired
    private DiscoveryClient discoveryClient;

    @Override
    public GraceJSONResult list(String keyword, Integer category, Integer page, Integer pageSize) {
        // 0. 判断分页参数是否存在 若不存在 设置一个默认值
        if (page == null) {
            page = 0;
        }

        if (pageSize == null) {
            pageSize = 10;
        }

        PageGridResult pageGridResult = portalService.queryListPageByCondition(keyword, category, page, pageSize);

        // 分页查询结果为空则直接返回
        if (pageGridResult.getRows().size() == 0) {
            return GraceJSONResult.ok(pageGridResult);
        }

        List<IndexArticleBO> indexArticleBOS = getIndexArticleList(pageGridResult);

        // 构造分页对象
        pageGridResult.setRows(indexArticleBOS);

        return GraceJSONResult.ok(pageGridResult);
    }

    @NotNull
    private List<IndexArticleBO> getIndexArticleList(PageGridResult pageGridResult) {

        List<Article> pageGridResultRows = (List<Article>) pageGridResult.getRows();

        Set<String> userIdSet = new HashSet<>();

        List<String> articleIdList = new ArrayList<>();

        // 获取文章所对应而id 保存之set中 用于查询用户信息
        pageGridResultRows.forEach((article -> {
            userIdSet.add(article.getPublishUserId());
            articleIdList.add(REDIS_READ_ARTICLE_COUNT + ":" + article.getId());
        }));

        String userIdsJson = JsonUtil.objectForJson(userIdSet);

        // 根据article id类别获取阅读数 若为空 则list中保存的值为空 根据articleIdList自上而下保存之readCountList中 后面根据id列表遍历 获取
        // 若为空 则没人阅读 设置为空即可
        List<String> articleReadCountList = redisOperator.mget(articleIdList);
//
//        List<ServiceInstance> instances = discoveryClient.getInstances("SERVICE-USER");
//        ServiceInstance userInstance = instances.get(0);

        // restTemplate原创调用根据id列表查询用户信息
//
//        String url = "http://user-service" + "/user/queryUserListByUserIds?userIds=" + userIdsJson;
//        GraceJSONResult basicUserInformationJsonResult = restTemplate.getForObject(url, GraceJSONResult.class);

        GraceJSONResult basicUserInformationJsonResult = userControllerAPI.queryUserListByUserIds(userIdsJson);
//            GraceJSONResult basicUserInformationJsonResult = userControllerAPI.queryUserListByUserIds(userIdsJson);

        if (basicUserInformationJsonResult == null) {
            GraceException.display(ResponseStatusEnum.USER_NOT_EXIST_ERROR);
        }

        Object data = basicUserInformationJsonResult.getData();
        List<PublishUserVO> publishUserVOS = null;
        try {
            publishUserVOS = objectMapper.readValue(objectMapper.writeValueAsString(data), TypeFactory.defaultInstance().constructCollectionType(List.class, PublishUserVO.class));
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }

        if (publishUserVOS == null) {
            GraceException.display(ResponseStatusEnum.ARTICLE_QUERY_PARAMS_ERROR);
        }


        List<IndexArticleBO> indexArticleBOS = new ArrayList<>();

        // 构建主页每条文章分页信息 若userId == publishId直接设置上传
        for (int i = 0; i < pageGridResultRows.size(); i++) {
            IndexArticleBO indexArticleBO = new IndexArticleBO();
            BeanUtils.copyProperties(pageGridResultRows.get(i), indexArticleBO);

            for (PublishUserVO publishUserVO : publishUserVOS) {
                if (publishUserVO.getId().equalsIgnoreCase(indexArticleBO.getPublishUserId())) {
                    indexArticleBO.setPublisherVO(publishUserVO);
                    if (StringUtils.isBlank(articleReadCountList.get(i))) {
                        indexArticleBO.setReadCounts(0);
                    } else {
                        indexArticleBO.setReadCounts(Integer.valueOf(articleReadCountList.get(i)));
                    }
                }
            }

            indexArticleBOS.add(indexArticleBO);
        }

        return indexArticleBOS;
    }


    @Override
    public GraceJSONResult queryGoodArticleListOfWriter(String writerId) {
        PageGridResult pageGridResult = portalService.queryGoodArticleListOfWriter(writerId);

        return GraceJSONResult.ok(pageGridResult);
    }

    @Override
    public GraceJSONResult detail(String articleId) {
        if (StringUtils.isBlank(articleId)) {
            GraceException.display(ResponseStatusEnum.FAILED);
        }

        ArticleDetailVO articleDetailVO = portalService.queryArticleDetaill(articleId);

        // 从redis中取出阅读数并设置
        Integer countFromRedis = getCountFromRedis(REDIS_READ_ARTICLE_COUNT + ":" + articleId);

        articleDetailVO.setReadCounts(countFromRedis);

        return GraceJSONResult.ok(articleDetailVO);
    }

    @Override
    public GraceJSONResult readArticle(String articleId, HttpServletRequest request) {
        if (StringUtils.isBlank(articleId)) {
            GraceException.display(ResponseStatusEnum.FAILED);
        }

        // 防止恶意刷新阅读量: 获取ip地址 并将ip地址设置到redis中  当前端进行恶意刷新时 使用拦截器 判断redis中是否有ip地址 若有则表示此ip已经访问过了 必须拦截
        String ip = IpUtil.getIp(request);
        redisOperator.setnx("REDIS_ALREADY_READ" + ":" + articleId + ":" + ip, ip);

        // 设置阅读数
        redisOperator.increment(REDIS_READ_ARTICLE_COUNT + ":" + articleId, 1);

        return GraceJSONResult.ok();
    }

    @Override
    public GraceJSONResult queryArticleListOfWriter(String writerId, Integer page, Integer pageSize) {
        if (page == null) {
            page = 0;
        }

        if (pageSize == null) {
            pageSize = 10;
        }

        PageGridResult pageGridResult = portalService.queryArticleListOfWriter(writerId, page, pageSize);

        List<IndexArticleBO> indexArticleList = getIndexArticleList(pageGridResult);

        pageGridResult.setRows(indexArticleList);

        return GraceJSONResult.ok(pageGridResult);
    }

    @Override
    public GraceJSONResult hotList() {
        List<Article> articles = portalService.getHostList();

        return GraceJSONResult.ok(articles);
    }


    @Override
    public Integer getReadCount(String articleId) {
        return getCountFromRedis(REDIS_READ_ARTICLE_COUNT + ":" + articleId);
    }
}
