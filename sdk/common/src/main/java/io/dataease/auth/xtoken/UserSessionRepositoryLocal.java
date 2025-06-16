package io.dataease.auth.xtoken;

import org.springframework.util.CollectionUtils;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class UserSessionRepositoryLocal implements UserSessionRepository {
    private final Map<String, User> sessionRegistry = new ConcurrentHashMap<>();

    @Override
    public User getUser(String account, String accountType, String tenantId) {
        return sessionRegistry.get(account);
    }

    @Override
    public void setUser(User user, String account) {
        sessionRegistry.put(account, user);
    }

    @Override
    public void setExpandInfo(String account, Map<String, String> expandInfo) {
        User user = sessionRegistry.get(account);
        Map<String, String> oldExpand = user.getExpandInfo();
        if (!CollectionUtils.isEmpty(oldExpand)) {
            oldExpand.putAll(expandInfo);
        } else {
            user.setExpandInfo(expandInfo);
        }
        setUser(user, account);
    }


}
