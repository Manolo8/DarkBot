package com.github.manolo8.darkbot.core.manager;

import com.github.manolo8.darkbot.core.installer.BotInstaller;
import com.github.manolo8.darkbot.core.itf.Installable;
import com.github.manolo8.darkbot.core.objects.Gui;
import com.github.manolo8.darkbot.core.objects.swf.SwfDictionary;
import com.github.manolo8.darkbot.core.objects.swf.SwfDictionary.Entry;

import java.util.HashMap;

import static com.github.manolo8.darkbot.core.manager.Core.API;

public class GuiManager
        implements Installable {

    private final SwfDictionary        dictionary;
    private final Gui                  tempGui;
    private final HashMap<String, Gui> guis;

    private long guiAddress;

    public GuiManager() {
        this.guis = new HashMap<>();
        this.dictionary = new SwfDictionary(0);
        this.tempGui = new Gui(0);
    }

    @Override
    public void install(BotInstaller botInstaller) {
        botInstaller.guiManagerAddress.subscribe(value -> {
            guiAddress = value;
            dictionary.update(API.readMemoryLong(guiAddress + 112));
        });
    }

    public Gui fromName(String name) {

        Gui gui = this.guis.get(name);

        if (gui != null)
            return gui;

        gui = new Gui(0);

        this.guis.put(name, gui);
        this.dictionary.addLazy(name, gui::update);

        return gui;
    }

    void update() {
        dictionary.update();
    }

    void tick() {

        for (int i = 0; i < dictionary.size; i++) {
            Entry entry = dictionary.element(i);

            //If has lazy, the gui is managed by other manager
            if (entry.value == 0 || dictionary.hasLazy(entry.key))
                continue;

            tempGui.update(entry.value);
            tempGui.update();

            if (tempGui.visible)
                tempGui.show(false);
        }

    }
}
