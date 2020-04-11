package com.github.manolo8.darkbot.view;

import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.BorderPane;
import javafx.scene.shape.SVGPath;
import javafx.stage.Stage;

public class HeaderController {

    @FXML
    private BorderPane header;

    @FXML
    private SVGPath btnHide;
    @FXML
    private SVGPath btnClose;

    private Node nodeToBack;
    private Node nodeToControl;

    private double x, y;

    void init() {
        header.setOnMouseDragged(this::onHeaderDragged);
        header.setOnMousePressed(this::onHeaderPressed);

        btnHide.setOnMouseClicked(this::hide);
        btnClose.setOnMouseClicked(this::close);
    }

    void setBack() {
        header.setLeft(nodeToBack);
    }

    void setControl() {
        header.setLeft(nodeToControl);
    }

    public void setBackNode(Node backNode) {
        this.nodeToBack = backNode;
    }

    public void setControlNode(Node controlNode) {
        this.nodeToControl = controlNode;
    }

    private void onHeaderDragged(MouseEvent event) {

        if (event.getButton() != MouseButton.PRIMARY)
            return;

        Stage stage = (Stage) (((Node) event.getSource()).getScene()).getWindow();

        stage.setX(event.getScreenX() - x);
        stage.setY(event.getScreenY() - y);
    }

    private void onHeaderPressed(MouseEvent event) {

        if (event.getButton() != MouseButton.PRIMARY)
            return;

        x = event.getX();
        y = event.getY();
    }

    private void hide(MouseEvent event) {

        if (event.getButton() != MouseButton.PRIMARY)
            return;

        Stage stage = (Stage) (((Node) event.getSource()).getScene()).getWindow();
        stage.setIconified(true);
    }

    private void close(MouseEvent event) {

        if (event.getButton() != MouseButton.PRIMARY)
            return;

        Stage stage = (Stage) (((Node) event.getSource()).getScene()).getWindow();
        stage.close();
        System.exit(0);
    }
}