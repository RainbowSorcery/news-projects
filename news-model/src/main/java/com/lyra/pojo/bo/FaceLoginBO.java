package com.lyra.pojo.bo;

import javax.validation.constraints.NotBlank;

public class FaceLoginBO {
    @NotBlank
    private String img64;
    @NotBlank
    private String username;

    public String getImg64() {
        return img64;
    }

    public void setImg64(String img64) {
        this.img64 = img64;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }
}
