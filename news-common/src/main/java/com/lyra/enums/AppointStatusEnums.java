package com.lyra.enums;

public enum AppointStatusEnums {
    regular_update(1, "定时上传"),
    not_regular_update(0, "非定时上传");

    private Integer type;
    private String value;

    AppointStatusEnums(Integer type, String value) {
        this.type = type;
        this.value = value;
    }

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
}
