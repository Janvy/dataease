package io.dataease.auth.xtoken.model;

import lombok.Data;

import java.io.Serializable;
import java.util.Set;

/**
 * @author clint.zhou
 * @date 2022/3/14 11:12 AM
 */
@Data
public class SUserInfo implements Serializable {

    private static final long serialVersionUID = -7240052746697069412L;

    private Long id;

    private String userBid;
    /**
     * 租户编码
     */
    private String tenantId;

    private String name;

    private String account;

    private String salt;

    private String password;

    private String ip;

    private String device;

    private String orgCode;

    private String mobile;

    protected Set<String> roles;

    protected Set<String> stringPermissions;

    /**
     * 当前项目id
     */
    private String projectId;

    /**
     * 账号bid
     */
    private String accountBid;

}
