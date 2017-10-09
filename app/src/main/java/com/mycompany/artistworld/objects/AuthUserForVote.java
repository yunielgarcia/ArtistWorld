package com.mycompany.artistworld.objects;

/**
 * Created by ygarcia on 10/9/2017.
 */

public class AuthUserForVote {
    private String email;
    private String password;
    private String username;
    private boolean terms;
    private boolean requiredAge;
    private String source;

    public AuthUserForVote(String email, String password, String username) {
        this.email = email;
        this.password = password;
        this.username = username;
        this.terms = true;
        this.requiredAge = true;
        this.source = "http://54.82.218.165/api/v1/source/13";
    }
}
