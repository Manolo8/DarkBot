package com.github.manolo8.darkbot.backpage;

import com.github.manolo8.darkbot.Main;
import com.github.manolo8.darkbot.backpage.entities.galaxy.*;
import org.dom4j.Document;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;

import java.net.HttpURLConnection;
import java.util.ArrayList;
import java.util.List;

public class GalaxyManager {

    public BackpageManager backpageManager;
    public Jumpgate jumpgate;
    private Main main;

    public GalaxyManager(Main main, BackpageManager backpageManager) {
        this.main = main;
        this.backpageManager = backpageManager;
        this.jumpgate = new Jumpgate();
    }

    private static Gate analyzeGate(Element e) {
        Gate gate = new Gate();

        gate.setCurrent(Integer.parseInt(e.attributeValue("current")));
        gate.setCurrentWave(Integer.parseInt(e.attributeValue("currentWave")));
        gate.setId(Integer.parseInt(e.attributeValue("id")));
        gate.setLifePrice(Integer.parseInt(e.attributeValue("lifePrice")));
        gate.setLivesLeft(Integer.parseInt(e.attributeValue("livesLeft")));
        gate.setPrepared(Integer.parseInt(e.attributeValue("prepared")));
        gate.setState(e.attributeValue("state"));
        gate.setTotal(Integer.parseInt(e.attributeValue("total")));
        gate.setTotalWave(Integer.parseInt(e.attributeValue("totalWave")));
        return gate;
    }

    private static EnergyCost analyzeEnergyCost(Element e) {
        EnergyCost energyCost = new EnergyCost();

        energyCost.setMode(e.attributeValue("mode"));
        energyCost.setValue(Integer.parseInt(e.getText()));
        return energyCost;
    }


    private static Multiplier analyzeMultipler(Element e) {
        Multiplier multiplier = new Multiplier();

        multiplier.setMode(e.attributeValue("mode"));
        multiplier.setState(Integer.parseInt(e.attributeValue("state")));
        multiplier.setValue(Integer.parseInt(e.attributeValue("value")));
        return multiplier;
    }

    public void updateJumpgateInfo() {
        String params = "flashinput/galaxyGates.php?userID=" + main.hero.id + "&action=init&sid=" + main.statsManager.sid;
        updateJumpgateInfo(params, 100);
    }

    public void updateJumpgateInfo(String params, int minWait) {
        List<Gate> gates = new ArrayList<>();
        List<EnergyCost> energyCosts = new ArrayList<>();
        List<Multiplier> multipliers = new ArrayList<>();
        List<Item> items = new ArrayList<>();

        Element root = getRootElement(params, minWait);

        List<Element> gateEle = root.elementIterator("gates").next().elements();
        List<Element> multiplersEle = root.elementIterator("multipliers").next().elements();
        //List<Element> itemsEle = root.elementIterator("items").next().elements();
        energyCosts.add(analyzeEnergyCost(root.element("energy_cost")));

        for (Element e : gateEle) {
            Gate gate = analyzeGate(e);
            gates.add(gate);
        }
        for (Element e : multiplersEle) {
            Multiplier multiplier = analyzeMultipler(e);
            multipliers.add(multiplier);
        }

        jumpgate.setGates(gates);
        jumpgate.setEnergyCosts(energyCosts);
        jumpgate.setMultipliers(multipliers);
    }

    private Element getRootElement(String params, int minWait) {
        Element element = null;
        try {
            HttpURLConnection conn = main.backpage.getGalaxyConnection(params, minWait);
            SAXReader reader = new SAXReader();
            Document document = reader.read(conn.getInputStream());
            element = document.getRootElement();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return element;
    }

}
