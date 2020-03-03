package com.leyou.common.advice;

import com.leyou.common.enums.ExceptionEnums;
import com.leyou.common.exception.LyException;
import com.leyou.common.vo.ExcceptionResult;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
public class CommonExceptionHandler {
    @ExceptionHandler(LyException.class)
    public ResponseEntity handlerRunTimeException(LyException e){
        ExceptionEnums enums = e.getExceptionEnums();
        return ResponseEntity.status(enums.getCode()).body(new ExcceptionResult(e.getExceptionEnums()));
    }
}
