package com.lyra.utils;

import com.aliyuncs.DefaultAcsClient;
import com.aliyuncs.IAcsClient;
import com.aliyuncs.exceptions.ClientException;
import com.aliyuncs.exceptions.ServerException;
import com.aliyuncs.facebody.model.v20191230.CompareFaceRequest;
import com.aliyuncs.facebody.model.v20191230.CompareFaceResponse;
import com.aliyuncs.profile.DefaultProfile;
import com.google.gson.Gson;
import com.lyra.utils.extend.AliyunResources;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class FaceVerifyUtils {
    @Autowired
    private AliyunResources aliyunResources;

    public boolean verifyFace(String faceA, String faceB, float qualityScoreThreshold) {

        DefaultProfile profile = DefaultProfile.getProfile("cn-shanghai", aliyunResources.getAccessKeyId(), aliyunResources.getAccessKeySecret());
        /** use STS Token
         DefaultProfile profile = DefaultProfile.getProfile(
         "<your-region-id>",           // The region ID
         "<your-access-key-id>",       // The AccessKey ID of the RAM account
         "<your-access-key-secret>",   // The AccessKey Secret of the RAM account
         "<your-sts-token>");          // STS Token
         **/
        IAcsClient client = new DefaultAcsClient(profile);

        CompareFaceRequest request = new CompareFaceRequest();
        request.setEndpoint("facebody.cn-shanghai.aliyuncs.com");
        request.setImageDataA(faceA);
        request.setImageDataB(faceB);
        request.setQualityScoreThreshold(qualityScoreThreshold);

        try {
            CompareFaceResponse response = client.getAcsResponse(request);

            Float confidence = response.getData().getConfidence();

            if (confidence < qualityScoreThreshold) {
                return false;
            }

            System.out.println(new Gson().toJson(response));
            return true;
        } catch (ServerException e) {
            e.printStackTrace();
        } catch (ClientException e) {
            System.out.println("ErrCode:" + e.getErrCode());
            System.out.println("ErrMsg:" + e.getErrMsg());
            System.out.println("RequestId:" + e.getRequestId());
        }

        return false;
    }
}
