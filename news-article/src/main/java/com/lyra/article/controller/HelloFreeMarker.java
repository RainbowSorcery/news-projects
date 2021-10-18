package com.lyra.article.controller;

import com.lyra.article.service.CommentService;
import com.lyra.pojo.Comments;
import com.lyra.result.PageGridResult;
import freemarker.template.Configuration;
import freemarker.template.Template;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.io.File;
import java.io.FileWriter;
import java.io.Writer;
import java.util.*;

@Controller
@RequestMapping("/free")
public class HelloFreeMarker {
    @Autowired
    private CommentService commentService;

    @Value("${spring.freemarker.target}")
    private String targetHtmlPath;

    @GetMapping("/sayHello")
    public String sayHello(Model model) {
        model.addAttribute("name", "Lyra Heartstrings");

        Comments comments = new Comments();

        comments.setCreateTime(new Date());
        comments.setContent("test");
        comments.setArticleId("123456");

        model.addAttribute("comments", comments);

        List<String>list = Arrays.asList("1", "2", "3", "4");

        model.addAttribute("list", list);

        Map<String, String> map = new HashMap<>();
        map.put("name", "Lyra heartstrings");
        map.put("age", "21");

        model.addAttribute("map", map);

        return "stu";
    }

    @GetMapping("/createHtml")
    @ResponseBody
    public String createHtml(Model model) throws Exception {
        // 配置freemarker
        Configuration configuration =
                new Configuration(Configuration.getVersion());
        String classPath = Objects.requireNonNull(this.getClass().getResource("/")).getPath();
        // 设置模板所在目录
        configuration.setDirectoryForTemplateLoading(new File(classPath + "/templates/"));

        // 获取模板
        Template template = configuration.getTemplate("stu.ftl", "utf-8");


        File targetFile = new File(targetHtmlPath);
        if (!targetFile.exists()) {
            targetFile.mkdirs();
        }
        Writer out = new FileWriter(targetFile + "/10001.html");

        // 设置冬天数据并进行输出
        template.process(makeModel(model), out);
        out.close();


        return "ok";
    }

    private Model makeModel(Model model) {
        model.addAttribute("name", "Lyra Heartstrings");

        Comments comments = new Comments();

        comments.setCreateTime(new Date());
        comments.setContent("test");
        comments.setArticleId("123456");

        model.addAttribute("comments", comments);

        List<String>list = Arrays.asList("1", "2", "3", "4");

        model.addAttribute("list", list);

        Map<String, String> map = new HashMap<>();
        map.put("name", "Lyra heartstrings");
        map.put("age", "21");

        model.addAttribute("map", map);

        return model;
    }
}
