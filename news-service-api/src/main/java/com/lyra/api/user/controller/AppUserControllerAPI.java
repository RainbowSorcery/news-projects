package com.lyra.api.user.controller;

import com.lyra.result.GraceJSONResult;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.time.LocalDate;
import java.util.Date;

@RequestMapping("/appUser")
public interface AppUserControllerAPI {
    @PostMapping("/queryAll")
    public GraceJSONResult queryAll(@RequestParam String nickname,
                                    @RequestParam Integer status,
                                    @RequestParam String startDate,
                                    @RequestParam String endDate,
                                    @RequestParam Integer page,
                                    @RequestParam Integer pageSize);

    @PostMapping("/userDetail")
    public GraceJSONResult userDetail(@RequestParam String userId);

    @PostMapping("/freezeUserOrNot")
    public GraceJSONResult freezeUserOrNot(@RequestParam String userId, @RequestParam Integer doStatus);
}
