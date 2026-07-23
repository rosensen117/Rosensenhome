package com.shiguang.lostfound.auth;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
public class AdminAccountInitializer implements CommandLineRunner {
    private final UserAccountRepository users;
    private final PasswordEncoder encoder;
    private final String account;
    private final String password;

    public AdminAccountInitializer(UserAccountRepository users, PasswordEncoder encoder,
            @Value("${app.admin.account:admin}") String account,
            @Value("${app.admin.password:Admin@123456}") String password) {
        this.users = users;
        this.encoder = encoder;
        this.account = account;
        this.password = password;
    }

    @Override
    public void run(String... args) {
        if (!users.existsByStudentId(account)) {
            users.save(new UserAccount("系统管理员", account, "13900000000", null, encoder.encode(password), Role.ADMIN));
        }
    }
}
