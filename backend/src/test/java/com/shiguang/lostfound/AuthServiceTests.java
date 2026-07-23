package com.shiguang.lostfound;

import com.shiguang.lostfound.auth.AuthDtos.LoginRequest;
import com.shiguang.lostfound.auth.AuthDtos.RegisterRequest;
import com.shiguang.lostfound.auth.AuthService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
class AuthServiceTests {
    @Autowired AuthService authService;

    @Test
    void registerLoginAndTokenAuthenticationWork() {
        String suffix = Long.toString(System.nanoTime()).substring(5);
        String studentId = "J" + suffix.substring(0, Math.min(10, suffix.length()));
        String phone = "137" + suffix.substring(0, 8);
        var registered = authService.register(new RegisterRequest("测试用户", studentId, phone, "test@campus.edu.cn", "Test@123456"));
        assertThat(authService.authenticate(registered.token()).getStudentId()).isEqualTo(studentId);
        var loggedIn = authService.login(new LoginRequest(studentId, "Test@123456"), false);
        assertThat(authService.authenticate(loggedIn.token()).getName()).isEqualTo("测试用户");
    }
}
