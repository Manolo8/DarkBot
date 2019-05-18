package com.github.manolo8.darkbot.core.manager;

import com.github.manolo8.darkbot.BackpageManager;
import com.github.manolo8.darkbot.Main;
import com.github.manolo8.darkbot.core.entities.Drone;
import com.github.manolo8.darkbot.core.entities.Hangar;
import com.google.gson.*;

import java.util.ArrayList;
import java.util.Base64;

import static com.github.manolo8.darkbot.Main.API;

public class HangarManager {

    private final Main main;
    private BackpageManager backpageManager;
    private boolean disconnecting = false;
    private Character exitKey = 'l';
    private long lastChangeHangar = 0;
    private long disconectTime = 0;
    private ArrayList<Hangar> hangars;
    private ArrayList<Drone> drones;

    public HangarManager(Main main){
        this.main = main;
        this.backpageManager = main.backpage;
        this.hangars = new ArrayList<Hangar>();
        this.drones = new ArrayList<Drone>();
    }

    public boolean changeHangar(String hangarID) {
        if (!this.disconnecting) {
            disconnect();
        }
        if (this.lastChangeHangar <= System.currentTimeMillis() - 40000 && this.main.backpage.sidStatus().contains("OK")) {
            if (this.disconectTime <= System.currentTimeMillis() - 20000) {

                String url = "indexInternal.es?action=internalDock&subAction=changeHangar&hangarId=" + hangarID;
                try {
                    backpageManager.getConnection(url).getResponseCode();
                    this.disconnecting = false;
                } catch (Exception e) {
                    e.printStackTrace();
                }
                this.lastChangeHangar = System.currentTimeMillis();
            }
        } else {
            return false;
        }

        return true;
    }

    public void checkDrones() {
        updateHangars();
        updateDrones();
        for (Drone drone : drones){
            if ((drone.getDamage()/100) >= main.config.GENERAL.SAFETY.REPAIR_DRONE_PORCENTAGE){
                repairDron(drone);
                System.out.println("Drone Repair");
            }
        }
    }

    public void updateDrones() {
        try {
            String hangarID = getActiveHangar();

            if (hangarID != null) {
                String decodeParams = "{\"params\":{\"hi\":" + hangarID + "}}";
                String encodeParams = Base64.getEncoder().encodeToString(decodeParams.getBytes("UTF-8"));
                String url = "flashAPI/inventory.php?action=getHangar&params="+encodeParams;
                String json = this.backpageManager.getDataInventory(url);

                JsonElement element = new JsonParser().parse(json).getAsJsonObject().get("data")
                        .getAsJsonObject().get("ret").getAsJsonObject().get("hangars");

                if (element.isJsonArray()) {
                    for (JsonElement hangar : element.getAsJsonArray()) {
                        if (hangar.getAsJsonObject().get("hangar_is_active").getAsBoolean()) {
                            JsonArray dronesArray = hangar.getAsJsonObject().get("general").getAsJsonObject().get("drones").getAsJsonArray();
                            for (JsonElement dron : dronesArray){
                                this.drones.add(new Gson().fromJson(dron,Drone.class));
                            }
                        }
                    }
                } else {
                    if (element.getAsJsonObject().get("hangar_is_active").getAsBoolean()) {
                        JsonArray dronesArray = element.getAsJsonObject().get("general").getAsJsonObject().get("drones").getAsJsonArray();
                        for (JsonElement dron : dronesArray){
                           this.drones.add(new Gson().fromJson(dron,Drone.class));
                        }
                    }
                }
            }

        } catch (Exception e){
            e.printStackTrace();
        }
    }

    private boolean repairDron(Drone drone){
        try {
            String decodeParams =
                    "{\"action\":\"repairDrone\",\"lootId\":\"" + drone.getLoot() + "\",\"repairPrice\":" + drone.getRepairPrice() +
                            ",\"params\":{\"hi\":" + getActiveHangar() + "}," +
                            "\"itemId\":\"" + drone.getItemId() + "\",\"repairCurrency\":\"" + drone.getRepairCurrency() +
                            "\",\"quantity\":1,\"droneLevel\":" + drone.getDroneLevel() + "}";
            String encodeParams = Base64.getEncoder().encodeToString(decodeParams.getBytes("UTF-8"));
            String url = "flashAPI/inventory.php?action=repairDrone&params="+encodeParams;
            String json = this.backpageManager.getDataInventory(url);
            if (json.contains("'isError':0")){
                return true;
            } else {
                return false;
            }
        } catch (Exception e){
            e.printStackTrace();
            return false;
        }
    }

    public void updateHangars() {
        String params = "flashAPI/inventory.php?action=getHangarList";
        String decodeString = this.main.backpage.getDataInventory(params);

        if (decodeString != null) {
            JsonArray hangarsArray =  new JsonParser().parse(decodeString).getAsJsonObject().get("data").getAsJsonObject()
                    .get("ret").getAsJsonObject().get("hangars").getAsJsonArray();
            for (JsonElement hangar : hangarsArray) {
                this.hangars.add(new Gson().fromJson(hangar,Hangar.class));
            }
        }
    }

    public String getActiveHangar(){
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
