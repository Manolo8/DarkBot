package com.github.manolo8.darkbot.backpage;

import com.github.manolo8.darkbot.Main;
import com.github.manolo8.darkbot.backpage.entities.galaxy.*;
import org.dom4j.Document;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;

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
     * @param gateName is a string of a gate {alpha, beta, gamma, delta, epsilon, zeta, kappa, lambda, hades, streuner}
     * @param sample should be used when on account extra energy amount is > 0
     * @param gateId is a int of a gate {1, 2, 3, 4, 5, 6, 7, 8, 13, 19}
     * @param multiplier boolean to use a multiplier
     * @param spinAmount amount of energy to spin {5, 10, 100}, set 0 to spin one energy
     * @param minWait is a minimum delay between requests
     */
    public void performGateSpin(String gateName, boolean sample, int gateId, boolean multiplier, int spinAmount, int minWait) {
        String params = "flashinput/galaxyGates.php?userID=" + main.hero.id + "&action=multiEnergy&sid=" + main.statsManager.sid + "&gateID=" + gateId + "&" + gateName + "=1";
        if (sample) params = params + "&sample=1";
        if (multiplier) params = params + "&multiplier=1";
        if (spinAmount > 0) params = params + "&spinamount=" + spinAmount;

        parseGalaxyInfo(params, minWait);
    }

    public void updateGalaxyInfo(int minWait) {
        parseGalaxyInfo("flashinput/galaxyGates.php?userID=" + main.hero.id + "&action=init&sid=" + main.statsManager.sid, minWait);
    }

    private void parseGalaxyInfo(String params, int minWait) {
        Element root = getRootElement(params, minWait);

        parseGates(root.elementIterator("gates"));
        parseItems(root.elementIterator("items"));
        parseMultipliers(root.elementIterator("multipliers"));
        parseEnergyCost(root.element("energy_cost"));
        galaxyInfo.updateGalaxyInfo(root);
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

    private Element getRootElement(String params, int minWait) {
        Element element = null;
        try {
            SAXReader reader = new SAXReader();
            Document document = reader.read(backpageManager.getGalaxyConnection(params, minWait).getInputStream());
            element = document.getRootElement();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return element;
    }

}
