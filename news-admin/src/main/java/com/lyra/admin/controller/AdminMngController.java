package com.lyra.admin.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.lyra.admin.service.AdminService;
import com.lyra.api.admin.controller.AdminMngControllerAPI;
import com.lyra.api.file.controller.FileControllerAPI;
import com.lyra.api.user.controller.BaseController;
import com.lyra.exception.GraceException;
import com.lyra.pojo.AdminUser;
import com.lyra.pojo.bo.AdminBO;
import com.lyra.pojo.bo.FaceLoginBO;
import com.lyra.pojo.vo.AdminLoginVO;
import com.lyra.result.GraceJSONResult;
import com.lyra.result.PageGridResult;
import com.lyra.result.ResponseStatusEnum;
import com.lyra.utils.FaceVerifyUtils;
import com.lyra.utils.RedisOperator;
import org.apache.commons.lang3.StringUtils;
import org.mindrot.jbcrypt.BCrypt;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Map;
import java.util.UUID;

@RestController
public class AdminMngController extends BaseController implements AdminMngControllerAPI {
    @Autowired
    private AdminService adminService;

    @Autowired
    private FileControllerAPI fileControllerAPI;

    @Autowired
    private RedisOperator redisOperator;


    @Autowired
    private FaceVerifyUtils faceVerifyUtils;

    @Override
    public GraceJSONResult adminLogin(AdminLoginVO adminLoginVO,
                                      HttpServletRequest request,
                                      HttpServletResponse response,
                                      BindingResult bindingResult) {
        // 1. 首先查询字段是否合法
        if (bindingResult.hasErrors()) {
            Map<String, String> bindResultErrors = super.getBindResultErrors(bindingResult);

            return GraceJSONResult.errorMap(bindResultErrors);
        }

        // 2. 首先根据用户名查询 用户在database中是否有记录
        AdminUser usernameByAdmin = adminService.findUsernameByAdmin(adminLoginVO.getUsername());
        if (usernameByAdmin == null) {
            return GraceJSONResult.errorCustom(ResponseStatusEnum.ADMIN_NOT_EXIT_ERROR);
        }

        // 3. 查看用户名密码是否匹配
        boolean flag = BCrypt.checkpw(adminLoginVO.getPassword(), usernameByAdmin.getPassword());

        if (flag) {
            setAdminLoginCookie(request, response, usernameByAdmin);
            return GraceJSONResult.ok();
        } else {
         return GraceJSONResult.errorCustom(ResponseStatusEnum.ADMIN_NOT_EXIT_ERROR);
        }
    }


    protected void setAdminLoginCookie(HttpServletRequest request, HttpServletResponse response, AdminUser usernameByAdmin) {
        String token = UUID.randomUUID().toString();
        setCookie(request, response, "atoken", token, COOKIE_AGE);
        setCookie(request, response, "aid", usernameByAdmin.getId(), COOKIE_AGE);
        setCookie(request, response, "aname", usernameByAdmin.getUsername(), COOKIE_AGE);

        redisOperator.set(REDIS_ADMIN_TOKEN + ":" + usernameByAdmin.getId(), token);

    }

    @Override
    public GraceJSONResult adminIsExist(String username) {
        checkAdminExits(username);

        return GraceJSONResult.ok();
    }



    private void checkAdminExits(String username) {
        AdminUser usernameByAdmin = adminService.findUsernameByAdmin(username);

        if (usernameByAdmin != null) {
            GraceException.display(ResponseStatusEnum.ADMIN_USERNAME_EXIST_ERROR);
        }
    }

    @Override
    public GraceJSONResult addNewAdmin(AdminBO adminBO, BindingResult bindingResult) {

        // 1. 判断字段是否有错
        if (bindingResult.hasErrors()) {
            Map<String, String> bindResultErrors = super.getBindResultErrors(bindingResult);
            return GraceJSONResult.errorMap(bindResultErrors);
        }

        // 2. 判断是否有base64图片 若有base64图片表示人脸识别注册 可以无需输入密码
        if (StringUtils.isBlank(adminBO.getImg64())) {
            if (StringUtils.isBlank(adminBO.getPassword()) || StringUtils.isBlank(adminBO.getConfirmPassword())) {
                GraceException.display(ResponseStatusEnum.ADMIN_PASSWORD_ERROR);
            }
            // 判断密码和确认密码是否一致
            if (!adminBO.getPassword().equals(adminBO.getConfirmPassword())) {
                GraceException.display(ResponseStatusEnum.ADMIN_PASSWORD_ERROR);
            }
        }

        // 校验用户是否存在
        this.checkAdminExits(adminBO.getUsername());

        adminService.addAdminUser(adminBO);

        return GraceJSONResult.ok();
    }

    @Override
    public GraceJSONResult getAdminList(Integer page, Integer pageSize) {
        if (page == null) {
            page = 1;
        }

        if (pageSize == null) {
            pageSize = 10;
        }

        IPage<AdminUser> adminUserIPage = adminService.queryUserPage(page, pageSize);

        PageGridResult pageGridResult = new PageGridResult();

        pageGridResult.setPage(adminUserIPage.getPages());
        pageGridResult.setRecords(adminUserIPage.getRecords().size());
        pageGridResult.setTotal(adminUserIPage.getTotal());
        pageGridResult.setRows(adminUserIPage.getRecords());

        return GraceJSONResult.ok(pageGridResult);
    }

    @Override
    public GraceJSONResult adminLogout(Long adminId, HttpServletRequest request, HttpServletResponse response) {
        deleteCookie(request, response, "atoken");
        deleteCookie(request, response, "aid");
        deleteCookie(request, response, "aname");

        redisOperator.del(REDIS_ADMIN_TOKEN + ":" + adminId);


        return GraceJSONResult.ok();
    }

    @Override
    public GraceJSONResult adminFaceLogin(FaceLoginBO faceLoginBO, HttpServletRequest request, HttpServletResponse response,  BindingResult bindingResult) {
        // 0. 首先判断参数是否合法
        if (bindingResult.hasErrors()) {
            Map<String, String> bindResultErrors = super.getBindResultErrors(bindingResult);

            GraceJSONResult.errorMap(bindResultErrors);
        }

        // 1. 根据username 将faceId查询出
        AdminUser adminUser = adminService.findUsernameByAdmin(faceLoginBO.getUsername());
        String faceId = adminUser.getFaceId();

        if (StringUtils.isBlank(faceId)) {
            GraceException.display(ResponseStatusEnum.ADMIN_FACE_NULL_ERROR);
        }

        // 2. 根据faceId 将图片下载 并将图片转换为Base64
//        String getFileBase64Url = "http://www.imoocnews.com:8004/fs/getFileBase64ByFaceId?faceId=" + faceId;
//        GraceJSONResult base64Result = restTemplate.getForObject(getFileBase64Url, GraceJSONResult.class);
//

        GraceJSONResult base64Result = fileControllerAPI.getFileBase64ByFaceId(faceId);

        if (base64Result == null) {
            GraceException.display(ResponseStatusEnum.ADMIN_FACE_NULL_ERROR);
        }

        // faceA 数据库中的图片
        String faceBase64A = (String) base64Result.getData();
        if (faceBase64A == null) {
            GraceException.display(ResponseStatusEnum.ADMIN_FACE_NULL_ERROR);
        }

        // faceB 前端验证上传的图片
        String faceBase64B = faceLoginBO.getImg64();


        // 3. 调用阿里云 面部对比API 进行面部识别
        boolean confidenceBoolean   = faceVerifyUtils.verifyFace(faceBase64A, faceBase64B, 70);

        // 4. 根据可信度
        if (!confidenceBoolean) {
            return GraceJSONResult.errorCustom(ResponseStatusEnum.ADMIN_FACE_LOGIN_ERROR);
        }

        setAdminLoginCookie(request, response, adminUser);


        return GraceJSONResult.ok();
    }
}
