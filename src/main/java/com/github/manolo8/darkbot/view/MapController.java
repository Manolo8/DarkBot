package com.github.manolo8.darkbot.view;

import com.github.manolo8.darkbot.core.manager.Core;
import com.github.manolo8.darkbot.view.draw.MapDrawer;
import javafx.animation.AnimationTimer;
import javafx.beans.Observable;
import javafx.fxml.FXML;
import javafx.scene.canvas.Canvas;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;

public class MapController {

    @FXML
    private Pane      pane;
    @FXML
    private Canvas    canvas;
    private MapDrawer drawer;

    private boolean rendering;

    void init(Core core) {
        canvas.widthProperty().bind(pane.widthProperty());
        canvas.heightProperty().bind(pane.heightProperty());

        drawer = new MapDrawer(core, canvas);

        drawer.update(true);

        pane.sceneProperty().addListener((observable, oldValue, newValue) -> {

            if (oldValue != null) {
                Stage stage = (Stage) oldValue.getWindow();
                stage.iconifiedProperty().removeListener(this::minifiedListener);
            }

            if (newValue == null)
                rendering = false;
            else {
                Stage stage = (Stage) newValue.getWindow();
                stage.iconifiedProperty().addListener(this::minifiedListener);
                rendering = !stage.isIconified();
            }
        });

        startAnimationTimer();
    }

    private void minifiedListener(Observable observable, boolean oldValue, boolean newValue) {
        rendering = !newValue;
    }

    private void startAnimationTimer() {

        AnimationTimer timer = new AnimationTimer() {

            @Override
            public void handle(long now) {

                if(System.currentTimeMillis() % 3 == 1)
                drawer.update(rendering);

            }
        };


        timer.start();
    }
}
