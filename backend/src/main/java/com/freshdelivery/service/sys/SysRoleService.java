package com.freshdelivery.service.sys;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.freshdelivery.entity.sys.SysPermission;
import com.freshdelivery.entity.sys.SysRole;
import com.freshdelivery.entity.sys.SysRolePermission;
import com.freshdelivery.mapper.sys.SysPermissionMapper;
import com.freshdelivery.mapper.sys.SysRoleMapper;
import com.freshdelivery.mapper.sys.SysRolePermissionMapper;
import com.freshdelivery.common.exception.BusinessException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class SysRoleService extends ServiceImpl<SysRoleMapper, SysRole> {

    @Autowired private SysRolePermissionMapper rolePermissionMapper;
    @Autowired private SysPermissionMapper permissionMapper;

    @Transactional
    public SysRole create(SysRole role, List<Long> permissionIds) {
        this.save(role);
        for (Long pid : permissionIds) {
            SysRolePermission rp = new SysRolePermission();
            rp.setRoleId(role.getId());
            rp.setPermissionId(pid);
            rolePermissionMapper.insert(rp);
        }
        return role;
    }

    @Transactional
    public void update(Long id, SysRole role, List<Long> permissionIds) {
        if (this.getById(id) == null) throw new BusinessException("角色不存在");
        role.setId(id);
        this.updateById(role);

        rolePermissionMapper.delete(new LambdaQueryWrapper<>(SysRolePermission.class)
                .eq(SysRolePermission::getRoleId, id));
        if (permissionIds != null && !permissionIds.isEmpty()) {
            for (Long pid : permissionIds) {
                SysRolePermission rp = new SysRolePermission();
                rp.setRoleId(id);
                rp.setPermissionId(pid);
                rolePermissionMapper.insert(rp);
            }
        }
    }

    public List<SysPermission> findRolePermissions(Long roleId) {
        List<Long> permIds = rolePermissionMapper.selectList(
                new LambdaQueryWrapper<>(SysRolePermission.class)
                        .eq(SysRolePermission::getRoleId, roleId))
                .stream()
                .map(SysRolePermission::getPermissionId)
                .toList();
        if (permIds.isEmpty()) return List.of();
        return permissionMapper.selectBatchIds(permIds);
    }
}