package com.github.manolo8.darkbot.backpage.types;

import com.github.manolo8.darkbot.backpage.utils.HtmlWalker;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

public class GalaxyGateHandler {

    private static HashMap<String, Integer> ITEM_OFFSET = new HashMap<String, Integer>() {{
        put("nanohull", 0);
        put("logfile", 100);
        put("rocket", 200);
        put("battery", 300);
        put("ore", 400);
        put("part", 500);
        put("voucher", 600);
    }};

    private long lastDate;

    public int                        uridium;
    public int                        cost;
    public int                        extraEnergy;
    public boolean                    galaxyGateDay;
    public boolean                    bonusRewardsDay;
    public Set<Integer>               spinAmounts;
    public HashMap<Integer, Gate>     gates;
    public HashMap<Integer, ItemInfo> items;

    public int  currentGateId;
    public int  selectedSpinAmount;
    public Gate currentGate;

    public GalaxyGateHandler() {
        this.gates = new HashMap<>();
        this.items = new HashMap<>();
        this.spinAmounts = new HashSet<>();
        this.selectedSpinAmount = 1;

        this.gates.put(1, currentGate = new Gate("alpha", 1));
        this.gates.put(2, new Gate("beta", 1));
        this.gates.put(3, new Gate("gamma", 1));
        this.gates.put(4, new Gate("delta", 2));
        this.gates.put(5, new Gate("epsilon", 3));
        this.gates.put(6, new Gate("zeta", 4));
        this.gates.put(7, new Gate("kappa", 5));
        this.gates.put(8, new Gate("lambda", 6));
        this.gates.put(13, new Gate("hades", 7));
        this.gates.put(19, new Gate("streuner", 8));

        this.items.put(ITEM_OFFSET.get("nanohull"), new ItemInfo("NANOHULL"));
        this.items.put(ITEM_OFFSET.get("logfile"), new ItemInfo("LOG"));
        this.items.put(ITEM_OFFSET.get("rocket") + 11, new ItemInfo("ACM-01"));
        this.items.put(ITEM_OFFSET.get("battery") + 2, new ItemInfo("MCB-25"));
        this.items.put(ITEM_OFFSET.get("rocket") + 3, new ItemInfo("PLT-2021"));
        this.items.put(ITEM_OFFSET.get("battery") + 4, new ItemInfo("UCB-100"));
        this.items.put(ITEM_OFFSET.get("battery") + 3, new ItemInfo("MCB-50"));
        this.items.put(ITEM_OFFSET.get("battery") + 5, new ItemInfo("SAB"));
        this.items.put(ITEM_OFFSET.get("ore") + 4, new ItemInfo("Xenomit"));
        this.items.put(ITEM_OFFSET.get("part"), new ItemInfo("Gate"));
        this.items.put(ITEM_OFFSET.get("voucher"), new ItemInfo("Repair"));
    }

    public void load(String data) {
        HtmlWalker walker = new HtmlWalker(data, 0, data.length());

        String str;
        while ((str = walker.nextElement()) != null) {
            switch (str) {
                case "multiplier":

                    String name = null;
                    int amount = 0;

                    while ((str = walker.nextElementAttribute()) != null) {
                        switch (str) {
                            case "mode":
                                name = walker.content();
                                break;
                            case "value":
                                amount = walker.contentAsInteger();
                                break;
                        }
                    }

                    if (name != null)
                        setMultiplier(name, amount);

                    break;
                case "gate": {
                    Gate temp = new Gate(null, 0);

                    while ((str = walker.nextElementAttribute()) != null) {
                        switch (str) {
                            case "current":
                                temp.parts = walker.contentAsInteger();
                                break;
                            case "currentWave":
                                temp.currentWave = walker.contentAsInteger();
                                break;
                            case "id":
                                temp.id = walker.contentAsInteger();
                                break;
                            case "lifePrice":
                                temp.lifePrice = walker.contentAsInteger();
                                break;
                            case "livesLeft":
                                temp.lifeLeft = walker.contentAsInteger();
                                break;
                            case "prepared":
                                temp.prepared = walker.contentAsInteger() == 1;
                                break;
                            case "total":
                                temp.totalParts = walker.contentAsInteger();
                                break;
                            case "totalWave":
                                temp.totalWave = walker.contentAsInteger();
                                break;
                        }
                    }

                    updateOrCreateGate(temp);

                    break;
                }
                case "spinamount":
                    addSpinAmount(walker.contentAsInteger());
                    break;
                case "money":
                    this.uridium = walker.contentAsInteger();
                    break;
                case "samples":
                    this.extraEnergy = walker.contentAsInteger();
                    break;
                case "spinamount_selected":
                    this.selectedSpinAmount = walker.contentAsInteger();
                    break;
                case "energy_cost":
                    this.cost = walker.contentAsInteger();
                    break;
                case "galaxyGateDay":
                    this.galaxyGateDay = walker.contentAsInteger() == 1;
                    break;
                case "bonusRewardsDay":
                    this.bonusRewardsDay = walker.contentAsInteger() == 1;
                    break;
                case "item":

                    String itemType = null;
                    int itemId = 0;
                    int itemAmount = 0;
                    long date = 0;
                    int gateId = -1;
                    int current = 0;
                    boolean duplicate = false;

                    while ((str = walker.nextElementAttribute()) != null) {
                        switch (str) {
                            case "type":
                                itemType = walker.content();
                                break;
                            case "item_id":
                                itemId = walker.contentAsInteger();
                                break;
                            case "amount":
                                itemAmount = walker.contentAsInteger();
                                break;
                            case "date":
                                date = walker.contentAsALong();
                                break;
                            case "gate_id":
                                gateId = walker.contentAsInteger();
                                break;
                            case "current":
                                current = walker.contentAsInteger();
                                break;
                            case "duplicate":
                                duplicate = walker.contentAsInteger() == 1;
                                break;
                        }
                    }

                    if (!duplicate && date >= lastDate) {
                        lastDate = date;
                        itemAdded(itemType, itemId, itemAmount, gateId, current);
                    }

                    break;
            }
        }
    }

    private void updateOrCreateGate(Gate gate) {
        Gate temp = gates.get(gate.id);

        if (temp != null) {
            temp.id = gate.id;
            temp.parts = gate.parts;
            temp.totalParts = gate.totalParts;
            temp.currentWave = gate.currentWave;
            temp.totalWave = gate.totalWave;
            temp.lifePrice = gate.lifePrice;
            temp.lifeLeft = gate.lifeLeft;
            temp.prepared = gate.prepared;
        }
    }

    private void setMultiplier(String name, int amount) {
        Gate temp = null;

        for (Gate gate : gates.values())
            if (gate.name.equals(name)) {
                temp = gate;
                break;
            }

        if (temp != null)
            temp.multiplier = amount;
    }

    private void addSpinAmount(int amount) {
        this.spinAmounts.add(amount);
    }

    private void itemAdded(String itemType, int itemId, int itemAmount, int gateId, int current) {

        if (itemType.equals("part")) {
            Gate gate = gates.get(gateId);
            if (gate != null)
                gate.parts = current;
        }

        Integer offset = ITEM_OFFSET.get(itemType);

        if (offset != null) {

            ItemInfo info = items.get(offset + itemId);

            if (info != null)
                info.amount += itemAmount == 0 ? 1 : itemAmount;
        }
    }
}