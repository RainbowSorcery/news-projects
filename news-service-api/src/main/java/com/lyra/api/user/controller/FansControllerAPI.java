package com.lyra.api.user.controller;

import com.lyra.result.GraceJSONResult;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

@RequestMapping("/fans")
public interface FansControllerAPI {
    @PostMapping("/isMeFollowThisWriter")
    public GraceJSONResult isMeFollowThisWriter(@RequestParam String writerId,
                                                @RequestParam String fanId);

    @PostMapping("/follow")
    public GraceJSONResult follow(@RequestParam String writerId,
                                  @RequestParam String fanId);

    @PostMapping("/unfollow")
    public GraceJSONResult unfollow(@RequestParam String writerId,
                                    @RequestParam String fanId);

    @PostMapping("/queryAll")
    public GraceJSONResult queryAll(@RequestParam String writerId,
                                    @RequestParam Integer page,
                                    @RequestParam Integer pageSize);

    

    @PostMapping("/queryRatio")
    public GraceJSONResult queryRatio(@RequestParam String writerId);

    @PostMapping("/queryRatioByRegion")
    public GraceJSONResult queryRatioByRegion(@RequestParam String writerId);
}
