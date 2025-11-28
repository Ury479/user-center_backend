package com.example.demo.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.example.demo.model.domain.request.UserLoginRequest;
import com.example.demo.model.domain.request.UserRegisterRequest;
import com.example.demo.model.domain.User;
import com.example.demo.service.UserService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.List;

import static com.example.demo.model.domain.User.ADMIN_ROLE;

@RestController
@RequestMapping("/user")
public class UserController {
    @Resource
    private UserService userService;

    @PostMapping("/register")
    public Long userRegister(@RequestBody UserRegisterRequest userRegisterRequest) {

        if (userRegisterRequest == null) {
            return null;
        }

        String userAccount = userRegisterRequest.getUserAccount();
        String userPassword = userRegisterRequest.getUserPassword();
        String checkPassword = userRegisterRequest.getCheckPassword();

        if (StringUtils.isAnyBlank(userAccount, userPassword, checkPassword)) {
            return null;
        }

        // 调用 service 注册
        return userService.userRegister(userAccount, userPassword, checkPassword);
    }


    @PostMapping("/login")
    public User userLogin(@RequestBody UserLoginRequest userLoginRequest, HttpServletRequest request) {
        if (userLoginRequest == null) {
            return null;
        }

        String userAccount = userLoginRequest.getUserAccount();
        String userPassword = userLoginRequest.getUserPassword();

        if (StringUtils.isAnyBlank(userAccount, userPassword)) {
            return null;
        }

        // 正确调用登录方法
        return userService.userLogin(userAccount, userPassword, request);
    }

    /**
     * 获取当前登录用户（前端 Pinia、GlobalHeader、axios 拦截器依赖）
     */
    @GetMapping("/current")
    public User getCurrentUser(HttpServletRequest request) {

        // 从 Session 中读取登录态
        Object userObj = request.getSession().getAttribute(UserService.USER_LOGIN_STATE);

        if (userObj == null) {
            return null;
        }

        // 返回安全脱敏后的用户
        return (User) userObj;
    }

    /**
     * 用户注销 —— 前端 userLogout() 对应的 API（必须补充）
     */
    @PostMapping("/logout")
    public boolean logout(HttpServletRequest request) {
        if (request == null) {
            return false;
        }

        // 清除 Session 登录态
        request.getSession().removeAttribute(UserService.USER_LOGIN_STATE);
        return true;
    }

    @GetMapping("/search")
    public List<User> searchUsers(String username, HttpServletRequest request) {
        if (!isAdmin(request)) {
            return new ArrayList<>();
        }
        QueryWrapper<User> queryWrapper = new QueryWrapper<>();
        if (!StringUtils.isAnyBlank(username)) {
            queryWrapper.like("username",username);
        }
        return userService.list(queryWrapper);
    }

    @PostMapping("/delete")
    public boolean deleteUsers(@RequestParam long id, HttpServletRequest request) {
        if (!isAdmin(request)) {
            return false;
        }
        if (id <= 0) {
            return false;
        }
        return userService.removeById(id);
    }

    /**
     * 是否为管理员
     * @param request
     * @return
     */
    private boolean isAdmin(HttpServletRequest request) {
        // 从 session 取登录用户
        User user = (User) request.getSession().getAttribute(UserService.USER_LOGIN_STATE);

        // 判断是否管理员
        return user != null
                && user.getUserRole() != null
                && user.getUserRole() == User.ADMIN_ROLE;

    }

}
