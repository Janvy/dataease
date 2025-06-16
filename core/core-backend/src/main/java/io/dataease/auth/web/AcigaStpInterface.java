package io.dataease.auth.web;

import cn.dev33.satoken.stp.StpInterface;
import io.dataease.auth.xtoken.AuthUserContext;
import io.dataease.auth.xtoken.User;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.List;

@Component
public class AcigaStpInterface implements StpInterface {
    @Override
    public List<String> getPermissionList(Object loginId, String loginType) {
        User user = AuthUserContext.get();
        if (user != null) {
            return user.getPermissions();
        } else {
            return Collections.emptyList();
        }
    }

    @Override
    public List<String> getRoleList(Object loginId, String loginType) {
        User user = AuthUserContext.get();
        if (user != null) {
            return user.getRoles();
        } else {
            return Collections.emptyList();
        }
    }
}
