package com.lyra.article.service;

import com.lyra.pojo.Article;
import com.lyra.pojo.vo.ArticleDetailVO;
import com.lyra.result.PageGridResult;

import java.util.List;

public interface PortalService {

    PageGridResult queryListPageByCondition(String keyword, Integer category, Integer page, Integer pageSize);

    List<Article> getHostList();

    PageGridResult queryArticleListOfWriter(String writerId, Integer page, Integer pageSize);

    PageGridResult queryGoodArticleListOfWriter(String writerId);

    ArticleDetailVO queryArticleDetaill(String articleId);
}
