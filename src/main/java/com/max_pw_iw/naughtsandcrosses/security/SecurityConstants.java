package com.max_pw_iw.naughtsandcrosses.security;

public class SecurityConstants {
    public static final String SECRET_KEY = "4zQIxmn8ZRoYujrU1t3Bx6nMUzwQq3L9cPIsElEAgfcvQ7OuouhrFKqKqqohHUr5FNrq6CcFnYsEGeX3eYCOCmVEYuHnpcQywm8Uj0L0n0eqDPcwjOxiJflPv6xIU5Dv"; //Your secret should always be strong (uppercase, lowercase, numbers, symbols) so that nobody can potentially decode the signature.
    public static final int TOKEN_EXPIRATION = 43200000; // 43200000 milliseconds = 43200 seconds = 12 hours.
    public static final String BEARER = "Bearer "; // Authorization : "Bearer " + Token 
    public static final String AUTHORIZATION = "Authorization"; // "Authorization" : Bearer Token
    public static final String[] REGISTER_AUTHENTICATE_PATH = {"/user/register" , "/authenticate"}; // Public path that clients can use to register.
}
