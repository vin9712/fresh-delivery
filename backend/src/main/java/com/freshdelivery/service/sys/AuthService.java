package com.freshdelivery.service.sys;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.freshdelivery.entity.sys.SysUser;
import com.freshdelivery.mapper.sys.SysUserMapper;
import com.freshdelivery.common.exception.BusinessException;
import com.freshdelivery.security.JwtUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final AuthenticationManager authenticationManager;
    private final SysUserMapper userMapper;
    private final JwtUtil jwtUtil;

    public AuthVO login(String username, String password) {
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(username, password));

        SysUser user = userMapper.selectOne(
                new LambdaQueryWrapper<SysUser>().eq(SysUser::getUsername, username));
        if (user == null) {
            throw new BusinessException(401, "用户不存在");
        }

        String token = jwtUtil.generateToken(username);
        return new AuthVO(user.getId(), user.getUsername(), user.getRealName(), token);
    }

    public record AuthVO(Long userId, String username, String realName, String token) {}
}