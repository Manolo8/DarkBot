package com.github.manolo8.darkbot.view.draw;

import com.github.manolo8.darkbot.DarkBotApp;
import com.github.manolo8.darkbot.core.manager.Core;
import com.github.manolo8.darkbot.core.manager.EntityManager;
import com.github.manolo8.darkbot.view.draw.types.*;
import javafx.scene.canvas.Canvas;

import java.util.ArrayList;
import java.util.List;

public class MapDrawer {

    private final GraphicDrawer drawer;

    private final List<Drawable> drawableList;

    public MapDrawer(Core core, Canvas canvas) {

        drawableList = new ArrayList<>();
        drawer = new GraphicDrawer(core.getMapManager(), canvas.getGraphicsContext2D());

        canvas.widthProperty().addListener((observable, oldValue, newValue) -> drawer.setWidth(newValue));
        canvas.heightProperty().addListener((observable, oldValue, newValue) -> drawer.setHeight(newValue));

        drawer.setWidth(canvas.getWidth());
        drawer.setHeight(canvas.getHeight());

        registerDrawers(core, canvas);
    }

    private void registerDrawers(Core core, Canvas canvas) {

        AreaDrawer areaDrawer = new AreaDrawer(core.getHeroManager());

        canvas.setOnMousePressed(areaDrawer::mousePressed);
        canvas.setOnMouseReleased(areaDrawer::mouseReleased);
        canvas.setOnMouseDragged(areaDrawer::mouseDragged);
        canvas.setOnMouseEntered(areaDrawer::mouseEntered);
        canvas.setOnMouseExited(areaDrawer::mouseExited);

        EntityManager entityManager = core.getEntityManager();

        this.drawableList.add(areaDrawer);
        this.drawableList.add(new ListDrawer(entityManager.barriers));

        this.drawableList.add(new TrailDrawer(core.getHeroManager()));
        this.drawableList.add(
                new InfoDrawer(
                        core.getHeroManager(),
                        core.getPetManager(),
                        core.getPingManager(),
                        core.getStatsManager(),
                        core.getModuleManager(),
                        core.getCommonConfig(),
                        core.getInstaller()
                )
        );
        this.drawableList.add(core.getHeroManager());
        this.drawableList.add(core.getHeroManager().pet);
        this.drawableList.add(new HealthDrawer(core.getHeroManager()));


        this.drawableList.add(new ListDrawer(entityManager.npcs));
        this.drawableList.add(new ListDrawer(entityManager.ships));
        this.drawableList.add(new ListDrawer(entityManager.boxes));
        this.drawableList.add(new ListDrawer(entityManager.palladiumAreas));
        this.drawableList.add(new ListDrawer(entityManager.portals));
        this.drawableList.add(new ListDrawer(entityManager.battleStations));
        this.drawableList.add(new ListDrawer(entityManager.basePieces));
        this.drawableList.add(new ListDrawer(entityManager.unknown));
    }

    public void update(boolean paint) {

        drawer.reset();

        synchronized (DarkBotApp.UPDATE_LOCKER) {
            for (Drawable drawable : drawableList) {
                drawable.update();
                if (paint)
                    drawable.redraw(drawer);
            }
        }
    }
}
