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
        assertTrue(result);
    }

    @Test
    void userRegister() {
        // 1. 账号正常，密码为空 -> 失败
        String userAccount = "yupi";
        String userPassword = "";
        String checkPassword = "123456";
        String planetCode = "";
        long result = userService.userRegister(userAccount, userPassword, checkPassword,planetCode);
        assertEquals(-1, result);

        // 1. 任意字段为空 -> 失败（这里先让密码为空）
        userAccount = "yupi";
        userPassword = "";
        checkPassword = "12345678";
        planetCode = "001";
        result = userService.userRegister(userAccount, userPassword, checkPassword, planetCode);
        assertEquals(-1, result);

        // 2. 账号过短 -> 失败
        userAccount = "yu";
        userPassword = "12345678";
        checkPassword = "12345678";
        planetCode = "001";
        result = userService.userRegister(userAccount, userPassword, checkPassword, planetCode);
        assertEquals(-1, result);

        // 3. 密码过短 -> 失败（< 8 位）
        userAccount = "yupi";
        userPassword = "123456";
        checkPassword = "123456";
        planetCode = "001";
        result = userService.userRegister(userAccount, userPassword, checkPassword, planetCode);
        assertEquals(-1, result);

        // 4. 账号包含非法字符（空格） -> 失败
        userAccount = "yupi  ";
        userPassword = "12345678";
        checkPassword = "12345678";
        planetCode = "001";
        result = userService.userRegister(userAccount, userPassword, checkPassword, planetCode);
        assertEquals(-1, result);

        // 5. 密码和校验密码不一致 -> 失败
        userAccount = "yupi";
        userPassword = "12345678";
        checkPassword = "123456789";
        planetCode = "001";
        result = userService.userRegister(userAccount, userPassword, checkPassword, planetCode);
        assertEquals(-1, result);

        // 6. planetCode 为空 -> 失败（如果你在 service 里对 planetCode 做了非空校验）
        userAccount = "yupi_planet";
        userPassword = "12345678";
        checkPassword = "12345678";
        planetCode = "";
        result = userService.userRegister(userAccount, userPassword, checkPassword, planetCode);
        assertEquals(-1, result);

        // 7. planetCode 重复 -> 失败
        // 先用一个正常数据注册一次
        userAccount = "yupi_unique_" + System.currentTimeMillis();
        userPassword = "12345678";
        checkPassword = "12345678";
        planetCode = "p001";
        long firstId = userService.userRegister(userAccount, userPassword, checkPassword, planetCode);
        assertTrue(firstId > 0);

        // 再用同一个 planetCode 注册第二个账号，应失败（-1）
        userAccount = "yupi_another";
        userPassword = "12345678";
        checkPassword = "12345678";
        // planetCode 仍然为 "p001"
        result = userService.userRegister(userAccount, userPassword, checkPassword, planetCode);
        assertEquals(-1, result);

        // 8. ✅ 完全合法：账号 / 密码 / 校验密码一致 + planetCode 合法且不重复 -> 成功
        userAccount = "yupi_" + System.currentTimeMillis();
        userPassword = "12345678";
        checkPassword = "12345678";
        planetCode = "p" + System.currentTimeMillis(); // 确保 planetCode 也不重复

        result = userService.userRegister(userAccount, userPassword, checkPassword, planetCode);
        System.out.println("register result = " + result);
        assertTrue(result > 0);
    }

}
