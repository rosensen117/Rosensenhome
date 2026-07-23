package com.shiguang.lostfound.auth;

import jakarta.validation.Valid;
import org.springframework.http.HttpHeaders;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import static com.shiguang.lostfound.auth.AuthDtos.*;

@RestController
@RequestMapping("/api/auth")
public class AuthController {
    private final AuthService authService;

    public AuthController(AuthService authService) { this.authService = authService; }

    @PostMapping("/register")
    public AuthResponse register(@Valid @RequestBody RegisterRequest request) { return authService.register(request); }

    @PostMapping("/login")
    public AuthResponse login(@Valid @RequestBody LoginRequest request) { return authService.login(request, false); }

    @PostMapping("/admin/login")
    public AuthResponse adminLogin(@Valid @RequestBody LoginRequest request) { return authService.login(request, true); }

    @GetMapping("/me")
    public UserResponse me(Authentication authentication) { return authService.currentUser(authentication.getName()); }

    @PostMapping("/logout")
    public MessageResponse logout(@RequestHeader(value = HttpHeaders.AUTHORIZATION, required = false) String authorization) {
        String token = authorization != null && authorization.startsWith("Bearer ") ? authorization.substring(7) : null;
        authService.logout(token);
        return new MessageResponse("已退出登录");
    }
}
