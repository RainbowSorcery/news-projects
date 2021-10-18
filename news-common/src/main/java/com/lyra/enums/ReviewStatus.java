package com.lyra.enums;

public enum ReviewStatus {

    pass("pass", "审核通过"),
    review("review", "结果不确定，需要进行人工审核"),
    block("block", "结果违规 审核失败");

    private String type;
    private String value;


    ReviewStatus(String type, String value) {
        this.type = type;
        this.value = value;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }
}
