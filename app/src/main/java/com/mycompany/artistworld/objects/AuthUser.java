package com.mycompany.artistworld.objects;

/**
 * Created by ygarcia on 10/4/2017.
 */

public class AuthUser {
    private String email;
    private String username;
    private String password;

    public AuthUser(String username, String password) {
        this.email = "";
        this.username = username;
        this.password = password;
    }
}
