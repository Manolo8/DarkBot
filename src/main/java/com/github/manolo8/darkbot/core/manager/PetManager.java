package com.github.manolo8.darkbot.core.manager;

import com.github.manolo8.darkbot.config.CommonConfig;
import com.github.manolo8.darkbot.core.entities.Pet;
import com.github.manolo8.darkbot.core.installer.BotInstaller;
import com.github.manolo8.darkbot.core.itf.Installable;
import com.github.manolo8.darkbot.core.objects.Gui;
import com.github.manolo8.darkbot.core.objects.PetGear;
import com.github.manolo8.darkbot.core.objects.swf.SwfArray;
import com.github.manolo8.darkbot.core.utils.Clock;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;

import static com.github.manolo8.darkbot.core.manager.Core.API;
import static com.github.manolo8.darkbot.core.manager.Core.APIU;

public class PetManager
        implements Installable {

    private final StatsManager statsManager;
    private final MapManager   mapManager;
    private final CommonConfig config;

    private final PetGear                   DISABLE;
    private final HashMap<Integer, PetGear> allGears;
    private final Gui                       gui;
    private final Pet                       pet;

    private final Clock   clock;
    private       boolean guiVisible;
    private       boolean seeAlive;
    private       boolean expected;

    private int stageGear;
    private int stageActive;
    private int stageDisable;

    private long petWindowAddress;

    private long     asset;
    private long     gearAddress;
    private SwfArray gearsArray;

    private boolean gearsOk;

    private PetGear current;
    private PetGear gearToSet;

    private List<PetGear> gears;

    public PetManager(Core core) {

        this.statsManager = core.getStatsManager();
        this.mapManager = core.getMapManager();
        this.config = core.getCommonConfig();

        this.gui = core.getGuiManager().fromName("pet");
        this.pet = core.getHeroManager().pet;

        this.gears = new ArrayList<>();
        this.gearsArray = new SwfArray(0, 32);

        this.clock = new Clock();

        allGears = new HashMap<>();
        allGears.put(0, DISABLE = new PetGear(0, "Disable", 0));
        allGears.put(1, new PetGear(1, "Passive", 0));
        allGears.put(2, new PetGear(2, "Protection", 0));
        allGears.put(4, new PetGear(4, "Box collection", 0));
        allGears.put(5, new PetGear(5, "Resource collection", 0));
        allGears.put(10, new PetGear(10, "Kamikaze", 30000));
    }

    @Override
    public void install(BotInstaller botInstaller) {
        botInstaller.mainApplicationAddress.subscribe(value -> asset = 0);
        botInstaller.petWindowAddress.subscribe(value -> {
            this.petWindowAddress = value;
            this.asset = 0;
            this.gearAddress = 0;
            this.gearsOk = false;
        });
    }

    public void update() {
        long address = API.readMemoryLong(API.readMemoryLong(gearAddress + 176) + 224);

        if (address != gearsArray.address)
            gearsArray.update(address);

        gearsArray.update();
        gui.update();

        verifyGears();
    }

    private void verifyGears() {

        checkSize();

        if (checkPet())
            updateGears();
    }

    private void checkSize() {
        if (gears.size() > gearsArray.size) {
            gears.forEach(gear -> gear.active = false);
            gears.clear();
        }
    }

    private boolean checkPet() {

        if (pet.isInvalid()) {
            if (current != null) {
                current.used();
                current.active = false;
                current = null;
            }
            return false;
        }

        return true;
    }

    private void updateGears() {

        long code = API.readMemoryLong(APIU.inElementGetChildren(asset, 1) + 152);
        gearsOk = false;

        for (int i = 0; i < gearsArray.size; i++) {

            long currentAddress = gearsArray.elements[i];
            int  id             = API.readMemoryInt(currentAddress + 172);

            PetGear gear;

            if (gears.size() < i + 1)
                gears.add(gear = gearById(id));
            else if (gears.get(i).id != id)
                gears.set(i, gear = gearById(id));
            else
                gear = gears.get(i);

            gear.update(currentAddress);
            gear.update();

            boolean active = code == gear.code;

            if (active)
                gearsOk = true;

            if (!active && gear.active)
                gear.used();

            if (active)
                current = gear;

            gear.active = active;
        }
    }

    public void tick() {

        if (pet.isInvalid() != expected) {
            expected = pet.isInvalid();
            clock.back(5_000);
        }

        if (!gearsOk && gearToSet != DISABLE && shouldActivePET())
            activePET();

        if (shouldSetupPetAsset())
            setupPetAsset();
        else
            tickPet();
    }

    private boolean shouldSetupPetAsset() {
        return asset == 0 || gearAddress == 0;
    }

    private void setupPetAsset() {
        if (petWindowAddress == 0)
            return;
        gearAddress = APIU.inElementGetLastChildren(petWindowAddress);
        if (gearAddress == 0)
            return;
        long temp = APIU.inArrayGetAddressIfMatch(API.readMemoryLong(petWindowAddress + 400), 32, value -> API.readMemoryInt(value + 172) == 54);
        if (temp == 0)
            return;
        temp = APIU.inArrayGetAddressIfMatch(API.readMemoryLong(temp + 184), 32, value -> API.readMemoryInt(value + 168) == 72);
        if (temp == 0)
            return;
        temp = APIU.inElementGetFirstChildren(temp);
        if (temp == 0)
            return;
        temp = API.readMemoryLong(temp + 176);
        if (temp <= 10)
            return;
        long code = API.readMemoryLong(APIU.inElementGetChildren(temp, 1) + 152);

        if (code != 0)
            asset = temp;
    }

    private void tickPet() {
        if (shouldDisablePET()) {
            seeAlive = false;
            if (!pet.isInvalid())
                disablePET();
            else if (guiVisible)
                finish();
        } else if (shouldActivePET()) {
            activePET();
        } else if (!pet.isInvalid()) {
            seeAlive = true;
            if (shouldSetPetGear())
                changePetGear();
            else if (guiVisible)
                finish();
        }

        if (gui.visible && !guiVisible)
            guiVisible = gui.show(false);
    }

    private boolean shouldDisablePET() {
        return gearToSet == DISABLE;
    }

    private void disablePET() {
        if (gui.show(true)) {

            guiVisible = true;

            if (!clock.isBiggerThenReset(500))
                return;

            if (stageDisable == 0) {
                gui.click(29, 122);
                stageDisable++;
            } else if (stageDisable == 1) {
                clock.back(5000);
                stageDisable = 0;
            }

        } else {
            stageDisable = 0;
        }
    }

    private boolean shouldActivePET() {
        if (pet.isInvalid()) {
            return config.MAX_PET_DEATHS == -1 || statsManager.petDeaths < config.MAX_PET_DEATHS;
        } else {
            stageActive = 0;
            return false;
        }
    }

    private void activePET() {
        if (gui.show(true)) {

            guiVisible = true;

            if (!clock.isBiggerThenReset(1500))
                return;

            if (stageActive == 0) {
                gui.click(29, 122);
                stageActive++;
            } else if (stageActive == 1) {
                gui.click(29, 122);

                if (seeAlive) {
                    seeAlive = false;
                    statsManager.petDeaths++;
                }

                stageActive++;
            } else {
                clock.back(5000);
                stageActive = 0;
            }

        } else {
            stageActive = 0;
        }
    }

    private boolean shouldSetPetGear() {
        return gearToSet != null && !gearToSet.isInCooldown() && !gearToSet.active && gears.indexOf(gearToSet) >= 0;
    }

    private void changePetGear() {
        if (gui.show(true)) {
            guiVisible = true;
            if (!clock.isBiggerThenReset(250))
                return;

            if (stageGear == 0) {
                gui.click(157, 116);
                stageGear++;
            } else if (stageGear == 1) {
                int index = gears.indexOf(gearToSet);

                gui.click(167, 151 + (index * 23));
                stageGear++;
            } else {
                clock.back(5000);
                stageGear = 0;
            }
        } else {
            stageGear = 0;
        }
    }

    private void finish() {
        guiVisible = false;
    }

    public void setGear(PetGear gear) {
        this.gearToSet = gear;
    }

    public PetGear current() {
        return current;
    }

    public PetGear gearToSet() {
        return gearToSet;
    }

    public PetGear gearById(int id) {

        PetGear gear = allGears.get(id);

        return gear == null ? new PetGear(id, "Unknown", 0) : gear;
    }

    public Collection<PetGear> getAllGears() {
        return allGears.values();
    }
}
