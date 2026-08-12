package com.freshdelivery.controller.sys;

import com.freshdelivery.common.Result;
import com.freshdelivery.common.PageResult;
import com.freshdelivery.common.aop.OperationLog;
import com.freshdelivery.entity.sys.SysRole;
import com.freshdelivery.entity.sys.SysUser;
import com.freshdelivery.service.sys.SysUserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/sys/user")
@RequiredArgsConstructor
public class SysUserController {

    private final SysUserService userService;

    @GetMapping("/page")
    public Result<PageResult<SysUser>> page(@RequestParam(defaultValue = "1") int pageNum,
                                            @RequestParam(defaultValue = "10") int pageSize,
                                            @RequestParam(required = false) String keyword) {
        return Result.ok(userService.page(pageNum, pageSize, keyword));
    }

    @GetMapping("/{id}")
    public Result<SysUser> detail(@PathVariable Long id) {
        return Result.ok(userService.detail(id));
    }

    @GetMapping("/{id}/roles")
    public Result<List<SysRole>> roles(@PathVariable Long id) {
        return Result.ok(userService.findUserRoles(id));
    }

    @PostMapping
    @OperationLog(module = "user", action = "add")
    public Result<Void> create(@Valid @RequestBody UserForm form) {
        SysUser user = new SysUser();
        user.setUsername(form.username());
        user.setPassword(form.password());
        user.setRealName(form.realName());
        user.setRoleId(form.roleId());
        userService.create(user, form.roleIds());
        return Result.ok();
    }

    @PutMapping("/{id}")
    @OperationLog(module = "user", action = "edit")
    public Result<Void> update(@PathVariable Long id, @Valid @RequestBody UserForm form) {
        SysUser user = new SysUser();
        user.setUsername(form.username());
        user.setPassword(form.password());
        user.setRealName(form.realName());
        user.setRoleId(form.roleId());
        userService.update(id, user, form.roleIds());
        return Result.ok();
    }

    @DeleteMapping("/{id}")
    @OperationLog(module = "user", action = "delete")
    public Result<Void> delete(@PathVariable Long id) {
        userService.delete(id);
        return Result.ok();
    }

    public record UserForm(
            String username, String password, String realName,
            Long roleId, List<Long> roleIds) {}
}