package com.github.manolo8.darkbot.config;

import com.github.manolo8.darkbot.config.utils.Ignorable;
import com.github.manolo8.darkbot.core.entities.BasePoint;
import com.github.manolo8.darkbot.core.entities.BattleStation;
import com.github.manolo8.darkbot.core.entities.Entity;
import com.github.manolo8.darkbot.core.entities.Portal;
import com.github.manolo8.darkbot.utils.I18n;

import java.io.Serializable;
import java.util.Objects;

public class SafetyInfo implements Serializable, Ignorable {
    public enum Type {
        PORTAL(I18n.get("gui.safety_places.type.portal")),
        CBS(I18n.get("gui.safety_places.type.cbs")),
        BASE(I18n.get("gui.safety_places.type.base"));
        public static Type of(Entity entity) {
            if (entity instanceof Portal) return PORTAL;
            if (entity instanceof BattleStation) return CBS;
            if (entity instanceof BasePoint) return BASE;
            return null;
        }
        String text;
        Type(String text) {this.text = text;}
        public String toString() {return text;}
    }

    public Type type;
    public int x, y, diameter;
    public transient Entity entity;
    public transient double distance;

    public SafetyInfo() {}

    public SafetyInfo(Type type, int x, int y, Entity entity) {
        this.type = type;
        this.x = x;
        this.y = y;
        this.diameter = type == Type.BASE ? 1500 : 500;
        this.runMode = type == Type.PORTAL && ((Portal) entity).target != null && !((Portal) entity).target.gg
                ? RunMode.ALWAYS : RunMode.NEVER;
        if (type == Type.PORTAL) jumpMode = JumpMode.ESCAPING;
        if (type == Type.CBS) cbsMode = CbsMode.ALLY;
        this.entity = entity;
    }

    @Override
    public boolean ignore() {
        return (x == 0 && y == 0) ||
                diameter == (type == Type.BASE ? 1500 : 500) &&
                (type != Type.PORTAL ? runMode == RunMode.NEVER :
                        (entity != null && ((Portal) entity).target != null
                                && runMode == (((Portal) entity).target.gg ? RunMode.NEVER : RunMode.ALWAYS))) &&
                jumpMode == (type == Type.PORTAL ? JumpMode.ESCAPING : null) &&
                cbsMode == (type == Type.CBS ? CbsMode.ALLY : null);
    }

    @Override
    public boolean writeAsNull() {
        return false;
    }

    // Running reasons this safety can be selected
    public enum RunMode {
        ALWAYS(I18n.get("gui.safety_places.run_mode.always")),
        ENEMY_FLEE_ONLY(I18n.get("gui.safety_places.run_mode.enemy_only")),
        REPAIR_ONLY(I18n.get("gui.safety_places.run_mode.repair_only")),
        REFRESH(I18n.get("gui.safety_places.run_mode.refresh")),
        NEVER(I18n.get("gui.safety_places.run_mode.never"));
        String text;
        RunMode(String text) {this.text = text;}
        public String toString() {return text;}
    }
    public RunMode runMode = RunMode.ALWAYS;

    // PORTAL
    // Condition to jump
    public enum JumpMode {
        NEVER(I18n.get("gui.safety_places.jump_mode.never")),
        ESCAPING(I18n.get("gui.safety_places.jump_mode.escaping")),
        FLEEING(I18n.get("gui.safety_places.jump_mode.fleeing")),
        REPAIRING(I18n.get("gui.safety_places.jump_mode.repairing")),
        ALWAYS(I18n.get("gui.safety_places.jump_mode.always")),
        ALWAYS_OTHER_SIDE(I18n.get("gui.safety_places.jump_mode.always_oth_side"));
        String text;
        JumpMode(String text) {this.text = text;}
        public String toString() {return text;}
    }
    public JumpMode jumpMode;

    // CBS
    // Condition to run to CBS
    public enum CbsMode {
        ALLY(I18n.get("gui.safety_places.cbs_mode.ally")),
        ALLY_NEUTRAL(I18n.get("gui.safety_places.cbs_mode.ally_neutral"));
        String text;
        CbsMode(String text) {this.text = text;}
        public String toString() {return text;}
    }
    public CbsMode cbsMode;

    public int radius() {
        return diameter / 2;
    }

    @Override
    public String toString() {
        String result = type.toString();
        if (type == Type.PORTAL && entity != null && ((Portal) entity).target != null)
            result += "(" + ((Portal) entity).target.name + ")";
        return result;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        SafetyInfo that = (SafetyInfo) o;
        return x == that.x &&
                y == that.y &&
                diameter == that.diameter &&
                type == that.type;
    }

    @Override
    public int hashCode() {
        return Objects.hash(type, x, y, diameter);
    }

}
