package com.freshdelivery.controller.sys;

import com.freshdelivery.common.Result;
import com.freshdelivery.entity.sys.SysPermission;
import com.freshdelivery.service.sys.SysPermissionService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/sys/permission")
@RequiredArgsConstructor
public class SysPermissionController {

    private final SysPermissionService permissionService;

    @GetMapping("/list")
    public Result<List<SysPermission>> list() {
        return Result.ok(permissionService.list());
    }
}