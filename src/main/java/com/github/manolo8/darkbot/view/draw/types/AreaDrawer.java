package com.github.manolo8.darkbot.view.draw.types;

import com.github.manolo8.darkbot.config.MapInfo;
import com.github.manolo8.darkbot.core.manager.HeroManager;
import com.github.manolo8.darkbot.view.draw.Drawable;
import com.github.manolo8.darkbot.view.draw.GraphicDrawer;
import com.github.manolo8.darkbot.view.draw.Palette;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.paint.Color;

public class AreaDrawer
        implements Drawable {

    private final HeroManager hero;
    private       boolean     clicked;
    private       boolean     hover;
    private       double      startX;
    private       double      startY;
    private       double      currentX;
    private       double      currentY;
    private       int         mode;

    private GraphicDrawer temp;

    public AreaDrawer(HeroManager hero) {
        this.hero = hero;
    }

    @Override
    public void draw(GraphicDrawer drawer) {

        if (!hover)
            return;

        MapInfo info = hero.map.mapInfo;

        temp = drawer;

        double x      = drawer.undoTranslateX(Math.min(startX, currentX));
        double y      = drawer.undoTranslateY(Math.min(startY, currentY));
        double width  = drawer.undoTranslateX(Math.max(startX, currentX) - Math.min(startX, currentX));
        double height = drawer.undoTranslateY(Math.max(startY, currentY) - Math.min(startY, currentY));

        boolean high = Math.sqrt(width * height) > 100 && clicked;

        info.foreach(tile -> {

            int type = (high && tile.intersect(x, y, width, height)) ? mode : tile.type();

            if (type != 0) {
                drawer.setColor(type == 1 ? Palette.COLOR_MOVE : Palette.COLOR_AVOID);
                drawer.set(tile.x, tile.y);
                drawer.fillRect(drawer.translateX(tile.width), drawer.translateY(tile.height));
            }
        });


        if (clicked && high) {
            drawer.setColor(Color.YELLOW);
            drawer.set(x, y);
            drawer.drawRect(drawer.translateX(width), drawer.translateY(height));
        }
    }

    public void mouseDragged(MouseEvent e) {
        currentX = e.getX();
        currentY = e.getY();
    }

    public void mousePressed(MouseEvent e) {
        clicked = true;
        startX = currentX = e.getX();
        startY = currentY = e.getY();

        if (e.getButton() == MouseButton.PRIMARY)
            mode = 1;
        else if (e.getButton() == MouseButton.MIDDLE)
            mode = 0;
        else
            mode = 2;
    }

    public void mouseReleased(MouseEvent e) {

        if (temp == null)
            return;

        double x      = temp.undoTranslateX(Math.min(startX, currentX));
        double y      = temp.undoTranslateY(Math.min(startY, currentY));
        double width  = temp.undoTranslateX(Math.max(startX, currentX) - Math.min(startX, currentX));
        double height = temp.undoTranslateY(Math.max(startY, currentY) - Math.min(startY, currentY));

        if (Math.sqrt(width * height) < 100)
            return;

        MapInfo info = hero.map.mapInfo;

        info.foreach(tile -> {
            if (tile.intersect(x, y, width, height))
                tile.setMode(mode);
        });

        clicked = false;
    }

    public void mouseEntered(MouseEvent e) {
        hover = true;
    }

    public void mouseExited(MouseEvent e) {
        hover = false;
    }
}
