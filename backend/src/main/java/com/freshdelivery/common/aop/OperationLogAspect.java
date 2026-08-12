package com.freshdelivery.common.aop;

import com.freshdelivery.entity.sys.SysOperationLog;
import com.freshdelivery.service.sys.CurrentUser;
import com.freshdelivery.service.sys.SysOperationLogService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.After;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import java.time.LocalDateTime;

@Aspect
@Component
@RequiredArgsConstructor
public class OperationLogAspect {

    private final SysOperationLogService logService;

    @After("@annotation(opLog)")
    public void log(JoinPoint point, OperationLog opLog) {
        try {
            ServletRequestAttributes attrs =
                    (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
            if (attrs == null) return;

            HttpServletRequest request = attrs.getRequest();
            String username = CurrentUser.getUsername();

            SysOperationLog operationLog = new SysOperationLog();
            operationLog.setUserName(username);
            operationLog.setModule(opLog.module());
            operationLog.setAction(opLog.action());
            operationLog.setOperateTime(LocalDateTime.now());
            operationLog.setIpAddress(request.getRemoteAddr());
            logService.save(operationLog);
        } catch (Exception ignored) {
        }
    }
}