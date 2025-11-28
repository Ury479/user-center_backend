package com.example.demo.model.domain;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 * User entity matching table "user".
 */
@TableName("user")
@Data
public class User implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId(type = IdType.AUTO)
    private Long id;

    private String username;

    @TableField("user_account")
    private String userAccount;

    @TableField("avatar_url")
    private String avatarUrl;

    private Integer gender;

    @TableField("user_password")
    private String userPassword;

    /**
     * phone number
     */
    @TableField("phone")
    private String phone;

    /**
     * email
     */
    @TableField("email")
    private String email;

    @TableField("user_status")
    private Integer userStatus;

    @TableField(value = "create_time", fill = FieldFill.INSERT)
    private Date createTime;

    @TableField(value = "update_time", fill = FieldFill.INSERT_UPDATE)
    private Date updateTime;

    @TableLogic               // 新增这一行，和教程保持一致
    @TableField("is_delete")  // 指定数据库列名
    private Integer isDelete;

    /**
     * 关键新增字段 —— 用户角色（教程中 admin=1, 普通用户=0）
     */
    @TableField("userRole")
    private Integer userRole;

    /**
     * default role
     */
    public static final int DEFAULT_ROLE = 0;

    public static final int ADMIN_ROLE = 1;

}
