package com.github.manolo8.darkbot.view.draw.types;

import com.github.manolo8.darkbot.core.entities.Ship;
import com.github.manolo8.darkbot.core.manager.HeroManager;
import com.github.manolo8.darkbot.view.draw.Drawable;
import com.github.manolo8.darkbot.view.draw.GraphicDrawer;

import static com.github.manolo8.darkbot.view.draw.Palette.*;

public class HealthDrawer implements Drawable {

    private final HeroManager hero;

    public HealthDrawer(HeroManager hero) {this.hero = hero;}

    @Override
    public void draw(GraphicDrawer drawer) {

        drawer.setTranslate(false);

        drawer.set(10, drawer.height - 40);

        drawerHealth(drawer, hero, drawer.midX() - 20);

        if (hero.target != null && !hero.target.removed) {
            drawer.set(10 + drawer.midX(), drawer.height - 40);
            drawerHealth(drawer, hero.target, drawer.midX() - 20);
        }
    }

    private void drawerHealth(GraphicDrawer drawer, Ship ship, double width) {
        drawer.fillProgress(HEALTH_BACK, HEALTH, width, 15, ship.health.hpPercent());

        drawer.move(0, 15);

        drawer.fillProgress(SHIELD_BACK, SHIELD, width, 15, ship.health.shieldPercent());

        drawer.move(width / 2, 3);

        drawer.move(0, 15);

        drawer.setFont(FONT_SMALL);
        drawer.setColor(TEXT);
        drawer.drawStringCenter(formatter.format(ship.health.shield));

        drawer.move(0, -15);
        drawer.drawStringCenter(formatter.format(ship.health.hp));

        drawer.move(0, -10);
        drawer.setFont(FONT_MID);
        drawer.drawStringCenter(ship.playerInfo.username);
    }
}
