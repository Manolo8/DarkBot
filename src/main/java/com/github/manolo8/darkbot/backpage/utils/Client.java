package com.github.manolo8.darkbot.backpage.utils;

import com.github.manolo8.darkbot.backpage.auth.AuthenticationResult;
import okhttp3.*;

import java.io.IOException;
import java.util.function.Consumer;

public class Client {

    private final OkHttpClient         client;
    public       AuthenticationResult auth;

    public Client() {
        this.client = new OkHttpClient();
    }

    public void auth(AuthenticationResult result) {
        this.auth = result;
    }

    public void getString(String url, Consumer<String> consumer) {

        Request request = new Request.Builder()
                .url(auth.base + url)
                .addHeader("cache-control", "no-cache")
                .addHeader("Cookie", "dosid=" + auth.sid)
                .build();

        client.newCall(request).enqueue(new StringCallBack(consumer));
    }

    private static class StringCallBack implements Callback {

        private final Consumer<String> consumer;

        public StringCallBack(Consumer<String> consumer) {
            this.consumer = consumer;
        }

        @Override
        public void onFailure(Call call, IOException e) {

        }

        @Override
        public void onResponse(Call call, Response response)
                throws IOException {

            ResponseBody body = response.body();
            String       data = body.string();

            body.close();

            consumer.accept(data);
        }
    }
}
