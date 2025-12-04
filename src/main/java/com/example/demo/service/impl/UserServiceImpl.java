package com.example.demo.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.demo.exception.BusinessException;
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
import com.example.demo.common.ErrorCode;


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
    public long userRegister(String userAccount, String userPassword, String checkPassword, String planetCode) {
        // 1. Basic null / blank check
        if (StringUtils.isAnyBlank(userAccount, userPassword, checkPassword, planetCode)) {
            log.info("user register failed: params are blank");
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "所有字段不能为空");
        }

        // 2. Length validation
        if (userAccount.length() < 4) {
            log.info("user register failed: userAccount length < 4");
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "账号长度不能小于 4 位");
        }
        if (userPassword.length() < 8 || checkPassword.length() < 8) {
            log.info("user register failed: password length < 8");
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "密码长度不能小于 8 位");
        }

        // simple length check for planetCode
        if (planetCode.length() > 12) {
            log.info("user register failed: planetCode length too long");
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "星球编号长度不能超过 12 位");
        }

        // 3. Illegal character check
        Pattern invalidPattern = Pattern.compile("[^a-zA-Z0-9_]");
        Matcher matcher = invalidPattern.matcher(userAccount);
        if (matcher.find()) {
            log.info("user register failed: userAccount contains invalid characters");
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "账号只能包含字母、数字和下划线");
        }

        // 4. Password and checkPassword must be the same
        if (!userPassword.equals(checkPassword)) {
            log.info("user register failed: password and checkPassword are not the same");
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "两次输入的密码不一致");
        }

        // 5. Account uniqueness check
        QueryWrapper<User> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("user_account", userAccount);
        long count = this.count(queryWrapper);
        if (count > 0) {
            log.info("user register failed: userAccount already exists");
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "账号已存在，请更换账号");
        }

        // 6. planetCode uniqueness check
        QueryWrapper<User> planetWrapper = new QueryWrapper<>();
        planetWrapper.eq("planetCode", planetCode);
        long planetCount = this.count(planetWrapper);
        if (planetCount > 0) {
            log.info("user register failed: planetCode already exists");
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "星球编号已存在，请更换编号");
        }

        // 7. Encrypt password
        String encrytPassword = DigestUtils.md5DigestAsHex((SALT + userPassword).getBytes());

        User user = new User();
        user.setUserAccount(userAccount);
        user.setUserPassword(encrytPassword);
        user.setPlanetCode(planetCode);

        // 8. Save user
        boolean saveResult = this.save(user);
        if (!saveResult) {
            log.info("user register failed: save user to database failed");
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "用户保存失败，请稍后重试");
        }

        return user.getId() == null ? -1 : user.getId();
    }


    @Override
    public long userRegister(String userAccount, String userPassword, String checkPassword) {
        return 0;
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
        // 5. 脱敏用户

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
        // ⭐ ALSO copy planetCode to safety user
        safetyUser.setPlanetCode(user.getPlanetCode());

        // 6. Save login state in session
        if (request != null) {
            request.getSession().setAttribute(USER_LOGIN_STATE, safetyUser);
        }

        return safetyUser;
    }

    @Override
    public int userLogout(HttpServletRequest request) {
        if (request == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "请求为空");
        }
        request.getSession().removeAttribute(USER_LOGIN_STATE);
        return 1;
    }


    /**
     * Build a safety user object without sensitive information.
     * This method is used when returning user data to frontend
     * or storing user information into session.
     *
     * @param originUser user loaded from database
     * @return safety user with desensitized fields
     */
    public User getSafetyUser(User originUser) {
        if (originUser == null) {
            return null;
        }

        User safetyUser = new User();
        safetyUser.setId(originUser.getId());
        safetyUser.setUsername(originUser.getUsername());
        safetyUser.setUserAccount(originUser.getUserAccount());
        safetyUser.setAvatarUrl(originUser.getAvatarUrl());
        safetyUser.setGender(originUser.getGender());

        // Never expose password
        safetyUser.setUserPassword(null);

        safetyUser.setPhone(originUser.getPhone());
        safetyUser.setEmail(originUser.getEmail());
        safetyUser.setUserStatus(originUser.getUserStatus());
        safetyUser.setCreateTime(originUser.getCreateTime());
        // you can also use originUser.getUpdateTime()
        safetyUser.setUpdateTime(new Date());

        // copy logic related fields
        safetyUser.setIsDelete(originUser.getIsDelete());
        safetyUser.setUserRole(originUser.getUserRole());
        safetyUser.setPlanetCode(originUser.getPlanetCode());

        return safetyUser;
    }


}




