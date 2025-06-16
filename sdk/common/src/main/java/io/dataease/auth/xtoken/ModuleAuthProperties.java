package io.dataease.auth.xtoken;

import lombok.Data;
import org.springframework.boot.autoconfigure.data.redis.RedisProperties;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.NestedConfigurationProperty;

/**
 * @author hadix
 * @date 2022/8/3
 */
@Data
@ConfigurationProperties("module.auth")
public class ModuleAuthProperties {
    /**
     * 是否启用
     */
    private boolean enabled = true;

    /**
     * 是否启用@SaCheckXXX的注解权限检查
     */
    private boolean enableCheck = true;
    /**
     * 为测试目的模拟测试用户登录
     */
    @NestedConfigurationProperty
    private User mock;
    /**
     * 用户鉴权session的redis连接配置
     */
    @NestedConfigurationProperty
    private RedisProperties redis = new RedisProperties();

}
