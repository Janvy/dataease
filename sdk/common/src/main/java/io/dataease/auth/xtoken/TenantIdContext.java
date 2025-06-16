package io.dataease.auth.xtoken;

/**
 * @author 31788
 * @date 2025/6/13 17:41
 * @description: 租户上下文
 */
public class TenantIdContext {
    private static final ThreadLocal<String> TENANT_ID = new ThreadLocal<>();

    public static void set(String tenantId) {
        TENANT_ID.set(tenantId);
    }

    public static void remove() {
        TENANT_ID.remove();
    }

    public static String get() {
        return TENANT_ID.get();
    }

    public static ThreadLocal<String> threadLocal() {
        return TENANT_ID;
    }

    /**
     * 使用指定的TenantId作为上下文执行{@code task}。
     *
     * @param tenantId 指定的traceId
     * @param task     无返回值的闭包。
     */
    public static void scope(String tenantId, final Runnable task) {
        // 捕获当前线程的TraceId。
        final String storedTenantId = get();
        // 设置执行线程的TenantId。
        set(tenantId);
        try {
            task.run();
        } finally {
            // 恢复执行线程的TenantId。
            set(storedTenantId);
        }
    }
}
