package com.lyra.enums;

public enum ArticleStatusEnums {
    auditing(1, "审核中"),
    manual_audit(2, "等待人工审核"),
    audit_success(3, "审核成功"),
    audit_failed(4, "审核未通过"),
    article_without(5, "文章撤回");
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

    ArticleStatusEnums(Integer type, String value) {
        this.type = type;
        this.value = value;
    }
}
