package com.lyra.pojo.bo;

import com.fasterxml.jackson.annotation.JsonFormat;
import org.hibernate.validator.constraints.Length;

import javax.validation.constraints.*;
import java.util.Date;

public class UpdateUserBO {
    @NotBlank(message = "用户不能Id为空")
    private String  id;
    @NotBlank(message = "城市不能为空")
    private String city;
    @NotBlank(message = "地区不能为空")
    private String district;
    @NotBlank(message = "email不能为空")
    @Email
    private String email;
    @NotBlank(message = "头像不能为空")
    private String face;

    @NotBlank(message = "用户名不能为空")
    @Length(max = 12)
    private String nickname;
    @NotBlank(message = "省份不能为空")
    private String province;
    @NotBlank(message = "真实姓名不能为空")
    private String realname;

    @NotNull
    @Min(value = 0, message = "性别选择错误")
    @Max(value = 1, message = "性别选择错误")
    private Integer sex;
    @NotNull
    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd")
    private Date birthday;


    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getDistrict() {
        return district;
    }

    public void setDistrict(String district) {
        this.district = district;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getFace() {
        return face;
    }

    public void setFace(String face) {
        this.face = face;
    }

    public String getNickname() {
        return nickname;
    }

    public void setNickname(String nickname) {
        this.nickname = nickname;
    }

    public String getProvince() {
        return province;
    }

    public void setProvince(String province) {
        this.province = province;
    }

    public String getRealname() {
        return realname;
    }

    public void setRealname(String realname) {
        this.realname = realname;
    }

    public Integer getSex() {
        return sex;
    }

    public void setSex(Integer sex) {
        this.sex = sex;
    }

    public Date getBirthday() {
        return birthday;
    }

    public void setBirthday(Date birthday) {
        this.birthday = birthday;
    }
}
