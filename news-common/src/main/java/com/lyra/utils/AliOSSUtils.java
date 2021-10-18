package com.lyra.utils;

import com.aliyun.oss.OSS;
import com.aliyun.oss.OSSClientBuilder;
import com.lyra.utils.extend.AliyunOSSResources;
import com.lyra.utils.extend.AliyunResources;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.InputStream;

@Component
public class AliOSSUtils {
    @Autowired
    private AliyunOSSResources aliyunOSSResources;

    @Autowired
    private AliyunResources aliyunResources;

    public String uploadFile(InputStream fileInputStream, String uploadPathAndFileName) {
        // yourEndpoint填写Bucket所在地域对应的Endpoint。以华东1（杭州）为例，Endpoint填写为https://oss-cn-hangzhou.aliyuncs.com。
        String endpoint = aliyunOSSResources.getEndpoint();
    // 阿里云账号AccessKey拥有所有API的访问权限，风险很高。强烈建议您创建并使用RAM用户进行API访问或日常运维，请登录RAM控制台创建RAM用户。
        String accessKeyId = aliyunResources.getAccessKeyId();
        String accessKeySecret = aliyunResources.getAccessKeySecret();

    // 创建OSSClient实例。
        OSS ossClient = new OSSClientBuilder().build(endpoint, accessKeyId, accessKeySecret);

    // 依次填写Bucket名称（例如examplebucket）和Object完整路径（例如exampledir/exampleobject.txt）。Object完整路径中不能包含Bucket名称。
        ossClient.putObject(aliyunOSSResources.getBucket(), uploadPathAndFileName, fileInputStream);

    // 关闭OSSClient。
        ossClient.shutdown();

        return aliyunOSSResources.getHost() + "/" + uploadPathAndFileName;
    }
}
