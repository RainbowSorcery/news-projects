package com.lyra.file.controller;

import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import com.lyra.api.file.controller.FileControllerAPI;
import com.lyra.exception.GraceException;
import com.lyra.file.service.UploadService;
import com.lyra.pojo.bo.AdminBO;
import com.lyra.result.GraceJSONResult;
import com.lyra.result.ResponseStatusEnum;
import com.lyra.utils.FileUtils;
import com.lyra.utils.ReviewImageUtils;
import com.mongodb.client.gridfs.GridFSBucket;
import com.mongodb.client.gridfs.GridFSFindIterable;
import com.mongodb.client.gridfs.model.GridFSFile;
import com.mongodb.client.model.Filters;
import org.bson.types.ObjectId;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;

@RestController
@RequestMapping("/fs")
public class FileController implements FileControllerAPI {
    private static final Logger log = LoggerFactory.getLogger(FileController.class);


    private static final String DEFAULTS_IMAGE = "http://121.4.39.168:9000/news/v2-fd66b46aa8cfd2e83cb76e0f18a65954_b.png";

    @Autowired
    private GridFSBucket gridFSBucket;

    @Autowired
    private UploadService uploadService;

    @Autowired
    private ReviewImageUtils reviewImageUtils;

    @Override
    public GraceJSONResult uploadFace(String userId, @RequestParam("file") MultipartFile multipartFile) throws IOException {


        if (multipartFile != null && StringUtils.isNotBlank(userId)) {
            String fileUrl = uploadService.uploadFile(multipartFile, userId);

            log.info("upload file path:{}", fileUrl);

//            return GraceJSONResult.ok(reviewImage(fileUrl));
            return GraceJSONResult.ok(fileUrl);
        }


        return GraceJSONResult.errorCustom(ResponseStatusEnum.UPLOAD_FAILED);
    }

    @Override
    public GraceJSONResult uploadToGridFS(AdminBO adminBO) {
        if (adminBO.getImg64() == null) {
            GraceException.display(ResponseStatusEnum.UPLOAD_IMAGE_BASE64_ISNULL);
        }

        Base64.Decoder decoder = Base64.getDecoder();
        byte[] imageBytes = decoder.decode(adminBO.getImg64());
        InputStream inputStream = new ByteArrayInputStream(imageBytes);

        ObjectId objectId = gridFSBucket.uploadFromStream(adminBO.getUsername() + ".png", inputStream);



        return GraceJSONResult.ok(objectId.toString());
    }


    @Override
    public void readInGridFS(String faceId, HttpServletResponse response) {
        if (StringUtils.isBlank(faceId) || faceId.equalsIgnoreCase("null")) {
            GraceException.display(ResponseStatusEnum.FILE_NOT_EXIST_ERROR);
        }


        File file = this.downloadFaceFile(faceId);



        FileUtils.downloadFileByStream(response, file);

    }


    /**
     * 根据faceId 向mongodb中查询文件 并下载至磁盘中
     * @param faceId faceid
     * @return file
     */
    private File downloadFaceFile(String faceId) {
        GridFSFindIterable fsFiles = gridFSBucket.find(Filters.eq("_id", new ObjectId(faceId)));

        GridFSFile fsFile = fsFiles.first();

        if (fsFile == null) {
            GraceException.display(ResponseStatusEnum.FILE_NOT_EXIST_ERROR);
        }

        log.info("Face File name:{}", fsFile.getFilename());

        File uploadFileDir = new File("/tmp/UserFaceTemp/");

        if (!uploadFileDir.exists()) {
            uploadFileDir.mkdirs();
        }

        File UploadFile = new File("/tmp/UserFaceTemp/" + fsFile.getFilename());

        OutputStream outputStream = null;

        try {
            outputStream = new FileOutputStream(UploadFile);

            gridFSBucket.downloadToStream(new ObjectId(faceId), outputStream);

        } catch (IOException e) {
            e.printStackTrace();
        } finally {
      if (outputStream != null) {
          try {
              outputStream.close();
          } catch (IOException e) {
              e.printStackTrace();
          }
      }
        }

        return UploadFile;
    }

    /**
     * 判断图片是否违规 若违规直接蛇和失效 返回默认图片
     * @param imageURL 检验图片url
     * @return 图片url
     */
    private String reviewImage(String imageURL) {

        String realImage = DEFAULTS_IMAGE;

        boolean result = false;

        result = reviewImageUtils.reviewImage(imageURL);

        if (result) {
            realImage = imageURL;
        }

        return realImage;
    }

    @Override
    public GraceJSONResult getFileBase64ByFaceId(String faceId) {
        if (StringUtils.isBlank(faceId)) {
            GraceException.display(ResponseStatusEnum.ADMIN_FACE_NULL_ERROR);
        }

        File faceFile = downloadFaceFile(faceId);

        String fileToBase64 = FileUtils.fileToBase64(faceFile);

        return GraceJSONResult.ok(fileToBase64);
    }

    @Override
    public GraceJSONResult uploadSomeFiles(String userId, List<MultipartFile> files) {
        if (StringUtils.isBlank(userId)) {
            GraceException.display(ResponseStatusEnum.USER_NOT_EXIST_ERROR);
        }

        if (files == null && files.size() <= 0) {
            GraceException.display(ResponseStatusEnum.FILE_UPLOAD_NULL_ERROR);
        }

        List<String> imageList = new ArrayList<>();

        files.forEach((file) -> {
            try {
                String realPath = uploadService.uploadFile(file, userId);
                reviewImage(realPath);
                imageList.add(realPath);
            } catch (IOException e) {
                e.printStackTrace();
            }
        });


        return GraceJSONResult.ok(imageList) ;
    }
}