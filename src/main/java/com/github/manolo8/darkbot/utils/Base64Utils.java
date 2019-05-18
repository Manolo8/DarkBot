package com.github.manolo8.darkbot.utils;

import java.util.Base64;

public class Base64Utils{

    public static String base64Decode(String text) throws Exception {
        return new String(Base64.getDecoder().decode(text),"UTF-8");
    }
    public static String base64Encode(String text) throws Exception {
        return Base64.getEncoder().encodeToString(text.getBytes("UTF-8"));
    }
}
