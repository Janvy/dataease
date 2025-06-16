package io.dataease.auth.xtoken;


import java.util.Map;

/**
 * @author hadix
 * @date 2022/8/3
 */
public interface UserSessionRepository {
    User getUser(String account, String accountType, String tenantId);

    default User getUser(String account) {
        return getUser(account, null, null);
    }

    void setUser(User user, String account);

    void setExpandInfo(String account, Map<String, String> expandInfo);

    default String getCustomerProjectChangeTag(String customerBid) {
        return "";
    }

    default void remove(String key) {

    }

    default void removeProjectChangeTag(String customerBid) {

    }

    default void removeCustomerCache(String account) {

    }
}
