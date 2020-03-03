package com.leyou.common.vo;

import com.leyou.common.enums.ExceptionEnums;
import lombok.Data;

@Data
public class ExcceptionResult {
    private int status;
    private String mssage;
    private Long timestamp;

    public ExcceptionResult(ExceptionEnums em) {
        this.status = em.getCode();
        this.mssage = em.getMsg();
        this.timestamp = System.currentTimeMillis();
    }
}
