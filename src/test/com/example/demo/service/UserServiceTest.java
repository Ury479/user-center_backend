package com.example.demo.service;

import com.example.demo.model.domain.User;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import javax.annotation.Resource;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
public class UserServiceTest {

    @Resource
    private UserService userService;

    @Test
    public void testAddUser() {
        User user = new User();
        user.setUsername("dogYupi");
        // 使用时间戳拼接，保证每次运行账号不同
        user.setUserAccount("testUser_" + System.currentTimeMillis());
        user.setAvatarUrl("https://636f-codenav-8qrj8px727565176-1256524210.tcb.qcloud.la/avatar.jpg");
        user.setGender(0);
        user.setUserPassword("xxx");
        user.setPhone("123");
        user.setEmail("456");

        boolean result = userService.save(user);
        System.out.println(user.getId());
        Assertions.assertTrue(result);
    }

    @Test
    void userRegister() {
        // 1. 账号正常，密码为空 -> 失败
        String userAccount = "yupi";
        String userPassword = "";
        String checkPassword = "123456";
        long result = userService.userRegister(userAccount, userPassword, checkPassword);
        Assertions.assertEquals(-1, result);

        // 2. 账号过短 -> 失败
        userAccount = "yu";
        result = userService.userRegister(userAccount, userPassword, checkPassword);
        Assertions.assertEquals(-1, result);

        // 3. 密码过短 -> 失败
        userAccount = "yupi";
        userPassword = "123456";
        checkPassword = "123456";
        result = userService.userRegister(userAccount, userPassword, checkPassword);
        Assertions.assertEquals(-1, result);

        // 4. 账号含空格（非法字符） -> 失败
        userAccount = "yupi  ";
        userPassword = "12345678";
        checkPassword = "12345678";
        result = userService.userRegister(userAccount, userPassword, checkPassword);
        Assertions.assertEquals(-1, result);

        // 5. 密码和校验密码不一致 -> 失败
        userAccount = "yupi";
        userPassword = "12345678";
        checkPassword = "123456789";
        result = userService.userRegister(userAccount, userPassword, checkPassword);
        Assertions.assertEquals(-1, result);

        // 6. ✅ 合法账号 + 合法密码 + 校验密码一致 + 不重复 -> 应该成功
        userAccount = "yupi_" + System.currentTimeMillis();  // 确保不重复
        userPassword = "12345678";
        checkPassword = "12345678";                          // ⚠️ 一定要改回和 userPassword 一样

        result = userService.userRegister(userAccount, userPassword, checkPassword);
        System.out.println("register result = " + result);
        Assertions.assertTrue(result > 0);
    }

}
