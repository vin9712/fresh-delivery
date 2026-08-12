package com.freshdelivery.controller.sys;

import com.freshdelivery.common.Result;
import com.freshdelivery.entity.sys.SysUser;
import com.freshdelivery.service.sys.AuthService;
import com.freshdelivery.service.sys.CurrentUser;
import com.freshdelivery.service.sys.SysUserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;
    private final SysUserService userService;

    @PostMapping("/login")
    public Result<AuthService.AuthVO> login(@Valid @RequestBody LoginRequest req) {
        return Result.ok(authService.login(req.username(), req.password()));
    }

    @GetMapping("/info")
    public Result<SysUser> info() {
        String username = CurrentUser.getUsername();
        if (username == null) return Result.error(401, "未登录");
        return Result.ok(userService.getUserInfo(username));
    }

    public record LoginRequest(String username, String password) {}
}