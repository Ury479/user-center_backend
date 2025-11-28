package com.example.demo.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.demo.model.domain.User;
import com.example.demo.service.UserService;
import com.example.demo.mapper.UserMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.apache.commons.lang3.StringUtils;
import org.springframework.util.DigestUtils;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.Date;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


/**
* @author ury
* @description 针对表【user】的数据库操作Service实现
* @createDate 2025-11-20 09:10:04
*/
@Service
@Slf4j
public class UserServiceImpl extends ServiceImpl<UserMapper, User>
    implements UserService{

    @Resource
    private UserMapper userMapper;

    /**
     * 盐值：混淆密码
     */
    private static final String SALT = "1234567890";
    /**
     *
     */
    public static final String USER_LOGIN_STATE = "userLoginState";


    @Override
    public long userRegister(String userAccount, String userPassword, String checkPassword) {
        // 1. Basic null / blank check
        if (StringUtils.isAnyBlank(userAccount, userPassword, checkPassword)) {
            log.info("user register failed: params are blank");
            return -1;
        }

        // 2. Length validation
        if (userAccount.length() < 4) {
            log.info("user register failed: userAccount length < 4");
            return -1;
        }
        if (userPassword.length() < 8 || checkPassword.length() < 8) {
            log.info("user register failed: password length < 8");
            return -1;
        }

        Pattern invalidPattern = Pattern.compile("[^a-zA-Z0-9_]");
        Matcher matcher = invalidPattern.matcher(userAccount);
        if (matcher.find()) {  // find means contains invalid char
            log.info("user register failed: userAccount contains invalid characters");
            return -1;
        }


        // 4. Password and checkPassword must be the same
        if (!userPassword.equals(checkPassword)) {
            log.info("user register failed: password and checkPassword are not the same");
            return -1;
        }

        // 5. Account uniqueness check
        QueryWrapper<User> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("user_account", userAccount);
        long count = this.count(queryWrapper);
        if (count > 0) {
            log.info("user register failed: userAccount already exists");
            return -1;
        }

        final String SALT = "1234567890";
        String encrytPassword = DigestUtils.md5DigestAsHex((SALT + userPassword).getBytes());

        User user = new User();
        user.setUserAccount(userAccount);
        user.setUserPassword(encrytPassword);
        boolean saveResult = this.save(user);
        if (!saveResult) {
            log.info("user register failed: save user to database failed");
            return -1;
        }
        return user.getId() == null ? -1 : user.getId();
    }

    @Override
    public User userLogin(String userAccount, String userPassword, HttpServletRequest request) {
        // 1. Basic null / blank check
        if (StringUtils.isAnyBlank(userAccount, userPassword)) {
            log.info("user login failed: params are blank");
            return null;
        }

        // 2. Length validation
        if (userAccount.length() < 4) {
            log.info("user login failed: userAccount length < 4");
            return null;
        }
        if (userPassword.length() < 8) {
            log.info("user login failed: password length < 8");
             return null;
        }

        // check invalid characters
        Pattern invalidPattern = Pattern.compile("[^a-zA-Z0-9_]");
        Matcher matcher = invalidPattern.matcher(userAccount);
        if (matcher.find()) {
            log.info("user login failed: userAccount contains invalid characters");
            return null;
        }

        // 3. Encrypt password
        String encrytPassword = DigestUtils.md5DigestAsHex((SALT + userPassword).getBytes());

        // build query: **注意用数据库字段名**
        QueryWrapper<User> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("user_account", userAccount);
        queryWrapper.eq("user_password", encrytPassword);

        User user = this.getOne(queryWrapper);
        if (user == null) {
            log.info("user login failed: userAccount and userPassword do not match");
            return null;
        }

        // 5. Remove password before returning
        user.setUserPassword(null);

        //用户信息脱敏
        User safetyUser = new User();
        safetyUser.setId(user.getId());
        safetyUser.setUsername(user.getUsername());
        safetyUser.setUserAccount(user.getUserAccount());
        safetyUser.setAvatarUrl(user.getAvatarUrl());
        safetyUser.setGender(user.getGender());
        safetyUser.setUserPassword(user.getUserPassword());
        safetyUser.setPhone(user.getPhone());
        safetyUser.setEmail(user.getEmail());
        safetyUser.setUserStatus(user.getUserStatus());
        safetyUser.setCreateTime(user.getCreateTime());
        safetyUser.setUpdateTime(new Date());
        // ⭐关键：把 isDelete 和 userRole 也拷贝过来
        safetyUser.setIsDelete(user.getIsDelete());
        safetyUser.setUserRole(user.getUserRole());

        // 6. Save login state in session
        if (request != null) {
            request.getSession().setAttribute(USER_LOGIN_STATE, safetyUser);
        }

        return safetyUser;
    }

}




