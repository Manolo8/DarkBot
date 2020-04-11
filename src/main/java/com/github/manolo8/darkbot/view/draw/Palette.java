package com.github.manolo8.darkbot.view.draw;

import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;
import javafx.scene.text.Font;

import java.text.DecimalFormat;

public class Palette {
    //0.003921
    public static final Paint BACKGROUND     = new Color(0.148998, 0.19605, 0.219576, 1);
    public static final Paint TEXT           = new Color(0.948882, 0.948882, 0.948882, 1);
    public static final Paint TEXT_DARK      = new Color(0.439152, 0.439152, 0.439152, 1);
    public static final Paint GOING          = new Color(0.560703, 0.607755, 0.999855, 1);
    public static final Paint PORTALS        = new Color(0.682254, 0.682254, 0.682254, 1);
    public static final Paint OWNER          = new Color(0.999855, 0.999855, 0.999855, 1);
    public static final Paint BOXES          = new Color(0.7802790000000001, 0.47052, 0.0, 1);
    public static final Paint ALLIES         = new Color(0.160761, 0.713622, 0.964566, 1);
    public static final Paint ENEMIES        = new Color(0.835173, 0.0, 0.0, 1);
    public static final Paint NPCS           = new Color(0.607755, 0.0, 0.0, 1);
    public static final Paint PET            = new Color(0.0, 0.297996, 0.54894, 1);
    public static final Paint PET_IN         = new Color(0.772437, 0.376416, 0.0, 1);
    public static final Paint PALLADIUM_AREA = new Color(0.054894, 0.301917, 0.572466, 0.5);
    public static final Paint HEALTH         = new Color(0.219576, 0.556782, 0.23526, 0.5);
    public static final Paint HEALTH_BACK    = new Color(0.145077, 0.364653, 0.152919, 0.5);
    public static final Paint SHIELD         = new Color(0.007842, 0.533256, 0.819489, 0.5);
    public static final Paint SHIELD_BACK    = new Color(0.003921, 0.360732, 0.556782, 0.5);
    public static final Paint COLOR_MOVE     = new Color(0, 0.30859375, 0.03515625, 0.5);
    public static final Paint COLOR_AVOID    = new Color(0.3359375, 0, 0.02734375, 0.5);
    public static final Paint BARRIER        = new Color(0.6627451f, 0.6627451f, 0.6627451f, 0.2);

    public static final Font FONT_BIG   = new Font("Consolas", 32);
    public static final Font FONT_MID   = new Font("SANS_SERIF", 18);
    public static final Font FONT_SMALL = new Font("Consolas", 12);

    public static final DecimalFormat formatter = new DecimalFormat("###,###,###");

}
