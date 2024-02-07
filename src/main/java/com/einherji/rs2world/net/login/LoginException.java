package com.einherji.rs2world.net.login;

public class LoginException extends RuntimeException {

    private final byte responseCode;

    public LoginException(byte responseCode) {
        super("Encountered login exception with code: " + responseCode);
        this.responseCode = responseCode;
    }

    public byte getResponseCode() {
        return responseCode;
    }
}
