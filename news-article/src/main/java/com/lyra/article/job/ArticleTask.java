package com.lyra.article.job;

import com.lyra.article.service.ArticleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.Date;

//@Component
@EnableScheduling
public class ArticleTask {
    @Autowired
    private ArticleService articleService;

    @Scheduled(cron = "0/3 * * * * ? ")
    public void article() {
        articleService.updateArticleAppoint();
        System.out.println(new Date());
    }
}
