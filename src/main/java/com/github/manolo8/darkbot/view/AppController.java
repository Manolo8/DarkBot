package com.github.manolo8.darkbot.view;

import com.github.manolo8.darkbot.backpage.auth.AuthenticationResult;
import com.github.manolo8.darkbot.core.manager.Core;
import javafx.collections.ObservableList;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.shape.SVGPath;

import java.awt.*;
import java.awt.datatransfer.StringSelection;
import java.io.IOException;

import static com.github.manolo8.darkbot.core.manager.Core.API;

public class AppController {

    private static String WRENCH  = "M 15.867188 3.410156 C 15.796875 3.128906 15.445312 3.03125 15.238281 3.238281 L 12.914062 5.5625 L 10.792969 5.207031 L 10.4375 3.085938 L 12.761719 0.761719 C 12.96875 0.554688 12.871094 0.203125 12.585938 0.132812 C 11.105469 -0.234375 9.476562 0.160156 8.316406 1.316406 C 7.078125 2.554688 6.738281 4.351562 7.253906 5.917969 L 0.585938 12.585938 C -0.195312 13.367188 -0.195312 14.632812 0.585938 15.414062 C 1.367188 16.195312 2.632812 16.195312 3.414062 15.414062 L 10.078125 8.75 C 11.644531 9.273438 13.433594 8.929688 14.683594 7.683594 C 15.839844 6.523438 16.234375 4.890625 15.867188 3.410156 Z M 2 14.75 C 1.585938 14.75 1.25 14.414062 1.25 14 C 1.25 13.585938 1.585938 13.25 2 13.25 C 2.414062 13.25 2.75 13.585938 2.75 14 C 2.75 14.414062 2.414062 14.75 2 14.75 Z M 2 14.75";
    private static String PLAY    = "M 13.261719 6.710938 L 2.261719 0.207031 C 1.367188 -0.320312 0 0.191406 0 1.496094 L 0 14.5 C 0 15.671875 1.273438 16.378906 2.261719 15.789062 L 13.261719 9.289062 C 14.242188 8.710938 14.246094 7.289062 13.261719 6.710938 Z M 13.261719 6.710938";
    private static String PAUSE   = "M 4.5 14.96875 L 1.5 14.96875 C 0.671875 14.96875 0 14.296875 0 13.46875 L 0 2.46875 C 0 1.640625 0.671875 0.96875 1.5 0.96875 L 4.5 0.96875 C 5.328125 0.96875 6 1.640625 6 2.46875 L 6 13.46875 C 6 14.296875 5.328125 14.96875 4.5 14.96875 Z M 14 13.46875 L 14 2.46875 C 14 1.640625 13.328125 0.96875 12.5 0.96875 L 9.5 0.96875 C 8.671875 0.96875 8 1.640625 8 2.46875 L 8 13.46875 C 8 14.296875 8.671875 14.96875 9.5 14.96875 L 12.5 14.96875 C 13.328125 14.96875 14 14.296875 14 13.46875 Z M 14 13.46875";
    private static String COPY    = "M 10 14 L 10 15.25 C 10 15.664062 9.664062 16 9.25 16 L 0.75 16 C 0.335938 16 0 15.664062 0 15.25 L 0 3.75 C 0 3.335938 0.335938 3 0.75 3 L 3 3 L 3 12.25 C 3 13.214844 3.785156 14 4.75 14 Z M 10 3.25 L 10 0 L 4.75 0 C 4.335938 0 4 0.335938 4 0.75 L 4 12.25 C 4 12.664062 4.335938 13 4.75 13 L 13.25 13 C 13.664062 13 14 12.664062 14 12.25 L 14 4 L 10.75 4 C 10.335938 4 10 3.664062 10 3.25 Z M 13.78125 2.28125 L 11.71875 0.21875 C 11.578125 0.078125 11.386719 0 11.1875 0 L 11 0 L 11 3 L 14 3 L 14 2.8125 C 14 2.613281 13.921875 2.421875 13.78125 2.28125 Z M 13.78125 2.28125";
    private static String WINDOW  = "M 16.945312 1 C 15.230469 1.097656 11.828125 1.453125 9.726562 2.738281 C 9.582031 2.828125 9.5 2.984375 9.5 3.148438 L 9.5 14.519531 C 9.5 14.882812 9.894531 15.109375 10.226562 14.941406 C 12.390625 13.855469 15.515625 13.558594 17.0625 13.476562 C 17.589844 13.449219 18 13.027344 18 12.519531 L 18 1.960938 C 18 1.40625 17.519531 0.96875 16.945312 1 Z M 8.273438 2.738281 C 6.171875 1.453125 2.769531 1.097656 1.054688 1 C 0.480469 0.96875 0 1.40625 0 1.960938 L 0 12.519531 C 0 13.027344 0.410156 13.449219 0.9375 13.476562 C 2.484375 13.558594 5.613281 13.855469 7.773438 14.945312 C 8.105469 15.109375 8.5 14.882812 8.5 14.523438 L 8.5 3.144531 C 8.5 2.980469 8.417969 2.828125 8.273438 2.738281 Z M 8.273438 2.738281";
    private static String REFRESH = "M 11.585938 4.164062 C 10.609375 3.25 9.339844 2.75 7.996094 2.75 C 5.574219 2.753906 3.484375 4.410156 2.90625 6.714844 C 2.867188 6.882812 2.714844 7 2.542969 7 L 0.753906 7 C 0.519531 7 0.339844 6.789062 0.382812 6.558594 C 1.058594 2.964844 4.214844 0.25 8 0.25 C 10.078125 0.25 11.960938 1.066406 13.355469 2.394531 L 14.46875 1.28125 C 14.941406 0.808594 15.75 1.140625 15.75 1.8125 L 15.75 6 C 15.75 6.414062 15.414062 6.75 15 6.75 L 10.8125 6.75 C 10.140625 6.75 9.808594 5.941406 10.28125 5.46875 Z M 1 9.25 L 5.1875 9.25 C 5.859375 9.25 6.191406 10.058594 5.71875 10.53125 L 4.414062 11.835938 C 5.390625 12.75 6.660156 13.25 8.003906 13.25 C 10.425781 13.246094 12.515625 11.589844 13.09375 9.285156 C 13.132812 9.117188 13.285156 9 13.457031 9 L 15.246094 9 C 15.480469 9 15.660156 9.210938 15.617188 9.441406 C 14.941406 13.035156 11.785156 15.75 8 15.75 C 5.921875 15.75 4.039062 14.933594 2.644531 13.605469 L 1.53125 14.71875 C 1.058594 15.191406 0.25 14.859375 0.25 14.1875 L 0.25 10 C 0.25 9.585938 0.585938 9.25 1 9.25 Z M 1 9.25";
    private static String BACK    = "M 1.078125 7.46875 L 7.152344 1.398438 C 7.445312 1.105469 7.921875 1.105469 8.210938 1.398438 L 8.921875 2.105469 C 9.214844 2.398438 9.214844 2.871094 8.921875 3.164062 L 4.109375 8 L 8.921875 12.835938 C 9.214844 13.128906 9.214844 13.601562 8.921875 13.894531 L 8.210938 14.605469 C 7.917969 14.898438 7.445312 14.898438 7.152344 14.605469 L 1.078125 8.53125 C 0.785156 8.238281 0.785156 7.761719 1.078125 7.46875 Z M 1.078125 7.46875";

    @FXML
    private BorderPane       pane;
    @FXML
    private HeaderController headerController;

    private boolean visible;

    private Node mapView;
    private Node configView;

    private Core core;

    public void initialize()
            throws IOException {

        core = new Core();

        headerController.init();
        headerController.setBackNode(createBackNode());
        headerController.setControlNode(createControlNode());

        FXMLLoader fxmlLoader;

        fxmlLoader = new FXMLLoader(getClass().getClassLoader().getResource("LoginView.fxml"));
        Node            loginView       = fxmlLoader.load();
        LoginController loginController = fxmlLoader.getController();

        fxmlLoader = new FXMLLoader(getClass().getClassLoader().getResource("MapView.fxml"));
        mapView = fxmlLoader.load();
        MapController mapController = fxmlLoader.getController();

        fxmlLoader = new FXMLLoader(getClass().getClassLoader().getResource("ConfigView.fxml"));
        configView = fxmlLoader.load();
        ConfigController configController = fxmlLoader.getController();

        loginController.initialize(this::auth);

        mapController.init(core);
        configController.init(core);

        pane.setCenter(loginView);
    }

    private void setMapView() {
        pane.setCenter(mapView);
        headerController.setControl();
    }

    private void setConfigView() {
        pane.setCenter(configView);
        headerController.setBack();
    }

    private HBox createControlNode() {
        HBox node = new HBox();

        node.setPrefWidth(150);

        ObservableList<Node> children = node.getChildren();

        children.add(createIcon(PLAY, PAUSE, this::toggleBot));
        children.add(createIcon(WRENCH, null, this::openConfig));
        children.add(createIcon(WINDOW, null, this::show));
        children.add(createIcon(COPY, null, this::copySid));
        children.add(createIcon(REFRESH, null, this::reload));

        return node;
    }

    private void toggleBot(MouseEvent event) {
        boolean running = core.isRunning();

        core.setRunning(!running);
    }

    private void openConfig(MouseEvent event) {
        setConfigView();
    }

    private void show(MouseEvent event) {
        API.setVisible(visible = !visible);
    }

    private void copySid(MouseEvent event) {
        String value = core.getAuthenticationResult().sid;

        if (value != null) {
            StringSelection selection = new StringSelection(value);
            Toolkit.getDefaultToolkit().getSystemClipboard().setContents(selection, selection);
        }
    }

    private void reload(MouseEvent event) {
        API.reload();
    }

    private Node createBackNode() {
        HBox node = new HBox();

        node.setPrefWidth(150);

        ObservableList<Node> children = node.getChildren();

        children.add(createIcon(BACK, null, event -> setMapView()));

        return node;
    }

    private Node createIcon(String icon, String click, EventHandler<MouseEvent> clickHandler) {
        SVGPath iconView = new SVGPath();

        iconView.getStyleClass().add("svg");

        iconView.setContent(icon);
        iconView.setPickOnBounds(true);

        if (click == null)
            iconView.setOnMouseClicked(clickHandler);
        else
            iconView.setOnMouseClicked(event -> {

                iconView.setContent(iconView.getContent().equals(icon) ? click : icon);

                clickHandler.handle(event);
            });

        HBox.setMargin(iconView, new Insets(5, 5, 5, 5));

        return iconView;
    }

    private void auth(AuthenticationResult authenticationResult) {
        core.auth(authenticationResult);
        setMapView();
    }
}
