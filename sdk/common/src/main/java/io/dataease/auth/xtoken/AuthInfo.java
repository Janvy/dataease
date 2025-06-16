package io.dataease.auth.xtoken;

import cn.hutool.core.util.StrUtil;
import com.auth0.jwt.JWT;
import com.auth0.jwt.exceptions.JWTDecodeException;
import com.auth0.jwt.interfaces.DecodedJWT;

import java.util.Date;

/**
 * @author hadix
 * @date 2022/8/3
 */
public class AuthInfo {
    private final DecodedJWT jwt;

    private AuthInfo(DecodedJWT jwt) {
        this.jwt = jwt;
    }

    public static AuthInfo of(String jwtToken) {
        final DecodedJWT jwt;
        try {
            jwt = JWT.decode(jwtToken);
        } catch (JWTDecodeException e) {
            throw new AuthException("token不合法，无法解码", e);
        }
        return new AuthInfo(jwt);
    }

    public boolean isExpired() {
        final Date expiresAt = jwt.getExpiresAt();
        return expiresAt != null && expiresAt.before(new Date());
    }

    /**
     * 登陆账号
     */
    public String account() {
        return jwt.getClaim("loginId").asString();
    }

    /**
     * 所属租户id
     */
    public String tenantId() {
        return StrUtil.isBlank(jwt.getClaim("tenantId").asString()) ? jwt.getClaim("clientTenantId").asString()
            : jwt.getClaim("tenantId").asString();
    }

    /**
     * 账号类型
     */
    public String accountType() {
        return jwt.getClaim("accountType").asString();
    }

    /**
     * 从token获取openId
     **/
    public String openId() {
        return jwt.getClaim("openId").asString();
    }
}
