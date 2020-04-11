package com.github.manolo8.darkbot.backpage.auth;

public class AuthenticationResult {

    public final int    id;
    public final String server;
    public final String sid;
    public final String vars;
    public final String preloader;
    public final String base;

    public AuthenticationResult(String server, int id, String sid, String vars, String preloader, String base) {
        this.server = server;
        this.id = id;
        this.sid = sid;
        this.vars = vars;
        this.preloader = preloader;
        this.base = base;
    }
}
