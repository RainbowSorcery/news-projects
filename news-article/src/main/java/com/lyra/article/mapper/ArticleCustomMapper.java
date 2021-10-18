package com.lyra.article.mapper;


import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Update;

@Mapper
public interface ArticleCustomMapper {

    @Update("update article set is_appoint = 0 where is_appoint = 1 and publish_time <= now()")
    public void updateArticleAppoint();
}
