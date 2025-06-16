package io.dataease.auth;

import cn.dev33.satoken.dao.SaTokenDaoRedisJackson;
import cn.hutool.core.util.StrUtil;
import io.dataease.auth.xtoken.ModuleAuthProperties;
import io.dataease.auth.xtoken.UserSessionRepository;
import io.dataease.auth.xtoken.UserSessionRepositoryLocal;
import io.dataease.auth.xtoken.UserSessionRepositoryRedis;
import io.lettuce.core.ClientOptions;
import io.lettuce.core.SocketOptions;
import io.lettuce.core.TimeoutOptions;
import io.lettuce.core.cluster.ClusterClientOptions;
import io.lettuce.core.cluster.ClusterTopologyRefreshOptions;
import io.lettuce.core.resource.ClientResources;
import org.apache.commons.pool2.impl.GenericObjectPoolConfig;
import org.springframework.beans.factory.FactoryBean;
import org.springframework.beans.factory.NoSuchBeanDefinitionException;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.autoconfigure.data.redis.RedisProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.connection.RedisPassword;
import org.springframework.data.redis.connection.RedisStandaloneConfiguration;
import org.springframework.data.redis.connection.lettuce.LettuceClientConfiguration;
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory;
import org.springframework.data.redis.connection.lettuce.LettucePoolingClientConfiguration;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.serializer.RedisSerializer;

import java.time.Duration;

/**
 * @author hadix
 * @date 2022/8/3
 */
@Configuration
@EnableConfigurationProperties(ModuleAuthProperties.class)
@ComponentScan
@ConditionalOnProperty(name = "module.auth.enabled", havingValue = "true", matchIfMissing = true)
public class ModuleAuthAutoConfiguration {

    @Configuration(proxyBeanMethods = false)
    @ConditionalOnClass({SaTokenDaoRedisJackson.class, RedisConnectionFactory.class})
    public static class SaTokenConfiguration {
        @Bean
        public FactoryBean<SaTokenDaoRedisJackson> saTokenDaoRedisJackson(UserSessionRepositoryRedis redis) {
            return new FactoryBean<SaTokenDaoRedisJackson>() {
                @Override
                public SaTokenDaoRedisJackson getObject() {
                    SaTokenDaoRedisJackson saTokenDaoRedisJackson = new SaTokenDaoRedisJackson();
                    saTokenDaoRedisJackson.init(redis.getRedisTemplate().getConnectionFactory());
                    StringRedisTemplate template = saTokenDaoRedisJackson.stringRedisTemplate;
                    template.setKeySerializer(RedisSerializer.string());
                    template.setValueSerializer(RedisSerializer.string());
                    template.setHashKeySerializer(RedisSerializer.java());
                    template.setHashValueSerializer(RedisSerializer.java());
                    return saTokenDaoRedisJackson;
                }

                @Override
                public Class<?> getObjectType() {
                    return SaTokenConfiguration.class;
                }
            };
        }
    }

    @Configuration(proxyBeanMethods = false)
    @ConditionalOnClass(RedisConnectionFactory.class)
    public static class UserSessionRepositoryConfiguration {

        @Bean(destroyMethod = "shutdown")
        public UserSessionRepositoryRedis userSessionRepositoryRedis(
            ModuleAuthProperties config, ObjectProvider<RedisConnectionFactory> alternativeRedisConnectionFactory) {
            RedisConnectionFactory connectionFactory;
            final RedisProperties redisConfig = config.getRedis();
            if (StrUtil.isNotBlank(redisConfig.getHost())) {
                LettuceClientConfiguration.LettuceClientConfigurationBuilder builder;
                final RedisProperties.Pool pool = redisConfig.getLettuce().getPool();
                if (pool != null) {
                    builder = LettucePoolingClientConfiguration.builder().poolConfig(getPoolConfig(pool));
                } else {
                    builder = LettuceClientConfiguration.builder();
                }
                final LettuceClientConfiguration configuration = builder.clientOptions(createClientOptions(redisConfig))
                    .clientName(redisConfig.getClientName())
                    .commandTimeout(redisConfig.getTimeout())
                    .clientResources(ClientResources.builder().build())
                    .build();

                final LettuceConnectionFactory lettuceConnectionFactory = new LettuceConnectionFactory(getStandaloneConfig(redisConfig), configuration);
                lettuceConnectionFactory.afterPropertiesSet();
                connectionFactory = lettuceConnectionFactory;
            } else {
                connectionFactory = alternativeRedisConnectionFactory.getIfAvailable();
                if (connectionFactory == null) {
                    throw new NoSuchBeanDefinitionException(RedisTemplate.class, "请使用 'module.auth.redis.*' 提供用户鉴权Session使用的redis实例连接配置,或使用 'spring.redis.*' 提供共享的redis实例配置");
                }
            }
//            RedisTemplate<String, Object> redisTemplate = new RedisTemplate<>();
//            redisTemplate.setConnectionFactory(connectionFactory);
//            redisTemplate.afterPropertiesSet();

            StringRedisTemplate redisTemplate = new StringRedisTemplate();
            redisTemplate.setConnectionFactory(connectionFactory);
            redisTemplate.afterPropertiesSet();
            return new UserSessionRepositoryRedis(redisTemplate);
        }

        private ClientOptions createClientOptions(RedisProperties config) {
            ClientOptions.Builder builder = initializeClientOptionsBuilder(config);
            Duration connectTimeout = config.getConnectTimeout();
            if (connectTimeout != null) {
                builder.socketOptions(SocketOptions.builder().connectTimeout(connectTimeout).build());
            }
            return builder.timeoutOptions(TimeoutOptions.enabled()).build();
        }

        private ClientOptions.Builder initializeClientOptionsBuilder(RedisProperties config) {
            if (config.getCluster() != null) {
                ClusterClientOptions.Builder builder = ClusterClientOptions.builder();
                RedisProperties.Lettuce.Cluster.Refresh refreshProperties = config.getLettuce().getCluster().getRefresh();
                ClusterTopologyRefreshOptions.Builder refreshBuilder = ClusterTopologyRefreshOptions.builder()
                    .dynamicRefreshSources(refreshProperties.isDynamicRefreshSources());
                if (refreshProperties.getPeriod() != null) {
                    refreshBuilder.enablePeriodicRefresh(refreshProperties.getPeriod());
                }
                if (refreshProperties.isAdaptive()) {
                    refreshBuilder.enableAllAdaptiveRefreshTriggers();
                }
                return builder.topologyRefreshOptions(refreshBuilder.build());
            }
            return ClientOptions.builder();
        }

        private GenericObjectPoolConfig<?> getPoolConfig(RedisProperties.Pool properties) {
            GenericObjectPoolConfig<?> config = new GenericObjectPoolConfig<>();
            config.setMaxTotal(properties.getMaxActive());
            config.setMaxIdle(properties.getMaxIdle());
            config.setMinIdle(properties.getMinIdle());
            if (properties.getTimeBetweenEvictionRuns() != null) {
                config.setTimeBetweenEvictionRuns(properties.getTimeBetweenEvictionRuns());
            }
            if (properties.getMaxWait() != null) {
                config.setMaxWait(properties.getMaxWait());
            }
            return config;
        }

        protected final RedisStandaloneConfiguration getStandaloneConfig(RedisProperties properties) {
            RedisStandaloneConfiguration config = new RedisStandaloneConfiguration();
            config.setHostName(properties.getHost());
            config.setPort(properties.getPort());
            config.setUsername(properties.getUsername());
            config.setPassword(RedisPassword.of(properties.getPassword()));
            config.setDatabase(properties.getDatabase());
            return config;
        }

    }

    @Bean
    @ConditionalOnMissingBean
    public UserSessionRepository userSessionRepositoryLocal() {
        return new UserSessionRepositoryLocal();
    }
}
