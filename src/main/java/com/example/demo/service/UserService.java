package com.example.demo.service;

import com.example.demo.model.domain.User;
import com.baomidou.mybatisplus.extension.service.IService;

import javax.servlet.http.HttpServletRequest;

/**
* @author ury
* @description 针对表【user】的数据库操作Service
* @createDate 2025-11-20 09:10:04
*/
public interface UserService extends IService<User> {
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
    User doLogin(String userAccount, String userPassword,HttpServletRequest request);
}
