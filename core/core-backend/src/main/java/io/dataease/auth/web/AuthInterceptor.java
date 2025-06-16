package io.dataease.auth.web;

import cn.hutool.core.util.StrUtil;
import io.dataease.auth.xtoken.*;
import io.dataease.exception.DEException;
import io.dataease.result.ResultCode;
import io.dataease.utils.WhitelistUtils;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.servlet.AsyncHandlerInterceptor;


/**
 * @author hadix
 * @date 2022/8/3
 */
@Slf4j
public class AuthInterceptor implements AsyncHandlerInterceptor {
    public static final String HEADER_TOKEN = "xtoken";

    private final UserSessionRepository session;
    private final ModuleAuthProperties config;

    public AuthInterceptor(UserSessionRepository session, ModuleAuthProperties config) {
        this.session = session;
        this.config = config;
    }

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) {
        String requestURI = request.getRequestURI();
        if (WhitelistUtils.match(requestURI)) {
            return true;
        }
        User user = config.getMock();
        if (user == null) {
            user = getUserFromLoginSession(request);
        }
        String projectId = request.getHeader("Projectid");
        if (StrUtil.isBlank(user.getProjectId()) && StrUtil.isNotBlank(projectId)) {
            user.setProjectId(projectId);
        }
        user.setIp(request.getRemoteAddr());
        user.setDevice(request.getHeader("user-agent"));
        AuthUserContext.set(user);
        TenantIdContext.set(user.getTenantId());
        return true;
    }

    private User getUserFromLoginSession(HttpServletRequest request) {
        String token = request.getHeader(HEADER_TOKEN);
        if (StrUtil.isBlank(token)) {
            token = request.getParameter(HEADER_TOKEN);
        }
        if (StrUtil.isBlank(token)) {
            token = request.getParameter("token");
        }
        if (StrUtil.isBlank(token)) {
            log.error("token is blank");
            DEException.throwException(ResultCode.USER_NOT_LOGGED_IN.code(), ResultCode.USER_NOT_LOGGED_IN.message());
        }
        AuthInfo authInfo = AuthInfo.of(token);
        if (authInfo.isExpired()) {
            DEException.throwException(ResultCode.USER_NOT_LOGGED_IN.code(), ResultCode.USER_NOT_LOGGED_IN.message());
//            throw new AuthException("登陆状态已过期，请重新登陆");
        }
        // 从token获取登陆账号
        final String account = authInfo.account();
        String accountType = authInfo.accountType();
        String tenantId = authInfo.tenantId();
        User user = session.getUser(account, accountType, tenantId);
        if (user == null) {
            log.error("get user by session is null");
            DEException.throwException(ResultCode.USER_NOT_LOGGED_IN.code(), ResultCode.USER_NOT_LOGGED_IN.message());
//            throw new AuthException("查询不到用户信息");
        }


        if (StrUtil.isBlank(user.getOpenId())) {
            user.setOpenId(authInfo.openId());
            log.info("{}|从token中解析openId赋值.",user.getAccount());
        }
        return user;
    }

    @Override
    public void afterConcurrentHandlingStarted(HttpServletRequest request, HttpServletResponse response, Object handler) {
        AuthUserContext.remove();
    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) {
        AuthUserContext.remove();
    }
}
