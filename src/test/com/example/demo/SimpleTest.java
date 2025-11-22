package com.example.demo;

import com.example.demo.mapper.UserMapper;
import com.example.demo.model.domain.User;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.List;

@SpringBootTest(classes = UserCenterApplication.class) // 启动整个 SpringBoot 容器
public class SimpleTest {

    @Autowired                                       // 这里就写 @Autowired 就行
    private UserMapper userMapper;

    @Test
    public void testSelect() {
        System.out.println("------ selectAll method test ------");

        List<User> userList = userMapper.selectList(null);

        Assertions.assertEquals(5, userList.size());

        userList.forEach(System.out::println);
    }
}

