package com.example.demo.service;

import com.example.demo.model.domain.User;
import com.baomidou.mybatisplus.extension.service.IService;

import javax.servlet.http.HttpServletRequest;

/**
* @author ury
* @description 针对表【user】的数据库操作Service
* @createDate 2025-11-20 09:10:04
 * 星球注释
*/
public interface UserService extends IService<User> {
    String USER_LOGIN_STATE = "userLoginState";

    long userRegister(String userAccount, String userPassword, String checkPassword, String planetCode);

    /**
     *
     * @param userAccount
     * @param userPassword
     * @param checkPassword
     * @return
     */
    long userRegister(String userAccount, String userPassword,String checkPassword);

    /**
     *
     * @param userAccount
     * @param userPassword
     * @return
     */
    User userLogin(String userAccount, String userPassword,HttpServletRequest request);

    /**
     * 用户注销
     */
    int userLogout(HttpServletRequest request);

    /**
     * 用户信息脱敏
     */
    User getSafetyUser(User originUser);
}
