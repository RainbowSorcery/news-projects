package com.lyra.file.service;

import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

public interface UploadService {
    /**
     * 上传文件
     * @param multipartFile 文件刘
     * @param userId 用户id
     * @return 文件路径
     * @throws IOException 抛出io异常
     */
    public String uploadFile(MultipartFile multipartFile, String userId) throws IOException;

    public String uploadFileOfOSS(MultipartFile multipartFile, String userId) throws IOException;
}
