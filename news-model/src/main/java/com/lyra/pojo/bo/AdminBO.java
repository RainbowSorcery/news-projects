package com.lyra.pojo.bo;

import javax.validation.constraints.NotBlank;

public class AdminBO {
    @NotBlank
    private String adminName;
    private String confirmPassword;
    private String password;
    @NotBlank
    private String username;
    private String img64;
    private String faceId;

    @Override
    public String toString() {
        return "AdminBO{" +
                "adminName='" + adminName + '\'' +
                ", confirmPassword='" + confirmPassword + '\'' +
                ", password='" + password + '\'' +
                ", username='" + username + '\'' +
                ", img64='" + img64 + '\'' +
                ", faceId='" + faceId + '\'' +
                '}';
    }

    public String getAdminName() {
        return adminName;
    }

    public void setAdminName(String adminName) {
        this.adminName = adminName;
    }

    public String getConfirmPassword() {
        return confirmPassword;
    }

    public void setConfirmPassword(String confirmPassword) {
        this.confirmPassword = confirmPassword;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getImg64() {
        return img64;
    }

    public void setImg64(String img64) {
        this.img64 = img64;
    }

    public String getFaceId() {
        return faceId;
    }

    public void setFaceId(String faceId) {
        this.faceId = faceId;
    }
}
