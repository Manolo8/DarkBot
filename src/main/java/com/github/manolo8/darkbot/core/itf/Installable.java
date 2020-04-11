package com.github.manolo8.darkbot.core.itf;

import com.github.manolo8.darkbot.core.installer.BotInstaller;

public interface Installable extends Manager {

    void install(BotInstaller botInstaller);
}
