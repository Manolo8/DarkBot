package com.github.manolo8.darkbot.view.draw.types;

import com.github.manolo8.darkbot.view.draw.Drawable;
import com.github.manolo8.darkbot.view.draw.GraphicDrawer;

import java.util.Collection;
import java.util.List;

public class ListDrawer
        implements Drawable {

    private final Collection<? extends Drawable> drawables;

    public ListDrawer(Collection<? extends Drawable> drawables) {
        this.drawables = drawables;
    }

    @Override
    public void draw(GraphicDrawer drawer) {
        for (Drawable drawable : drawables)
            drawable.redraw(drawer);
    }
}
