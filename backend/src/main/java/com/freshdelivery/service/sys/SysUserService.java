package com.freshdelivery.service.sys;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.freshdelivery.common.PageResult;
import com.freshdelivery.common.exception.BusinessException;
import com.freshdelivery.entity.sys.SysUser;
import com.freshdelivery.entity.sys.SysUserRole;
import com.freshdelivery.mapper.sys.SysUserMapper;
import com.freshdelivery.mapper.sys.SysUserRoleMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class SysUserService extends ServiceImpl<SysUserMapper, SysUser> {

    @Autowired private SysUserRoleMapper userRoleMapper;
    @Autowired private PasswordEncoder passwordEncoder;

    @Transactional
    public SysUser create(SysUser user, List<Long> roleIds) {
        if (this.exists(new LambdaQueryWrapper<SysUser>().eq(SysUser::getUsername, user.getUsername()))) {
            throw new BusinessException("用户名已存在");
        }
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        user.setStatus(1);
        user.setCreatedAt(LocalDateTime.now());
        this.save(user);

        for (Long roleId : roleIds) {
            SysUserRole ur = new SysUserRole();
            ur.setUserId(user.getId());
            ur.setRoleId(roleId);
            userRoleMapper.insert(ur);
        }
        return user;
    }

    @Transactional
    public void update(Long id, SysUser user, List<Long> roleIds) {
        SysUser existing = this.getById(id);
        if (existing == null) throw new BusinessException("用户不存在");
        user.setId(id);
        user.setPassword(null);
        this.updateById(user);

        userRoleMapper.delete(new LambdaQueryWrapper<SysUserRole>().eq(SysUserRole::getUserId, id));
        if (roleIds != null && !roleIds.isEmpty()) {
            for (Long roleId : roleIds) {
                SysUserRole ur = new SysUserRole();
                ur.setUserId(id);
                ur.setRoleId(roleId);
                userRoleMapper.insert(ur);
            }
        }
    }

    public void delete(Long id) {
        if (this.getById(id) == null) throw new BusinessException("用户不存在");
        userRoleMapper.delete(new LambdaQueryWrapper<SysUserRole>().eq(SysUserRole::getUserId, id));
        this.removeById(id);
    }

    public SysUser detail(Long id) {
        SysUser user = this.getById(id);
        if (user == null) throw new BusinessException("用户不存在");
        user.setPassword(null);
        return user;
    }

    public PageResult<SysUser> page(int pageNum, int pageSize, String keyword) {
        LambdaQueryWrapper<SysUser> wrapper = new LambdaQueryWrapper<>();
        if (keyword != null && !keyword.isBlank()) {
            wrapper.and(w -> w.like(SysUser::getUsername, keyword)
                    .or().like(SysUser::getRealName, keyword));
        }
        wrapper.orderByDesc(SysUser::getCreatedAt);

        com.baomidou.mybatisplus.extension.plugins.pagination.Page<SysUser> p =
                this.page(new com.baomidou.mybatisplus.extension.plugins.pagination.Page<>(pageNum, pageSize), wrapper);
        PageResult<SysUser> result = new PageResult<>();
        result.setRecords(p.getRecords());
        result.setTotal(p.getTotal());
        result.setPageNum(pageNum);
        result.setPageSize(pageSize);
        return result;
    }

    public SysUser getUserInfo(String username) {
        SysUser user = this.getOne(
                new LambdaQueryWrapper<SysUser>().eq(SysUser::getUsername, username));
        if (user == null) throw new BusinessException("用户不存在");
        user.setPassword(null);
        return user;
    }
}