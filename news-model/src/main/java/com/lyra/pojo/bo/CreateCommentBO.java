package com.lyra.pojo.bo;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

public class CreateCommentBO {
    @NotBlank(message = "文章id不能为空")
    private String articleId;
    @NotBlank(message = "文章评论不能为空")
    private String commentUserId;

    public String getArticleId() {
        return articleId;
    }

    public void setArticleId(String articleId) {
        this.articleId = articleId;
    }

    public String getCommentUserId() {
        return commentUserId;
    }

    public void setCommentUserId(String commentUserId) {
        this.commentUserId = commentUserId;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getFatherId() {
        return fatherId;
    }

    public void setFatherId(String fatherId) {
        this.fatherId = fatherId;
    }

    @NotBlank(message = "文章内容不能为空")
    private String content;
    @NotBlank(message = "文章父评论id不能为空")
    private String fatherId;

}