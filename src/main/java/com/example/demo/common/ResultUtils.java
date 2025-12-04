package com.example.demo.common;

public class ResultUtils {
    /**
     * 成功（带数据）
     */
    public static <T> BaseResponse<T> success(T data) {
        return new BaseResponse<>(
                ErrorCode.SUCCESS.getCode(),
                data,
                ErrorCode.SUCCESS.getMessage(),
                ""
        );
    }

    /**
     * 只根据 ErrorCode 返回错误（不关心 data 类型）
     */
    public static BaseResponse<?> error(ErrorCode errorCode) {
        return new BaseResponse<>(
                errorCode.getCode(),
                null,
                errorCode.getMessage(),
                errorCode.getDescription()
        );
    }

    /**
     * 自定义 message / description 的错误
     */
    public static BaseResponse<?> error(ErrorCode errorCode, String message, String description) {
        return new BaseResponse<>(
                errorCode.getCode(),
                null,
                message,
                description
        );
    }

    /**
     * 自定义 message / description 的错误
     */
    public static BaseResponse<?> error(ErrorCode errorCode, String description) {
        return new BaseResponse<>(
                errorCode.getCode(),
                null,
                errorCode.getMessage(),
                description
        );
    }

    /**
     * 直接传入 code / message / description 的错误
     *（不走 ErrorCode 枚举的兜底方案，可选）
     */
    public static BaseResponse<?> error(int code, String message, String description) {
        return new BaseResponse<>(
                code,
                null,
                message,
                description
        );
    }
}
