package io.dataease.auth.xtoken;


/**
 * @author hadix
 * @date 2022/8/3
 */
public class AuthUserContext {
    private static final ThreadLocal<User> USER_HOLDER = new InheritableThreadLocal<>();

    public static ThreadLocal<User> threadLocal() {
        return USER_HOLDER;
    }

    public static User get() {
        return USER_HOLDER.get();
    }

    public static User getOrNull() {
        final User user = USER_HOLDER.get();
        if (user == null) {
            return User.NULL;
        }
        return user;
    }

    public static void set(User user) {
        USER_HOLDER.set(user);
    }

    public static void remove() {
        USER_HOLDER.remove();
    }

    public static Long getId() {
        return getOrNull().getId();
    }

    public static String getUserBid() {
        return getOrNull().getUserBid();
    }

    public static String getTenantId() {
        return getOrNull().getTenantId();
    }

    public static String getName() {
        return getOrNull().getName();
    }

    public static String getAccount() {
        return getOrNull().getAccount();
    }

    public static String getSalt() {
        return getOrNull().getSalt();
    }

    public static String getPassword() {
        return getOrNull().getPassword();
    }

    public static String getIp() {
        return getOrNull().getIp();
    }

    public static String getDevice() {
        return getOrNull().getDevice();
    }

    public static String getOrgCode() {
        return getOrNull().getOrgCode();
    }

    public static String getMobile() {
        return getOrNull().getMobile();
    }
}
