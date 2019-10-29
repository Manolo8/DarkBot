package com.github.manolo8.darkbot.backpage;

import com.github.manolo8.darkbot.Main;
import com.github.manolo8.darkbot.backpage.entities.galaxy.GalaxyInfo;
import com.github.manolo8.darkbot.backpage.entities.galaxy.SpinGate;
import org.dom4j.Document;
import org.dom4j.io.SAXReader;

import java.io.InputStream;

public class GalaxyManager {

    private Main main;
    private GalaxyInfo galaxyInfo;
    private BackpageManager backpageManager;
    private long lastGatesUpdate;

    GalaxyManager(Main main, BackpageManager backpageManager) {
        this.main = main;
        this.backpageManager = backpageManager;
        this.galaxyInfo = new GalaxyInfo();
    }

    public GalaxyInfo getGalaxyInfo() {
        return galaxyInfo;
    }

    public void performGateSpin(SpinGate gate, boolean multiplier, int spinAmount, int minWait) {
        String params = "flashinput/galaxyGates.php?userID=" + main.hero.id + "&action=multiEnergy&sid=" + main.statsManager.sid + gate.getParam();

        if (galaxyInfo.getSamples() != null && galaxyInfo.getSamples() > 0) params = params + "&sample=1";
        if (multiplier) params = params + "&multiplier=1";
        if (spinAmount > 4) params = params + "&spinamount=" + spinAmount;

        handleRequest(params, -1, minWait);
    }

    public void updateGalaxyInfo(int expiryTime) {
        handleRequest("flashinput/galaxyGates.php?userID=" + main.hero.id + "&action=init&sid=" + main.statsManager.sid, expiryTime, 2500);
    }

    private void handleRequest(String params, int expiryTime, int minWait) {
        if (System.currentTimeMillis() < lastGatesUpdate + expiryTime) return;
        try (InputStream in = backpageManager.getConnection(params, minWait).getInputStream()) {
            Document document = new SAXReader().read(in);
            if (document.getRootElement() != null) galaxyInfo.updateGalaxyInfo(document.getRootElement());
        } catch (Exception e) {
            e.printStackTrace();
        }
        lastGatesUpdate = System.currentTimeMillis();
    }
}