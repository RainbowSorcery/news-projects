package com.lyra.article.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.lyra.article.mapper.CommentCustomMapper;
import com.lyra.article.mapper.CommentMapper;
import com.lyra.article.service.ArticleService;
import com.lyra.article.service.CommentService;
import com.lyra.article.service.PortalService;
import com.lyra.exception.GraceException;
import com.lyra.pojo.Comments;
import com.lyra.pojo.vo.ArticleDetailVO;
import com.lyra.pojo.vo.CommentVO;
import com.lyra.result.PageGridResult;
import com.lyra.result.ResponseStatusEnum;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.List;

@Service
public class CommentServiceImpl implements CommentService {
    @Autowired
    private PortalService portalService;

    @Autowired
    private CommentMapper commentMapper;

    @Autowired
    private CommentCustomMapper commentCustomMapper;

    @Override
    public PageGridResult queryCommentList(String articleId, Integer page, Integer pageSize) {
        QueryWrapper<CommentVO> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("article_id", articleId);

        Page<CommentVO> commentVOPage = new Page<>(page, pageSize);
//        IPage<CommentVO> page1 = commentMapper.queryCommentList(queryWrapper, commentVOPage);

        // 分页查询直接将分页参数添加到方法中 并返回分页即可 多表查询和mybatis相同 直接mybatis + xml即可
        // 内连接和外连接区别 内连接用于将左边全部显示 若右边不匹配直接为空
        // 外连接则是两张表相交部分
        // sql 的功能将左边的表全部显示 根据fatherId将右边的表匹配
        Page<CommentVO> commentVOPage1 = commentCustomMapper.queryCommentVo(articleId, commentVOPage);

        return setPageGridResult(commentVOPage1);
    }

    private PageGridResult setPageGridResult(Page page) {
        PageGridResult pageGridResult = new PageGridResult();

        pageGridResult.setPage(page.getPages());
        pageGridResult.setRecords(page.getRecords().size());
        pageGridResult.setTotal(page.getTotal());
        pageGridResult.setRows(page.getRecords());

        return pageGridResult;
    }

    @Override
    @Transactional
    public void deleteComments(String writerId, String commentId) {
        if (StringUtils.isBlank(writerId) || StringUtils.isBlank(commentId)) {
            GraceException.display(ResponseStatusEnum.FAILED);
        }
        QueryWrapper<Comments> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("writer_id", writerId);
        queryWrapper.eq("id", commentId);

        int result = commentMapper.delete(queryWrapper);

        if (result != 1) {
            GraceException.display(ResponseStatusEnum.FAILED);
        }

    }

    @Override
    public PageGridResult mng(String writerId, Integer page, Integer pageSize) {
        QueryWrapper<Comments> commentsQueryWrapper = new QueryWrapper<>();
        commentsQueryWrapper.eq("writer_id", writerId);

        Page<Comments> commentsPage = new Page<>(page, pageSize);
        commentMapper.selectPage(commentsPage, commentsQueryWrapper);

        return setPageGridResult(commentsPage);
    }

    @Override
    @Transactional
    public void createComment(String userId, String nickname, String face, String articleId, String fatherId, String content) {
        Comments comments = new Comments();
        comments.setContent(content);
        comments.setFatherId(fatherId);
        comments.setCommentUserId(userId);
        comments.setCommentUserNickname(nickname);
        comments.setCommentUserFace(face);

        ArticleDetailVO articleDetailVO = portalService.queryArticleDetaill(articleId);
        comments.setWriterId(articleDetailVO.getPublishUserId());
        comments.setArticleId(articleId);
        comments.setArticleTitle(articleDetailVO.getTitle());
        comments.setArticleCover(articleDetailVO.getArticleCover());

        comments.setCreateTime(new Date());

        commentMapper.insert(comments);
    }
}
