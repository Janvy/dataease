package io.dataease.auth.xtoken;


import lombok.Getter;

/**
 * 账号类型
 */
@Getter
public enum AccountType {

    A("A"),
    B("B"),
    C("C"),

    ;

    private final String type;

    AccountType(String type) {
        this.type = type;
    }
}
