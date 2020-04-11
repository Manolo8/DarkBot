package com.github.manolo8.darkbot;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.SceneAntialiasing;
import javafx.scene.image.Image;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

import java.io.File;
import java.io.IOException;

public class DarkBotApp extends Application {

    public final static Object UPDATE_LOCKER = new Object();

    private Stage stage;
    private Scene scene;

    @Override
    public void start(Stage startStage)
            throws IOException {

        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getClassLoader().getResource("AppView.fxml"));

        Parent root = fxmlLoader.load();

        stage = startStage;
        scene = new Scene(root, 682, 453, false, SceneAntialiasing.BALANCED);

        setDefaults();

        scene.getStylesheets().add(getClass().getResource("/style.css").toExternalForm());
        stage.initStyle(StageStyle.TRANSPARENT);
        stage.show();

    }

    private void setDefaults() {
        stage.getIcons().add(new Image(getClass().getClassLoader().getResourceAsStream("tardis.png")));
        stage.setTitle("DarkBot - By Manolo8");
        stage.setScene(scene);
        stage.setResizable(false);
    }

}
