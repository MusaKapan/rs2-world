package com.einherji.rs2world.net.login;

public class LoginResponseCodes {

    public static final byte LOGIN_OK = 2;
    public static final byte INVALID_CREDENTIALS = 3;
    public static final byte ACCOUNT_DISABLED = 4;
    public static final byte ALREADY_LOGGED_IN = 5;
    public static final byte WORLD_UPDATED = 6;
    public static final byte WORLD_FULL = 7;
    public static final byte LOGIN_SERVER_OFFLINE = 8;
    public static final byte LOGIN_LIMIT_EXCEEDED = 9;
    public static final byte BAD_SESSION_ID = 10;
    public static final byte PLEASE_TRY_AGAIN = 11;
    public static final byte NEED_MEMBERS = 12;
    public static final byte COULD_NOT_COMPLETE_LOGIN = 13;
    public static final byte SERVER_BEING_UPDATED = 14;
    public static final byte LOGIN_ATTEMPTS_EXCEEDED = 16;
    public static final byte MEMBERS_ONLY_AREA = 17;
}
