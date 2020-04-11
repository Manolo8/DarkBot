package com.github.manolo8.darkbot.view.draw.types;

import com.github.manolo8.darkbot.core.entities.Entity;
import com.github.manolo8.darkbot.core.objects.Location;
import com.github.manolo8.darkbot.view.draw.Drawable;
import com.github.manolo8.darkbot.view.draw.GraphicDrawer;
import javafx.scene.paint.Color;

import static java.lang.Math.pow;
import static java.lang.Math.sqrt;

public class TrailDrawer
        implements Drawable {

    private final Color  color = new Color(0.73046875, 0.73046875, 0.73046875, 1);
    private final Entity entity;
    private final int[]  last;
    private       int    current;
    private       int    index;

    public TrailDrawer(Entity entity) {
        this.entity = entity;
        this.last = new int[512];
    }

    @Override
    public void update() {

        index = (current % 256) * 2;

        nextTrail();
    }

    @Override
    public void draw(GraphicDrawer drawer) {
        drawTrail(drawer);
    }

    private void nextTrail() {

        int index = this.index;

        Location location = entity.location;

        if (index > 2) {
            int x = last[index - 2];
            int y = last[index - 1];

            if (location.distance(x, y) < 50)
                return;
        }

        last[index++] = (int) location.x;
        last[index] = (int) location.y;

        current++;
    }

    private void drawTrail(GraphicDrawer drawer) {

        Location location = entity.location;

        drawer.set((int) location.x, (int) location.y);

        int lastX = (int) location.x;
        int lastY = (int) location.y;

        for (int i = index - 2; i >= 1; ) {
            drawer.setColor(color);

            int x = last[i--];
            int y = last[i--];

            if (sqrt(pow(x - lastX, 2) + pow(y - lastY, 2)) < 200)
                drawer.drawLineTo(x, y);
            else
                drawer.set(x, y);

            lastX = x;
            lastY = y;
        }

        for (int i = 510; i > index + 2; ) {
            drawer.setColor(color);

            int x = last[i--];
            int y = last[i--];

            if (sqrt(pow(x - lastX, 2) + pow(y - lastY, 2)) < 200)
                drawer.drawLineTo(x, y);
            else
                drawer.set(x, y);

            lastX = x;
            lastY = y;
        }
    }
}
