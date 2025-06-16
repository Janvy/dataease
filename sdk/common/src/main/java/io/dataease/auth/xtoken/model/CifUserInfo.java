package io.dataease.auth.xtoken.model;


import lombok.Data;

import java.io.Serializable;

/**
 * @author clint.zhou
 * @date 2022/3/14 11:12 AM
 */

@Data
public class CifUserInfo implements Serializable {


    private static final long serialVersionUID = -7076287257749731131L;
    private Long id;

    private String bid;

    // 与BID等价
    private Long bidLong;

    private String name;

    private String account;

    private String ip;

    private String device;

    private String mobile;

    public Long getId() {
        return id;
    }

    public CifUserInfo setId(Long id) {
        this.id = id;
        return this;
    }

    public String getBid() {
        return bid;
    }

    public CifUserInfo setBid(String bid) {
        this.bid = bid;
        return this;
    }

    public Long getBidLong() {
        return bidLong;
    }

    public void setBidLong(Long bidLong) {
        this.bidLong = bidLong;
    }

    public String getName() {
        return name;
    }

    public CifUserInfo setName(String name) {
        this.name = name;
        return this;
    }

    public String getAccount() {
        return account;
    }

    public CifUserInfo setAccount(String account) {
        this.account = account;
        return this;
    }


    public String getIp() {
        return ip;
    }

    public CifUserInfo setIp(String ip) {
        this.ip = ip;
        return this;
    }

    public String getDevice() {
        return device;
    }

    public CifUserInfo setDevice(String device) {
        this.device = device;
        return this;
    }

    public String getMobile() {
        return mobile;
    }

    public CifUserInfo setMobile(String mobile) {
        this.mobile = mobile;
        return this;
    }

    @Override
    public String toString() {
        return "CifUserInfo{" +
            "id=" + id +
            ", bid='" + bid + '\'' +
            ", name='" + name + '\'' +
            ", account='" + account + '\'' +
            ", ip='" + ip + '\'' +
            ", device='" + device + '\'' +
            ", mobile='" + mobile + '\'' +
            '}';
    }
}
