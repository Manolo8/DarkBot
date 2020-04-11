package com.github.manolo8.darkbot.view.draw;

public interface Drawable {

    default void update() {
    }

    void draw(GraphicDrawer drawer);

    default void redraw(GraphicDrawer drawer) {
        drawer.setTranslate(true);
        draw(drawer);
    }
}
