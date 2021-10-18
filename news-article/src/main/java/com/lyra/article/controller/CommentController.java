package com.lyra.article.controller;

import com.lyra.api.article.controller.CommentControllerAPI;
import com.lyra.api.user.controller.BaseController;
import com.lyra.article.service.CommentService;
import com.lyra.pojo.bo.CreateCommentBO;
import com.lyra.pojo.vo.AccountBasicInfoVO;
import com.lyra.result.GraceJSONResult;
import com.lyra.result.PageGridResult;
import com.lyra.result.ResponseStatusEnum;
import com.lyra.utils.JsonUtil;
import com.lyra.utils.RedisOperator;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

import java.util.Map;

@RestController
public class CommentController extends BaseController implements CommentControllerAPI {

    @Autowired
    private RedisOperator redisOperator;

    @Autowired
    private CommentService commentService;

    @Override
    public GraceJSONResult createComment(CreateCommentBO createCommentBO, BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            Map<String, String> bindResultErrors = getBindResultErrors(bindingResult);


            GraceJSONResult.errorMap(bindResultErrors);
        }

        // 根据用户id远程调用获取用户信息

        GraceJSONResult graceJSONResult = remoteTransferGetUserInformation(createCommentBO.getCommentUserId());
        AccountBasicInfoVO accountBasicInfoVO = JsonUtil.jsonToObject(JsonUtil.objectForJson(graceJSONResult.getData()), AccountBasicInfoVO.class);



        // 进行插入评论操作
        if (accountBasicInfoVO == null) {
            return GraceJSONResult.exception(ResponseStatusEnum.USER_NOT_EXIST_ERROR);
        }

        commentService.createComment(accountBasicInfoVO.getId(), accountBasicInfoVO.getNickname(), accountBasicInfoVO.getFace(), createCommentBO.getArticleId(), createCommentBO.getFatherId(), createCommentBO.getContent());
        // 将评论数保存至redis中 每次评论时 redis + 1
        redisOperator.increment(REDIS_COMMENT_COUNT + ":" + createCommentBO.getArticleId(), 1);

        return GraceJSONResult.ok();
    }

    @Override
    public GraceJSONResult list(String articleId, Integer page, Integer pageSize) {
        if (page == null) {
            page = 0;
        }

        if (pageSize == null) {
            pageSize = 10;
        }

        PageGridResult pageGridResult = commentService.queryCommentList(articleId, page, pageSize);

        return GraceJSONResult.ok(pageGridResult);
    }

    @Override
    public GraceJSONResult delete(String writerId, String commentId) {
        commentService.deleteComments(writerId, commentId);

        return GraceJSONResult.ok();
    }

    @Override
    public GraceJSONResult mng(String writerId, Integer page, Integer pageSize) {
        if (page == null) {
            page = 0;
        }

        if (pageSize == null) {
            pageSize = 10;
        }

        PageGridResult pageGridResult = commentService.mng(writerId, page, pageSize);


        return GraceJSONResult.ok(pageGridResult);
    }

    @Override
    public GraceJSONResult counts(String articleId) {
        if (StringUtils.isBlank(articleId)) {
            return GraceJSONResult.exception(ResponseStatusEnum.FAILED);
        }

        Integer countFromRedis = getCountFromRedis(REDIS_COMMENT_COUNT + ":" + articleId);

        return GraceJSONResult.ok(countFromRedis);
    }
}
