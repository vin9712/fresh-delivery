package com.freshdelivery.service.sys;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.freshdelivery.common.PageResult;
import com.freshdelivery.common.exception.BusinessException;
import com.freshdelivery.entity.sys.SysApproval;
import com.freshdelivery.entity.sys.SysPermission;
import com.freshdelivery.entity.sys.SysRole;
import com.freshdelivery.entity.sys.SysRolePermission;
import com.freshdelivery.entity.sys.SysUserRole;
import com.freshdelivery.mapper.sys.SysApprovalMapper;
import com.freshdelivery.mapper.sys.SysPermissionMapper;
import com.freshdelivery.mapper.sys.SysRoleMapper;
import com.freshdelivery.mapper.sys.SysRolePermissionMapper;
import com.freshdelivery.mapper.sys.SysUserRoleMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class SysApprovalService {

    @Autowired private SysApprovalMapper approvalMapper;
    @Autowired private SysUserRoleMapper userRoleMapper;
    @Autowired private SysRoleMapper roleMapper;
    @Autowired private SysRolePermissionMapper rolePermissionMapper;
    @Autowired private SysPermissionMapper permissionMapper;

    public PageResult<SysApproval> page(int pageNum, int pageSize, String bizType) {
        LambdaQueryWrapper<SysApproval> wrapper = new LambdaQueryWrapper<>();
        if (bizType != null && !bizType.isBlank()) {
            wrapper.eq(SysApproval::getBizType, bizType);
        }
        wrapper.orderByDesc(SysApproval::getSubmitTime);

        Page<SysApproval> p = approvalMapper.selectPage(
                new Page<>(pageNum, pageSize), wrapper);
        PageResult<SysApproval> result = new PageResult<>();
        result.setRecords(p.getRecords());
        result.setTotal(p.getTotal());
        result.setPageNum(pageNum);
        result.setPageSize(pageSize);
        return result;
    }

    public PageResult<SysApproval> getPending(int pageNum, int pageSize) {
        LambdaQueryWrapper<SysApproval> wrapper = new LambdaQueryWrapper<SysApproval>()
                .eq(SysApproval::getStatus, 0)
                .orderByDesc(SysApproval::getSubmitTime);

        Page<SysApproval> p = approvalMapper.selectPage(
                new Page<>(pageNum, pageSize), wrapper);
        PageResult<SysApproval> result = new PageResult<>();
        result.setRecords(p.getRecords());
        result.setTotal(p.getTotal());
        result.setPageNum(pageNum);
        result.setPageSize(pageSize);
        return result;
    }

    public void submit(SysApproval approval) {
        approval.setStatus(0);
        approval.setSubmitTime(LocalDateTime.now());
        approvalMapper.insert(approval);
    }

    public void handle(Long id, Integer status, String remark) {
        SysApproval approval = approvalMapper.selectById(id);
        if (approval == null) throw new BusinessException("审批单不存在");
        if (approval.getStatus() != 0) throw new BusinessException("审批已完成");

        approval.setStatus(status);
        approval.setRemark(remark);
        approval.setApproveTime(LocalDateTime.now());
        approvalMapper.updateById(approval);
    }

    /** 获取用户所有角色ID列表 */
    public List<SysRole> getRolesByUserId(Long userId) {
        List<Long> roleIds = userRoleMapper.selectList(
                new LambdaQueryWrapper<>(SysUserRole.class)
                        .eq(SysUserRole::getUserId, userId))
                .stream()
                .map(SysUserRole::getRoleId)
                .toList();
        if (roleIds.isEmpty()) return List.of();
        return roleMapper.selectBatchIds(roleIds);
    }

    /** 获取角色所有权限ID列表 */
    public List<SysPermission> getPermissionsByRoleId(Long roleId) {
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