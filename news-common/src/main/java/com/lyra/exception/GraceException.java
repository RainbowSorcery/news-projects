package com.lyra.exception;

import com.lyra.result.ResponseStatusEnum;
import org.springframework.boot.context.properties.bind.BindResult;

public class GraceException {
    public static void display(ResponseStatusEnum responseStatusEnum) {
        throw new MyGraceException(responseStatusEnum);
    }
}
