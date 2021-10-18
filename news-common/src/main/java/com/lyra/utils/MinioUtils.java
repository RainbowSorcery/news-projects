package com.lyra.utils;

import com.lyra.exception.GraceException;
import com.lyra.result.ResponseStatusEnum;
import com.lyra.utils.extend.MinioResources;
import io.minio.*;
import io.minio.http.Method;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;

import java.io.FileInputStream;
import java.io.InputStream;

@Component
public class MinioUtils {

    private Logger logger = LoggerFactory.getLogger(MinioUtils.class);

    @Autowired
    private MinioResources minioResources;

    public String uploadFile(InputStream fileSteam, String folderAndFileName, Long size) {


        try {
            MinioClient minioClient = MinioClient.builder()
                    .endpoint(minioResources.getEndpoint())
                    .credentials(minioResources.getAccessKey(), minioResources.getSecretKey()).build();


            // 上传文件
            minioClient.putObject(PutObjectArgs.builder()
                    .bucket(minioResources.getBucket())
                    .object(folderAndFileName)
                    .stream(fileSteam, size, -1).build());

            return minioResources.getEndpoint() + minioResources.getBucket() + "/" + folderAndFileName;

        } catch (Exception e) {
            logger.error(e.getMessage());
            GraceException.display(ResponseStatusEnum.UPLOAD_FAILED);
            e.printStackTrace();
        }


        GraceException.display(ResponseStatusEnum.GET_FILE_IMAGE_URL_FAILED);

        return null;
    }
}
