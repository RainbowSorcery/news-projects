package com.lyra.api.admin.controller;

import com.lyra.pojo.bo.AdminBO;
import com.lyra.pojo.bo.FaceLoginBO;
import com.lyra.pojo.vo.AdminLoginVO;
import com.lyra.result.GraceJSONResult;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;

@RequestMapping("/adminMng")
public interface AdminMngControllerAPI {
    @PostMapping("/adminLogin")
    public GraceJSONResult adminLogin(@RequestBody AdminLoginVO adminLoginVO,
                                      HttpServletRequest request,
                                      HttpServletResponse response,
                                      BindingResult bindingResult);

    @PostMapping("/adminIsExist")
    public GraceJSONResult adminIsExist(@RequestParam String username);

    @PostMapping("/addNewAdmin")
    public GraceJSONResult addNewAdmin(@RequestBody @Valid AdminBO adminBO, BindingResult bindingResult);


    @PostMapping("/getAdminList")
    public GraceJSONResult getAdminList(@RequestParam Integer page, @RequestParam Integer pageSize);

    @PostMapping("/adminLogout")
    public GraceJSONResult adminLogout(@RequestParam Long adminId, HttpServletRequest request, HttpServletResponse response);

    @PostMapping("/adminFaceLogin")
    public GraceJSONResult adminFaceLogin(@RequestBody @Valid FaceLoginBO faceLoginBO, HttpServletRequest request, HttpServletResponse response, BindingResult bindingResult);
}