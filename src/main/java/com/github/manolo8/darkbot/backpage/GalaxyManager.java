package com.github.manolo8.darkbot.backpage;

import com.github.manolo8.darkbot.Main;
import com.github.manolo8.darkbot.backpage.entities.galaxy.*;
import org.dom4j.Document;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;

import java.net.HttpURLConnection;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class GalaxyManager {

    public GalaxyInfo galaxyInfo;
    private BackpageManager backpageManager;
    private Main main;

    public GalaxyManager(Main main, BackpageManager backpageManager) {
        this.main = main;
        this.backpageManager = backpageManager;
        this.galaxyInfo = new GalaxyInfo();
    }

    /**
     * @param gate       choose gate from GatesList to spin
     * @param multiplier boolean to use a multiplier
     * @param spinAmount amount of energy to spin {5, 10, 100}, set 0 to spin one energy
     * @param minWait    minimum delay between requests
     * @return response code from connection
     */

    public int performGateSpin(GatesList gate, boolean multiplier, int spinAmount, int minWait) {
        String params = "flashinput/galaxyGates.php?userID=" + main.hero.id + "&action=multiEnergy&sid=" + main.statsManager.sid + gate.getParam();

        if (galaxyInfo.getSamples() != null && galaxyInfo.getSamples() > 0) params = params + "&sample=1";
        if (multiplier) params = params + "&multiplier=1";
        if (spinAmount > 4) params = params + "&spinamount=" + spinAmount;

        return parseGalaxyInfo(params, minWait);
    }

    public int updateGalaxyInfo(int minWait) {
        return parseGalaxyInfo("flashinput/galaxyGates.php?userID=" + main.hero.id + "&action=init&sid=" + main.statsManager.sid, minWait);
    }

    private int parseGalaxyInfo(String params, int minWait) {
        Element rootElement = null;
        int responseCode = -1;

        try {
            SAXReader reader = new SAXReader();

            HttpURLConnection conn = backpageManager.getGalaxyConnection(params, minWait);
            responseCode = conn.getResponseCode();
            Document document = reader.read(conn.getInputStream());
            rootElement = document.getRootElement();
        } catch (Exception e) {
            e.printStackTrace();
        }

        if (rootElement == null) return -2;

        parseGates(rootElement.elementIterator("gates"));
        parseItems(rootElement.elementIterator("items"));
        parseMultipliers(rootElement.elementIterator("multipliers"));
        parseEnergyCost(rootElement.element("energy_cost"));
        galaxyInfo.updateGalaxyInfo(rootElement);

        return responseCode;
    }

    private void parseGates(Iterator<Element> iterator) {
        if (iterator.hasNext()) {
            List<Gate> gates = new ArrayList<>();
            List<Element> gateEle = iterator.next().elements();

            for (Element e : gateEle) {
                Gate gate = new Gate(e);
                gates.add(gate);
            }
            galaxyInfo.setGates(gates);
        }
    }

    private void parseItems(Iterator<Element> iterator) {
        if (iterator.hasNext()) {
            List<Item> items = new ArrayList<>();
            List<Element> itemsEle = iterator.next().elements();

            for (Element e : itemsEle) {
                Item item = new Item(e);
                items.add(item);
            }
            galaxyInfo.setItems(items);
        }
    }

    private void parseMultipliers(Iterator<Element> iterator) {
        if (iterator.hasNext()) {
            List<Multiplier> multipliers = new ArrayList<>();
            List<Element> multipliersEle = iterator.next().elements();

            for (Element e : multipliersEle) {
                Multiplier multiplier = new Multiplier(e);
                multipliers.add(multiplier);
            }
            galaxyInfo.setMultipliers(multipliers);
        }
    }

    private void parseEnergyCost(Element element) {
        if (element.getText() == null) return;
        List<EnergyCost> energyCost = new ArrayList<>();

        energyCost.add(new EnergyCost(element));
        galaxyInfo.setEnergyCosts(energyCost);
    }
}
