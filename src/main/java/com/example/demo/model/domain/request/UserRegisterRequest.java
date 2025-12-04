package com.example.demo.model.domain.request;

import lombok.Data;

import java.io.Serializable;

/**
 * @author yupi
 */
@Data
public class UserRegisterRequest implements Serializable {

    private static final long serialVersionUID = -397797868934200356L;

    private String userAccount;

    private String userPassword;

    private String  checkPassword;

    /**
     * 星球编号（和教程里的 planetCode 保持一致）
     */
    private String planetCode;
}
