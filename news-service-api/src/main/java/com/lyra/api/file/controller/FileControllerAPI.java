package com.lyra.api.file.controller;

import com.lyra.pojo.bo.AdminBO;
import com.lyra.result.GraceJSONResult;
import io.swagger.annotations.Api;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;

@Api(value = "登录注册api", tags = "用户登录注册controller")
@Component
@FeignClient(value = "file-service", path = "/fs")
public interface FileControllerAPI {
    @PostMapping("/uploadFace")
    public GraceJSONResult uploadFace(@RequestParam String userId, MultipartFile multipartFile) throws IOException;

    @PostMapping("/uploadToGridFS")
    public GraceJSONResult uploadToGridFS(@RequestBody AdminBO adminBO);

    @GetMapping("/readInGridFS")
    public void readInGridFS(@RequestParam String faceId, HttpServletResponse response);

    @GetMapping("/getFileBase64ByFaceId")
    public GraceJSONResult getFileBase64ByFaceId(@RequestParam String faceId);

    @PostMapping("/uploadSomeFiles")
    public GraceJSONResult uploadSomeFiles(@RequestParam String userId, @RequestBody List<MultipartFile> files);
}
