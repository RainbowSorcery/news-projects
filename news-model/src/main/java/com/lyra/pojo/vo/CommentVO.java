package com.lyra.pojo.vo;

import java.util.Date;

public class CommentVO {
    private String commentId;
    private String fatherId;
    private String commentUserId;
    private String commentUserNickname;
    private String articleId;
    private String content;
    private Date createTime;
    private String quoteUserNickname;
    private String quoteContent;

    public String getCommentUserFace() {
        return commentUserFace;
    }

    public void setCommentUserFace(String commentUserFace) {
        this.commentUserFace = commentUserFace;
    }

    private String commentUserFace;

    public String getCommentId() {
        return commentId;
    }

    public void setCommentId(String commentId) {
        this.commentId = commentId;
    }

    public String getFatherId() {
        return fatherId;
    }

    public void setFatherId(String fatherId) {
        this.fatherId = fatherId;
    }

    public String getCommentUserId() {
        return commentUserId;
    }

    public void setCommentUserId(String commentUserId) {
        this.commentUserId = commentUserId;
    }

    public String getCommentUserNickname() {
        return commentUserNickname;
    }

    public void setCommentUserNickname(String commentUserNickname) {
        this.commentUserNickname = commentUserNickname;
    }

    public String getArticleId() {
        return articleId;
    }

    public void setArticleId(String articleId) {
        this.articleId = articleId;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }

    public String getQuoteUserNickname() {
        return quoteUserNickname;
    }

    public void setQuoteUserNickname(String quoteUserNickname) {
        this.quoteUserNickname = quoteUserNickname;
    }

    public String getQuoteContent() {
        return quoteContent;
    }

    public void setQuoteContent(String quoteContent) {
        this.quoteContent = quoteContent;
    }
}
