package com.example.demo.exception;

import com.example.demo.common.ErrorCode;
import lombok.Data;

@Data
public class BusinessException extends RuntimeException {
    private int code;
    private String description;

    public BusinessException(ErrorCode errorCode) {
        super(errorCode.getMessage());
        this.code = errorCode.getCode();
        this.description = errorCode.getDescription();
    }

    public BusinessException(ErrorCode errorCode, String description) {
        super(errorCode.getMessage());
        this.code = errorCode.getCode();
        this.description = description;  // ✔ 这里是对 description 的覆写（Yupi 教程重点）
    }

    public BusinessException(int code, String message, String description) {
        super(message);
        this.code = code;
        this.description = description;
    }
}
