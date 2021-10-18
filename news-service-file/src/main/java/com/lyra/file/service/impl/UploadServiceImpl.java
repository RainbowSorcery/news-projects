package com.lyra.file.service.impl;

import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import com.lyra.exception.GraceException;
import com.lyra.file.service.UploadService;
import com.lyra.result.ResponseStatusEnum;
import com.lyra.utils.AliOSSUtils;
import com.lyra.utils.MinioUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.UUID;

@Service
public class UploadServiceImpl implements UploadService {
    @Autowired
    private MinioUtils minioUtils;

    @Autowired
    private AliOSSUtils aliOSSUtils;

    @Override
    public String uploadFile(MultipartFile multipartFile, String userId) throws IOException {

        String fileName = multipartFile.getOriginalFilename();


        if (StringUtils.isNotBlank(fileName) && StringUtils.isNotBlank(userId)) {
            String suffix = fileName.substring(fileName.lastIndexOf("."));

            String filePathAndFileName = userId + "/" + UUID.randomUUID().toString() + suffix;

            return minioUtils.uploadFile(
                    multipartFile.getInputStream(),
                    filePathAndFileName,
                    multipartFile.getSize());
        } else {
            GraceException.display(ResponseStatusEnum.FILE_UPLOAD_NULL_ERROR);

            return null;
        }
    }

    @Override
    public String uploadFileOfOSS(MultipartFile multipartFile, String userId) throws IOException {
        String fileName = multipartFile.getOriginalFilename();

        if (StringUtils.isNotBlank(fileName) && StringUtils.isNotBlank(userId)) {
            String suffix = fileName.substring(fileName.lastIndexOf("."));

            String filePathAndFileName = userId + "/" + UUID.randomUUID().toString() + suffix;


            return aliOSSUtils.uploadFile(multipartFile.getInputStream(), filePathAndFileName);
        } else {
            GraceException.display(ResponseStatusEnum.FILE_UPLOAD_NULL_ERROR);

            return null;
        }
    }
}
