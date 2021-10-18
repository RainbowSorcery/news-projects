package com.lyra.enums;

public enum ArticleTypeEnum {
    ONE_IMAGE(1, "单封面"),
    NO_IMAGE(2, "无封面");

    private Integer type;
    private String value;

    public Integer getType() {
        return type;
    }

    public void setType(Integer type) {
        this.type = type;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    ArticleTypeEnum(Integer type, String value) {
        this.type = type;
        this.value = value;
    }
}
