package io.dataease.auth.xtoken;


/**
 * @author hadix
 * @date 2022/8/3
 */
public class AuthException extends RuntimeException {

    private static final long serialVersionUID = 937027784246589703L;

    public AuthException(String message) {
        super(message);
    }

    public AuthException(String message, Throwable cause) {
        super(message, cause);
    }

}
