package com.github.manolo8.darkbot.backpage;

import com.github.manolo8.darkbot.Main;
import com.github.manolo8.darkbot.backpage.entities.galaxy.*;
import org.dom4j.Document;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;

import java.net.HttpURLConnection;
import java.util.Iterator;
import java.util.stream.Collectors;

public class GalaxyManager {

    private GalaxyInfo galaxyInfo;
    private BackpageManager backpageManager;
    private Main main;
    private long lastGatesUpdate;

    public GalaxyManager(Main main, BackpageManager backpageManager) {
        this.main = main;
        this.backpageManager = backpageManager;
        this.galaxyInfo = new GalaxyInfo();
    }

    public GalaxyInfo getGalaxyInfo() {
        return galaxyInfo;
    }

    public long lastGatesUpdate() {
        return System.currentTimeMillis() - lastGatesUpdate;
    }

    /**
     * @param spinAmount amount of energy to spin {1, 5, 10, 100}
     * @return returns response code of connection
     */
    public int performGateSpin(GatesList gate, boolean multiplier, int spinAmount, int minWait) {
        String params = "flashinput/galaxyGates.php?userID=" + main.hero.id + "&action=multiEnergy&sid=" + main.statsManager.sid + gate.getParam();

        if (galaxyInfo.getSamples() != null && galaxyInfo.getSamples() > 0) params = params + "&sample=1";
        if (multiplier) params = params + "&multiplier=1";
        if (spinAmount > 4) params = params + "&spinamount=" + spinAmount;

        return parseGalaxyInfo(params, minWait);
    }

    public int updateGalaxyInfo(int minWait) {
        int responseCode = parseGalaxyInfo("flashinput/galaxyGates.php?userID=" + main.hero.id + "&action=init&sid=" + main.statsManager.sid, minWait);
        lastGatesUpdate = System.currentTimeMillis();
        return responseCode;
    }

    private int parseGalaxyInfo(String params, int minWait) {
        Element rootElement = null;
        int responseCode = -1;

        try {
            SAXReader reader = new SAXReader();

            HttpURLConnection conn = backpageManager.getConnection(params, minWait);
            conn.setRequestProperty("User-Agent", "Mozilla/5.0");

            Document document = reader.read(conn.getInputStream());
            rootElement = document.getRootElement();
            responseCode = conn.getResponseCode();
        } catch (Exception e) {
            e.printStackTrace();
        }

        if (rootElement == null) return -2;

        parseGates(rootElement.elementIterator("gates"));
        parseItems(rootElement.elementIterator("items"));
        parseMultipliers(rootElement.elementIterator("multipliers"));
        galaxyInfo.updateGalaxyInfo(rootElement);

        return responseCode;
    }

    private void parseGates(Iterator<Element> iterator) {
        if (!iterator.hasNext()) return;
        galaxyInfo.setGates(iterator.next().elements().stream().map(Gate::new).collect(Collectors.toList()));
    }

    private void parseItems(Iterator<Element> iterator) {
        if (!iterator.hasNext()) return;
        galaxyInfo.setItems(iterator.next().elements().stream().map(Item::new).collect(Collectors.toList()));
    }

    private void parseMultipliers(Iterator<Element> iterator) {
        if (!iterator.hasNext()) return;
        galaxyInfo.setMultipliers(iterator.next().elements().stream().map(Multiplier::new).collect(Collectors.toList()));
    }
}