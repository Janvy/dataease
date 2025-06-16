package io.dataease.auth.xtoken;

import cn.dev33.satoken.stp.StpUtil;
import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.lang.TypeReference;
import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSONUtil;
import io.dataease.auth.xtoken.model.CifUserInfo;
import io.dataease.auth.xtoken.model.SUserInfo;
import io.dataease.auth.xtoken.util.SaTokenUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.util.CollectionUtils;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Map;

@Slf4j
public class UserSessionRepositoryRedis implements UserSessionRepository {
    /**
     * 用户信息缓存
     */
    public static final String SASS_USER_INFO_CACHE_KEY = "SASS_USER_INFO:";
    public static final String SASS_CUSTOMER_INFO_CACHE_KEY = "SASS_CUSTOMER_INFO:";
    public static final String SASS_USER_EXPAND_INFO_CACHE_KEY = "SASS_USER_EXPAND_INFO:";

    private final StringRedisTemplate redisTemplate;

    public UserSessionRepositoryRedis(StringRedisTemplate redisTemplate) {
        this.redisTemplate = redisTemplate;
    }

    @Override
    public User getUser(String account, String accountType, String tenantId) {
        User user = SaTokenUtil.getSessionUser();
        if (AccountType.C.getType().equalsIgnoreCase(user.getAccountType())
            || AccountType.C.getType().equals(accountType)) {
            String cifUserJson = redisTemplate.opsForValue().get(SASS_CUSTOMER_INFO_CACHE_KEY + account);
            CifUserInfo info = JSONUtil.toBean(cifUserJson, CifUserInfo.class);
            if (info == null) {
                log.error("token={},account={}从缓存中获取C端账号为空", StpUtil.getTokenValue(), account);
                return null;
            }
            user.setId(info.getId());
            user.setAccount(account);
            user.setUserBid(info.getBid());
            user.setTenantId(tenantId);
            user.setName(info.getName());
            user.setIp(info.getIp());
            user.setDevice(info.getDevice());
            user.setMobile(info.getMobile());
            String userExpandInfo = redisTemplate.opsForValue().get(SASS_USER_EXPAND_INFO_CACHE_KEY + account);
            if (StrUtil.isNotBlank(userExpandInfo)) {
                Map<String, String> expandInfo = JSONUtil.toBean(userExpandInfo, new TypeReference<Map<String, String>>() {}, true);
                user.setExpandInfo(expandInfo);
            }
            return user;
        }
        String userInfoJson = redisTemplate.opsForValue().get(SASS_USER_INFO_CACHE_KEY + account);
        SUserInfo info = JSONUtil.toBean(userInfoJson, SUserInfo.class);
        if (info == null) {
            log.error("token={}, account={}从缓存中获取B端账号为空", StpUtil.getTokenValue(), account);
            return null;
        }
        user.setId(info.getId());
        user.setAccount(info.getAccount());
        user.setUserBid(info.getUserBid());
        user.setTenantId(info.getTenantId());
        user.setName(info.getName());
        user.setSalt(info.getSalt());
        user.setPassword(info.getPassword());
        user.setIp(info.getIp());
        user.setDevice(info.getDevice());
        user.setOrgCode(info.getOrgCode());
        user.setMobile(info.getMobile());
        user.setPermissions(new ArrayList<>(CollUtil.emptyIfNull(info.getStringPermissions())));
        String userExpandInfo = redisTemplate.opsForValue().get(SASS_USER_EXPAND_INFO_CACHE_KEY + account);
        if (StrUtil.isNotBlank(userExpandInfo)) {
            Map<String, String> expandInfo = JSONUtil.toBean(userExpandInfo, new TypeReference<Map<String, String>>() {}, true);
            user.setExpandInfo(expandInfo);
        }
        log.info("xtoken={}, user={}", StpUtil.getTokenValue(), JSONUtil.toJsonStr(user));
        return user;


    }


    @Override
    public void setUser(User user, String account) {
        SUserInfo sUserInfo = new SUserInfo();
        BeanUtil.copyProperties(user, sUserInfo);
        sUserInfo.setRoles(new HashSet<>(CollUtil.emptyIfNull(user.getRoles())));
        sUserInfo.setStringPermissions(new HashSet<>(CollUtil.emptyIfNull(user.getPermissions())));
        redisTemplate.opsForValue().set(SASS_USER_INFO_CACHE_KEY + account, JSONUtil.toJsonStr(sUserInfo));
    }

    @Override
    public void setExpandInfo(String account, Map<String, String> expandInfo) {
        User user = SaTokenUtil.getSessionUser();
        User redisUser = getUser(user.getAccount(), user.getAccountType(), user.getTenantId());
        Map<String, String> oldExpandMap = redisUser.getExpandInfo();
        if (!CollectionUtils.isEmpty(oldExpandMap)) {
            redisUser.getExpandInfo().putAll(expandInfo);
        } else {
            redisUser.setExpandInfo(expandInfo);
        }
        redisTemplate.opsForValue().set(SASS_USER_EXPAND_INFO_CACHE_KEY + redisUser.getAccount(),
            JSONUtil.toJsonStr(redisUser.getExpandInfo()));
        setUser(redisUser, user.getAccount());
    }

    @Override
    public String getCustomerProjectChangeTag(String customerBid) {
        return redisTemplate.opsForValue().get(String.format(RedisKeyConstant.CUSTOMER_PROJECT_CHANGE_TAG, customerBid));
    }

    @Override
    public void remove(String key) {
        redisTemplate.delete(key);
    }

    @Override
    public void removeProjectChangeTag(String customerBid) {
        redisTemplate.delete(String.format(RedisKeyConstant.CUSTOMER_PROJECT_CHANGE_TAG, customerBid));
    }

    public void removeCustomerCache(String account) {
        redisTemplate.delete(SASS_CUSTOMER_INFO_CACHE_KEY + account);
    }

    public void shutdown() {
        RedisConnectionFactory connectionFactory = redisTemplate.getConnectionFactory();
        if (connectionFactory instanceof DisposableBean) {
            try {
                ((DisposableBean) connectionFactory).destroy();
            } catch (Exception e) {
                log.warn("shutdown error : " + e.getMessage(), e);
            }
        }
    }

    public StringRedisTemplate getRedisTemplate() {
        return redisTemplate;
    }
}
