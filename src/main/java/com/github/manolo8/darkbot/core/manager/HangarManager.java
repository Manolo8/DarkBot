package com.github.manolo8.darkbot.core.manager;

import com.github.manolo8.darkbot.Main;
import com.github.manolo8.darkbot.core.entities.Dron;
import com.github.manolo8.darkbot.core.entities.Hangar;
import com.github.manolo8.darkbot.core.objects.swf.Array;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Base64;

import static com.github.manolo8.darkbot.Main.API;

public class HangarManager {

    private final Main main;
    private boolean disconnecting = false;
    private Character exitKey = 'l';
    private long lastChangeHangar = 0;
    private long disconectTime = 0;
    private ArrayList<Hangar> hangars;
    private ArrayList<Dron> drones;

    /*
     * Para sacar los vants
     * /flashAPI/inventory.php/action=getHangar&params=
     * Params es:
     * {"params":{"hi": HANGARID}}
     * En base 64
     */

    public HangarManager(Main main){
        this.main = main;
        this.hangars = new ArrayList<Hangar>();
        this.drones = new ArrayList<Dron>();
    }

    public boolean changeHangar(String hangarID) {
        HttpURLConnection conn = null;
        if (!this.disconnecting) {
            disconnect();
        }
        if (this.lastChangeHangar <= System.currentTimeMillis() - 40000 && this.main.backpage.sidStatus().contains("OK") || !hangarID.isEmpty()) {
            if (this.disconectTime <= System.currentTimeMillis() - 20000) {
                String instance = this.main.statsManager.instance, sid = this.main.statsManager.sid;
                if (instance == null || instance.isEmpty() || sid == null || sid.isEmpty()) return false;
                String url = instance + "/indexInternal.es?action=internalDock&subAction=changeHangar&hangarId=" + hangarID;
                try {
                    conn = (HttpURLConnection) new URL(url)
                            .openConnection();
                    conn.setInstanceFollowRedirects(false);
                    conn.setRequestProperty("Cookie", "dosid=" + sid);
                    conn.getResponseCode();
                    conn.disconnect();
                    this.disconnecting = false;
                } catch (Exception e) {
                    e.printStackTrace();
                } finally {
                    if (conn != null) {
                        conn.disconnect();
                    }
                }
                this.lastChangeHangar = System.currentTimeMillis();
            }

        } else {
            return false;
        }

        return true;
    }

    private String getDataInventory(String params){
        String data = null;
        HttpURLConnection conn = null;
        InputStream inputStream;
        String instance = this.main.statsManager.instance, sid = this.main.statsManager.sid;
        if (instance == null || instance.isEmpty() || sid == null || sid.isEmpty()) return data;

        String url = instance + params;
        try {
            conn = (HttpURLConnection) new URL(url)
                    .openConnection();
            conn.setInstanceFollowRedirects(false);
            conn.setRequestProperty("Content-Type","application/x-www-form-urlencoded");
            conn.setRequestProperty("Cookie", "dosid=" + sid);
            int responseCode = conn.getResponseCode();
            if (200 <= responseCode && responseCode <= 299) {
                inputStream = conn.getInputStream();
            } else {
                inputStream = conn.getErrorStream();
            }

            BufferedReader in = new BufferedReader(new InputStreamReader(inputStream));
            StringBuilder responseb = new StringBuilder();
            String currentLine;

            while ((currentLine = in.readLine()) != null)
                responseb.append(currentLine);

            in.close();
            inputStream.close();

            byte[] base64Decode = Base64.getDecoder().decode(responseb.toString());
            data = new String(base64Decode, "UTF-8");

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (conn != null ){
                conn.disconnect();
            }
        }

        return data;
    }

    public void updateDrones() {
        try {
            String hangarID = getActiveHangar();

            if (hangarID != null) {
                String decodeParams = "{\"params\":{\"hi\":" + hangarID + "}}";
                String encodeParams = Base64.getEncoder().encodeToString(decodeParams.getBytes("UTF-8"));
                String url = "/flashAPI/inventory.php?action=getHangar&params="+encodeParams;
                String json = getDataInventory(url);
                JsonObject object =  new JsonParser().parse(json).getAsJsonObject();
                JsonObject data = object.get("data").getAsJsonObject();
                JsonObject ret  = data.get("ret").getAsJsonObject();
                JsonArray hangardata = ret.get("hangars").getAsJsonArray();

                for (JsonElement hangar : hangardata.getAsJsonArray()) {
                    boolean active = hangar.getAsJsonObject().get("hangar_is_active").getAsBoolean();

                    if (active) {
                        JsonArray dronesArray = hangar.getAsJsonObject().get("general").getAsJsonObject().get("drones").getAsJsonArray();
                        for (JsonElement dron : dronesArray){
                            JsonObject dronJson = dron.getAsJsonObject();
                            String lootId = "drone_iris";
                            switch (dronJson.get("L").getAsInt()){
                                case 1:
                                    lootId = "drone_flax";
                                    break;
                                case 2:
                                    lootId = "drone_iris";
                                    break;
                                case 3:
                                    lootId = "drone_apis";
                                    break;
                                case 4:
                                    lootId = "drone_zeus";
                                    break;
                            }
                            int damage = Integer.parseInt(dronJson.get("HP").getAsString().replace("%"," ").trim());
                            this.drones.add(new Dron(lootId,dronJson.get("repair").getAsInt(),dronJson.get("I").getAsString(),
                                    dronJson.get("currency").getAsString(),dronJson.get("LV").getAsInt(),damage));
                        }
                    }
                }
            }

        } catch (Exception e){}
    }

    private boolean repairDron(Dron dron, String activeHangarId){
        try {
            String decodeParams =
                    "{\"action\":\"repairDrone\",\"lootId\":\"" + dron.getLootId() + "\",\"repairPrice\":" + dron.getRepairPrice() +
                            ",\"params\":{\"hi\":" + activeHangarId + "}," +
                            "\"itemId\":\"" + dron.getItemId() + "\",\"repairCurrency\":\"" +dron.getRepairCurrency() +
                            "\",\"quantity\":1,\"droneLevel\":" + dron.getDroneLevel() + "}";
            String encodeParams = Base64.getEncoder().encodeToString(decodeParams.getBytes("UTF-8"));
            String url = "/flashAPI/inventory.php?action=repairDrone&params="+encodeParams;
            String json = getDataInventory(url);
            if (json.contains("'isError':0")){
                return true;
            } else {
                return false;
            }
        } catch (Exception e){
            return false;
        }
    }

    public void updateHangars() {
        String params = "/flashAPI/inventory.php?action=getHangarList";
        String decodeString = getDataInventory(params);

        JsonObject object =  new JsonParser().parse(decodeString).getAsJsonObject();
        JsonArray hangarsArray  = object.get("data").getAsJsonObject().get("ret").getAsJsonObject().get("hangars").getAsJsonArray();

        for (JsonElement hangar : hangarsArray) {
            boolean active = hangar.getAsJsonObject().get("hangar_is_active").getAsBoolean();
            String hangarID = hangar.getAsJsonObject().get("hangarID").getAsString();
            this.hangars.add(new Hangar(hangarID,active));
        }
    }

    public String getActiveHangar(){
        updateHangars();
        for(Hangar hangar : hangars){
            if (hangar.isHangar_is_active()){
                return hangar.getHangarID();
            }
        }
        return null;
    }

    public void disconnect() {
        API.keyboardClick(exitKey);
        disconectTime = System.currentTimeMillis();
        disconnecting = true;
    }
}
