package io.dataease.auth.xtoken;

import lombok.Data;

import java.io.Serializable;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * @author hadix
 * @date 2022/8/3
 */
@Data
public class User implements Serializable {
    private static final long serialVersionUID = -6468802011683902342L;
    public static final User NULL = new User();

    private Long id;
    private String userBid;
    private String tenantId;
    private String name;
    private String account;
    private String salt;
    private String password;
    private String ip;
    private String device;
    private String orgCode;
    private String mobile;
    private List<String> permissions;
    private List<String> roles;
    /**
     * 第三方平台id
     */
    private String openId;

    /**
     * 所属的业务组织主键id
     */
    private Long currentOrgId;

    /**
     * 所拥有的所有组织列表
     */
    private Set<Long> allOrgId;

    /**
     * 所拥有的所有项目列表
     */
    private Set<String> allProjectIds;

    /**
     * 当前项目id
     */
    private String projectId;

    /**
     * 账号类型
     */
    private String accountType;

    /**
     * 所拥有的所有项目列表
     */
    private List<String> projectIds;

    /**
     * 账号bid
     */
    private String accountBid;

    /**
     * 拓展信息
     */
    private Map<String, String> expandInfo;

}
