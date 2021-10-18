package com.lyra.article.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.lyra.pojo.Comments;
import com.lyra.pojo.vo.CommentVO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface CommentCustomMapper  {
    Page<CommentVO> queryCommentVo(@Param("articleId") String articleId, Page<CommentVO> page);
}
