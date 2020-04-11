package com.github.manolo8.darkbot.view;

import com.github.manolo8.darkbot.backpage.auth.AuthenticationException;
import com.github.manolo8.darkbot.backpage.auth.AuthenticationResult;
import com.github.manolo8.darkbot.backpage.auth.Authenticator;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseEvent;

import java.util.function.Consumer;

public class LoginController {


    @FXML
    private TextField fieldUsername;
    @FXML
    private TextField fieldPassword;
    @FXML
    private Label     messageCredentials;
    @FXML
    private Button    btnCredentialsLogin;

    @FXML
    private TextField fieldServer;
    @FXML
    private TextField fieldSession;
    @FXML
    private Label     messageSession;
    @FXML
    private Button    btnSessionLogin;

    private Consumer<AuthenticationResult> onSuccess;
    private Authenticator                  authenticator;

    void initialize(Consumer<AuthenticationResult> onSuccess) {
        this.onSuccess = onSuccess;
        this.authenticator = new Authenticator();

        btnCredentialsLogin.setOnMouseClicked(this::credentialsLogin);
        btnSessionLogin.setOnMouseClicked(this::sessionLogin);
    }

    private void credentialsLogin(MouseEvent event) {
        authenticator.setCredentials(fieldUsername.getText(), fieldPassword.getText());
        authenticate();
    }

    private void sessionLogin(MouseEvent event) {
        authenticator.setSession(fieldSession.getText(), fieldServer.getText());
        authenticate();
    }

    private void authenticate() {

        setMessage("Logging-in");

        btnCredentialsLogin.setDisable(true);
        btnSessionLogin.setDisable(true);

        createAuthThread();
    }

    private void successCallback(AuthenticationResult result) {
        setMessage("Successfully logged in!");
        onSuccess.accept(result);
    }

    private void errorCallback(AuthenticationException exception) {
        setMessage(exception.getMessage());

        btnCredentialsLogin.setDisable(false);
        btnSessionLogin.setDisable(false);
    }

    private void createAuthThread() {
        Thread thread = new Thread(() -> {
            try {
                AuthenticationResult result = authenticator.authenticate();
                Platform.runLater(() -> successCallback(result));
            } catch (AuthenticationException e) {
                Platform.runLater(() -> errorCallback(e));
            }
        });

        thread.start();
    }

    private void setMessage(String message) {
        this.messageCredentials.setText(message);
        this.messageSession.setText(message);
    }
}
