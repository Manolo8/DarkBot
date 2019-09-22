package com.github.manolo8.darkbot.backpage;

import com.github.manolo8.darkbot.Main;
import com.github.manolo8.darkbot.backpage.entities.galaxy.*;
import com.github.manolo8.darkbot.utils.XmlHelper;
import org.dom4j.Document;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;

import java.util.ArrayList;
import java.util.List;

public class GalaxyManager {

    public Jumpgate jumpgate;
    private BackpageManager backpageManager;
    private Main main;

    public GalaxyManager(Main main, BackpageManager backpageManager) {
        this.main = main;
        this.backpageManager = backpageManager;
        this.jumpgate = new Jumpgate();
    }

    public void performGateSpin(String gateName, boolean sample, int gateId, int multiplier, int minWait) {
        String params = "flashinput/galaxyGates.php?userID=" + main.hero.id + "&action=multiEnergy&sid=" + main.statsManager.sid + "&gateID=" + gateId + "&" + gateName + "=1";
        if (sample) params = params + "&sample=1";
        if (multiplier > 0) params = params + "&multiplier=" + multiplier;

        parseJumpgate(params, minWait);
    }

    public void updateJumpgateInfo(int minWait) {
        parseJumpgate("flashinput/galaxyGates.php?userID=" + main.hero.id + "&action=init&sid=" + main.statsManager.sid, minWait);
    }

    private void parseJumpgate(String params, int minWait) {
        Element root = getRootElement(params, minWait);

        List<Gate> gates = new ArrayList<>();
        List<EnergyCost> energyCosts = new ArrayList<>();
        List<Multiplier> multipliers = new ArrayList<>();
        List<Item> items = new ArrayList<>();

        if (root.elementIterator("gates").hasNext()) {
            List<Element> gateEle = root.elementIterator("gates").next().elements();

            for (Element e : gateEle) {
                Gate gate = new Gate(e);
                gates.add(gate);
            }
            jumpgate.setGates(gates);
        }

        if (root.elementIterator("items").hasNext()) {
            List<Element> itemsEle = root.elementIterator("items").next().elements();

            for (Element e : itemsEle) {
                Item item = new Item(e);
                items.add(item);
            }
            jumpgate.setItems(items);
        }

        if (root.elementIterator("multipliers").hasNext()) {
            List<Element> multipliersEle = root.elementIterator("multipliers").next().elements();

            for (Element e : multipliersEle) {
                Multiplier multiplier = new Multiplier(e);
                multipliers.add(multiplier);
            }
            jumpgate.setMultipliers(multipliers);
        }

        energyCosts.add(new EnergyCost(root.element("energy_cost")));
        jumpgate.setEnergyCosts(energyCosts);
        jumpgate.setMoney(XmlHelper.getValueInt(root, "money"));
        jumpgate.setSamples(XmlHelper.getValueInt(root, "samples"));
        jumpgate.setSpinOnSale(XmlHelper.getValueInt(root, "spinOnSale"));
        jumpgate.setSpinSalePercentage(XmlHelper.getValueInt(root, "spinSalePercentage"));
        jumpgate.setGalaxyGateDay(XmlHelper.getValueInt(root, "galaxyGateDay"));
        jumpgate.setBonusRewardsDay(XmlHelper.getValueInt(root, "bonusRewardsDay"));
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
