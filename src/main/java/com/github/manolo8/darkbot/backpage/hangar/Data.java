package com.github.manolo8.darkbot.backpage.hangar;

public class Data {
    private Ret ret;
    private Money money;
    private Map  map;

    public Ret getRet() {
        return ret;
    }

    public Money getMoney() {
        return money;
    }

    public Map getMap() {
        return map;
    }

    @Override
    public String toString() {
        return "Data{" +
                "ret=" + ret +
                ", money=" + money +
                ", map=" + map +
                '}';
    }
}
