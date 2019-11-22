package com.github.manolo8.darkbot.config;

import com.github.manolo8.darkbot.core.itf.NpcExtraProvider;
import com.github.manolo8.darkbot.extensions.features.Feature;
import com.github.manolo8.darkbot.utils.I18n;

public enum NpcExtra implements NpcExtraFlag {
    NO_CIRCLE("NC", I18n.get("config.npc_extra.no_circle"), I18n.get("config.npc_extra.no_circle.desc")),
    IGNORE_OWNERSHIP("IO", I18n.get("config.npc_extra.ignore_ownership"), I18n.get("config.npc_extra.ignore_ownership.desc")),
    IGNORE_ATTACKED("IA", I18n.get("config.npc_extra.ignore_attacked"), I18n.get("config.npc_extra.ignore_attacked.desc")),
    PASSIVE("P", I18n.get("config.npc_extra.passive"), I18n.get("config.npc_extra.passive.desc")),
    ATTACK_SECOND("AS", I18n.get("config.npc_extra.attack_second"), I18n.get("config.npc_extra.attack_second.desc"));

    private final String shortName, name, description;
    NpcExtra(String shortName, String name, String description) {
        this.shortName = shortName;
        this.name = name;
        this.description = description;
    }

    @Override
    public String getShortName() {
        return shortName;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public String getDescription() {
        return description;
    }

    @Feature(name = "Npc extra flag provider", description = "Provides default npc extra flags")
    public static class DefaultNpcExtraProvider implements NpcExtraProvider {
        @Override
        public NpcExtraFlag[] values() {
            return NpcExtra.values();
        }
    }

}