package com.lyra.article.service;

import com.lyra.result.PageGridResult;

public interface CommentService {

    void createComment(String userId, String nickname, String face, String articleId, String fatherId, String content);

    PageGridResult queryCommentList(String articleId, Integer page, Integer pageSize);

    PageGridResult mng(String writerId, Integer page, Integer pageSize);

    void deleteComments(String writerId, String commentId);
}
