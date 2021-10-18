package com.lyra.exception;

import com.lyra.result.ResponseStatusEnum;

public class MyGraceException extends RuntimeException {
    private ResponseStatusEnum responseStatusEnum;

    public MyGraceException(ResponseStatusEnum responseStatusEnum) {
        super("状态码:" + responseStatusEnum.status() + "\t" + "异常信息:" + responseStatusEnum.msg());
        this.responseStatusEnum = responseStatusEnum;
    }

    public ResponseStatusEnum getResponseStatusEnum() {
        return responseStatusEnum;
    }

    public void setResponseStatusEnum(ResponseStatusEnum responseStatusEnum) {
        this.responseStatusEnum = responseStatusEnum;
    }
}
