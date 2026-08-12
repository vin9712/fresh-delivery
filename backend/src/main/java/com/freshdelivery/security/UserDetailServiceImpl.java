package com.freshdelivery.security;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.freshdelivery.entity.sys.SysUser;
import com.freshdelivery.entity.sys.SysUserSecurity;
import com.freshdelivery.mapper.sys.SysUserMapper;
import com.freshdelivery.mapper.sys.SysRoleMapper;
import com.freshdelivery.mapper.sys.SysUserRoleMapper;
import com.freshdelivery.common.exception.BusinessException;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class UserDetailServiceImpl implements UserDetailsService {

    private final SysUserMapper userMapper;
    private final SysRoleMapper roleMapper;
    private final SysUserRoleMapper userRoleMapper;

    @Override
    public UserDetails loadUserByUsername(String username) {
        SysUser user = userMapper.selectOne(
                new LambdaQueryWrapper<SysUser>().eq(SysUser::getUsername, username));
        if (user == null) {
            throw new BusinessException(401, "用户不存在");
        }
        if (user.getStatus() == 0) {
            throw new BusinessException(401, "用户已被禁用");
        }

        List<String> permissions = roleMapper.findPermissionsByUserId(user.getId());
        return new SysUserSecurity(user, permissions);
    }
}