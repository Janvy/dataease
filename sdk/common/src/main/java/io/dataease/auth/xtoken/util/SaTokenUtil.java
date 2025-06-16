package io.dataease.auth.xtoken.util;

import cn.dev33.satoken.session.SaSession;
import cn.dev33.satoken.stp.StpUtil;
import cn.hutool.json.JSONUtil;
import io.dataease.auth.xtoken.AccountType;
import io.dataease.auth.xtoken.AuthException;
import io.dataease.auth.xtoken.User;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class SaTokenUtil {

//    public static User getSessionUser() {
//        SaSession session;
//        try {
//            session = StpUtil.getSession(false);
//        } catch (NotLoginException e) {
//            log.error("获取session报未登录错误，获取不到session信息");
//            throw new AuthException("登录失效，请重新登录");
//        } catch (Exception e) {
//            return new User();
//        }
//        return getSessionUser(session);
//    }

    public static User getSessionUser() {
        try {
            SaSession session = StpUtil.getSession(false);
            return getSessionUser(session);
        } catch (Exception e) {
            log.info("error", e);
        }
        User user = new User();
        user.setAccountType(AccountType.C.getType());
        return user;


//        SaSession session;
//        try {
//            session = StpUtil.getSession(false);
//        } catch (NotLoginException e) {
//            log.error("获取session报未登录错误，获取不到session信息");
//            throw new AuthException("登录失效，请重新登录");
//        } catch (Exception e) {
//            return new User();
//        }
//        return getSessionUser(session);
    }

    public static User getSessionUser(SaSession saSession) {
        if (saSession == null) {
            log.error("查询不到用户session信息");
            throw new AuthException("登录失效，请重新登录");
        }
        Object userObj = saSession.get("user");
        if (userObj == null) {
            return new User();
        }
        User user = JSONUtil.toBean((String) userObj, User.class);
        if (user == null) {
            user = new User();
        }
        return user;
    }

    public static void setUser(SaSession saSession, User user) {
        saSession.set("user", JSONUtil.toJsonStr(user));
    }
}
