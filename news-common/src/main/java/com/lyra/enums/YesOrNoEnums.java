package com.lyra.enums;

public enum YesOrNoEnums {
    YES(1, "删除"),
    NO(0, "未删除");

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

    YesOrNoEnums(Integer type, String value) {
        this.type = type;
        this.value = value;
    }
}
