package com.github.manolo8.darkbot.backpage.hangar;

import com.github.manolo8.darkbot.utils.Base64Utils;
import com.github.manolo8.darkbot.utils.IOUtils;
import com.github.manolo8.darkbot.utils.http.Http;
import com.github.manolo8.darkbot.utils.http.Method;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.io.*;
import java.util.Base64;
import java.util.Optional;

public class HangarResponse {
    private int isError;
    private Data data;

    //Test
    public static void main(String[] args) {
        Gson gson = new Gson();
        String dosid = "2137";

        InputStreamReader reader = Http.create("https://pl2.darkorbit.com/flashAPI/inventory.php", Method.POST)
                .setRawHeader("Cookie", "dosid=" + dosid)
                .setRawParam("action", "getHangarList")
                .setRawParam("params", Base64Utils.encode("{}"))
                .consumeInputStream(HangarResponse::createReader);

        HangarResponse hangarList = gson.fromJson(reader, HangarResponse.class);
        System.out.println(hangarList);

        reader = hangarList.data.getRet().getHangars().stream()
                .filter(Hangar::isHangarActive)
                .findFirst()
                .map(hangar -> Http.create("https://pl2.darkorbit.com/flashAPI/inventory.php", Method.POST)
                        .setRawHeader("Cookie", "dosid=" + dosid)
                        .setRawParam("action", "getHangar")
                        .setRawParam("params", Base64Utils.encode("{\"params\":{\"hi\":" + hangar.getHangarId() + "}}"))
                        .consumeInputStream(HangarResponse::createReader))
                .orElseThrow(IllegalStateException::new);

        HangarResponse hangar = gson.fromJson(reader, HangarResponse.class);
        System.out.println(hangar);
    }

    private static InputStreamReader createReader(InputStream in) {
        try {
            return new InputStreamReader(new ByteArrayInputStream(Base64.getDecoder()
                    .decode(IOUtils.readByteArray(in, true))));
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    public int getIsError() {
        return isError;
    }

    public Data getData() {
        return data;
    }

    @Override
    public String toString() {
        return "HangarResponse{" +
                "isError=" + isError +
                ", data=" + data +
                '}';
    }
}
