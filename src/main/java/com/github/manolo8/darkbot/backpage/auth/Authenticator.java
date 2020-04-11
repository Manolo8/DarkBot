package com.github.manolo8.darkbot.backpage.auth;

import okhttp3.*;

import java.io.IOException;
import java.util.List;
import java.util.Map;

public class Authenticator {

    private final OkHttpClient client;
    private       String       username;
    private       String       password;
    private       String       sid;
    private       String       server;

    public Authenticator() {
        this.client = new OkHttpClient();
    }

    public void setCredentials(String username, String password) {
        this.username = username;
        this.password = password;
    }

    public void setSession(String sid, String server) {
        this.sid = sid;
        this.server = server;
    }

    public AuthenticationResult authenticate()
            throws AuthenticationException {

        try {

            if (username != null && password != null)
                sidAndServer();

            return authenticate0();

        } catch (Exception e) {
            username = null;
            password = null;
            sid = null;
            server = null;
            e.printStackTrace();
            throw new AuthenticationException(e.getMessage());
        }
    }

    @SuppressWarnings("ConstantConditions")
    private void sidAndServer()
            throws AuthenticationException, IOException {

        Request  request;
        Response response;
        String   document;

        request = new Request.Builder()
                .url("https://en.darkorbit.com")
                .get()
                .addHeader("cache-control", "no-cache")
                .build();

        response = client.newCall(request).execute();

        document = response.body().string();
        Map<String, List<String>> header    = response.headers().toMultimap();
        List<String>              setCookie = header.get("Set-Cookie");

        String urlAuthentication = urlAuthentication(document);
        sid = extractSid(setCookie);

        response.body().close();

        if (sid == null)
            throw new AuthenticationException("SID invalid...");

        MediaType   mediaType = MediaType.parse("application/x-www-form-urlencoded");
        RequestBody body      = RequestBody.create(mediaType, "username=" + username + "&password=" + password);
        request = new Request.Builder()
                .url(urlAuthentication)
                .post(body)
                .addHeader("Content-Type", "application/x-www-form-urlencoded")
                .addHeader("cache-control", "no-cache")
                .addHeader("Cookie", "dosid=" + sid)
                .build();

        response = client.newCall(request).execute();

        server = extractServer(response.request().url().toString());

        response.body().close();

        if (server == null || server.equals("en"))
            throw new AuthenticationException("Name or password wrong");
    }

    @SuppressWarnings("ConstantConditions")
    private AuthenticationResult authenticate0()
            throws IOException, AuthenticationException {

        Request  request;
        Response response;
        String   document;

        request = new Request.Builder()
                .url("https://" + server + ".darkorbit.com/indexInternal.es?action=internalMapRevolution")
                .get()
                .addHeader("Cookie", "dosid=" + sid)
                .build();

        response = client.newCall(request).execute();

        document = response.body().string();

        response.body().close();

        int start = document.indexOf("\"onFail\":");
        int end   = document.lastIndexOf("\"allowChat\"") + 16;

        if (start == -1 || end == -1)
            throw new AuthenticationException("SessionID or server valid!");

        document = document.substring(start, end);

        String preloader = document.substring(document.indexOf("src") + 7, document.indexOf("version") - 3);
        String vars      = document.substring(document.indexOf("lang") - 1).replaceAll("\"", "").replaceAll(",", "&").replaceAll(": ", "=");
        int    id        = Integer.parseInt(vars.substring(vars.indexOf("ID=") + 3, vars.indexOf("&session")));

        return new AuthenticationResult(
                server,
                id,
                sid,
                vars,
                preloader,
                "https://" + server + ".darkorbit.com/"
        );
    }

    private String extractServer(String url) {
        int s = url.indexOf('/');
        int e = url.indexOf('.');

        if (s == -1 || e == -1)
            return null;

        return url.substring(s + 2, e);
    }

    private String extractSid(List<String> cookie) {
        for (String string : cookie) {
            if (string.startsWith("dosid")) {
                int index = string.indexOf(';');
                return string.substring(6, index);
            }
        }

        return null;
    }

    private String urlAuthentication(String document)
            throws AuthenticationException {

        int start = document.indexOf("https://sas.bpsecure.com/Sas/Authentication");
        int end   = start;

        if (start == -1)
            throw new AuthenticationException("Action URL not found");

        while (document.charAt(end) != '"') end++;

        return document.substring(start, end).replaceAll("&amp;", "&");
    }
}
