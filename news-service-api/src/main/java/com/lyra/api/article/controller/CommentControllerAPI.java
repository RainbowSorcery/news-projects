package com.lyra.api.article.controller;

import com.lyra.pojo.bo.CreateCommentBO;
import com.lyra.result.GraceJSONResult;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@RequestMapping("/comment")
public interface CommentControllerAPI {
    @PostMapping("/createComment")
    public GraceJSONResult createComment(@RequestBody @Valid CreateCommentBO createCommentBO, BindingResult bindingResult);

    @GetMapping("/counts")
    public GraceJSONResult counts(@RequestParam String articleId);

    @GetMapping("/list")
    public GraceJSONResult list(@RequestParam String articleId,
                                @RequestParam Integer page,
                                @RequestParam Integer pageSize);

    @PostMapping("/mng")
    public GraceJSONResult mng(@RequestParam String writerId,
                               @RequestParam Integer page,
                               @RequestParam Integer pageSize);

    @PostMapping("/delete")
    public GraceJSONResult delete(@RequestParam String writerId,
                                  @RequestParam String commentId);
}
