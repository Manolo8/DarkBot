package com.github.manolo8.darkbot.backpage;

import com.github.manolo8.darkbot.Main;
import com.github.manolo8.darkbot.backpage.hangar.Hangar;
import com.github.manolo8.darkbot.backpage.hangar.HangarResponse;
import com.github.manolo8.darkbot.core.itf.Tickable;
import com.github.manolo8.darkbot.utils.Base64Utils;
import com.github.manolo8.darkbot.utils.Time;
import com.github.manolo8.darkbot.utils.http.Method;
import com.google.gson.Gson;
import com.google.gson.JsonObject;

import java.io.IOException;
import java.io.InputStream;

public class HangaManager implements Tickable {
    private final Gson gson = new Gson();

    private final Main main;
    private final BackpageManager backpage;

    private HangarResponse hangarList;
    private HangarResponse currentHangar;

    private long updateHangarListEvery = -1, updateCurrentHangarEvery = -1;
    private long hangarListUpdatedUntil, currentHangarUpdatedUntil;


    public HangaManager(Main main, BackpageManager backpage) {
        this.main = main;
        this.backpage = backpage;
    }

    @Override
    public void tick() {
        try {
            if (updateHangarListEvery != -1 && hangarListUpdatedUntil < System.currentTimeMillis()) {
                long timer = System.currentTimeMillis();
                updateHangarList();
                hangarListUpdatedUntil = System.currentTimeMillis() + updateHangarListEvery;

                System.out.println("HangarList updated in: " + (System.currentTimeMillis() - timer) + "ms");
                Time.sleep(500);
            }

            if (updateCurrentHangarEvery != -1 && currentHangarUpdatedUntil < System.currentTimeMillis()) {
                long timer = System.currentTimeMillis();
                updateCurrentHangar();
                currentHangarUpdatedUntil = System.currentTimeMillis() + updateCurrentHangarEvery;

                System.out.println("CurrentHangar updated in: " + (System.currentTimeMillis() - timer) + "ms");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public HangarResponse getHangarList() {
        return hangarList;
    }

    public HangarResponse getCurrentHangar() {
        return currentHangar;
    }

    /**
     * @param millis set -1 to disable
     */
    public void setUpdateCurrentHangarEvery(long millis) {
        this.updateCurrentHangarEvery = millis;
    }

    /**
     * @param millis set -1 to disable
     */
    public void setUpdateHangarListEvery(long millis) {
        this.updateHangarListEvery = millis;
    }

    public void updateCurrentHangar() throws Exception {
        if (hangarList == null) updateHangarList();

        for (Hangar hangar : hangarList.getData().getRet().getHangars())
            if (hangar.isActive()) {
                this.currentHangar = getHangarResponseById(hangar.getHangarId());
                break;
            }
    }

    public void updateHangarList() throws Exception {
        this.hangarList = deserializeHangar(getInputStream("getHangarList", new JsonObject()));
    }

    public HangarResponse getHangarResponseById(int hangarId) throws Exception {
        JsonObject paramObj = new JsonObject();
        JsonObject hangarObj = new JsonObject();

        hangarObj.addProperty("hi", hangarId);
        paramObj.add("params", hangarObj);

        return deserializeHangar(getInputStream("getHangar", paramObj));
    }

    public InputStream getInputStream(String action, JsonObject json) throws IOException {
        return backpage.getConnection("flashAPI/inventory.php", Method.POST)
                .setRawParam("action", action)
                .setParam("params", Base64Utils.encode(json.toString()))
                .getInputStream();
    }

    private HangarResponse deserializeHangar(InputStream in) throws Exception {
        HangarResponse hangar = gson.fromJson(Base64Utils.decode(in), HangarResponse.class);
        in.close();

        if (hangar.getData().map != null) {
            String[] lootIds = hangar.getData().map.get("lootIds");

            hangar.getData().getRet().getItemInfos()
                    .forEach(itemInfo -> itemInfo.setLocalizationId(lootIds[itemInfo.getLootId()]));

            hangar.getData().map = null;
        }

        return hangar;
    }
}
