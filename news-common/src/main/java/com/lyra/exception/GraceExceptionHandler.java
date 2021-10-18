package com.lyra.exception;

import com.lyra.result.GraceJSONResult;
import com.lyra.result.ResponseStatusEnum;
import org.apache.tomcat.util.http.fileupload.impl.SizeLimitExceededException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GraceExceptionHandler {
    @ExceptionHandler(MyGraceException.class)
    public GraceJSONResult returnMyGraceException(MyGraceException e) {
        e.printStackTrace();

        return GraceJSONResult.exception(e.getResponseStatusEnum());
    }


    @ExceptionHandler(SizeLimitExceededException.class)
    public GraceJSONResult returnSizeLimitGraceException(SizeLimitExceededException sizeLimitExceededException) {
        sizeLimitExceededException.printStackTrace();

        return GraceJSONResult.exception(ResponseStatusEnum.FILE_MAX_SIZE_ERROR);

    }
}
