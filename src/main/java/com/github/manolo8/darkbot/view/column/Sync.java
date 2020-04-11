package com.github.manolo8.darkbot.view.column;

import javafx.scene.control.ComboBox;
import javafx.scene.control.Slider;
import javafx.scene.control.TextField;

public class Sync<O> {

    private final O object;

    public Sync(O object) {
        this.object = object;
    }

    public void controlString(TextField field, Get<String, O> get, Set<String, O> set) {

        field.setText(get.value(object));

        field.textProperty().addListener((observable, oldValue, newValue) -> set.value(object, newValue));
    }

    public void controlIntegerByIndex(Slider slider, Get<Integer, O> get, Set<Integer, O> set) {

        slider.setValue(get.value(object));

        slider.valueProperty().addListener((observable, oldValue, newValue) -> set.value(object, (int) ((double) newValue)));

    }

    public void controlInteger(ComboBox<Integer> comboBox, Get<Integer, O> get, Set<Integer, O> set) {
        comboBox.getSelectionModel().select(get.value(object));
        comboBox.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> set.value(object, newValue));
    }

    public void controlIntegerByIndex(ComboBox<?> comboBox, Get<Integer, O> get, Set<Integer, O> set) {
        comboBox.getSelectionModel().select(get.value(object));
        comboBox.getSelectionModel().selectedIndexProperty().addListener((observable, oldValue, newValue) -> set.value(object, (int) newValue));
    }

    public void controlString(ComboBox<String> comboBox, Get<String, O> get, Set<String, O> set) {
        comboBox.getSelectionModel().select(get.value(object));
        comboBox.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> set.value(object, newValue));
    }

    public void controlChar(TextField textField, Get<Character, O> get, Set<Character, O> set) {
        textField.setText(String.valueOf(get.value(object)));

        textField.textProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue.length() > 1)
                textField.setText(String.valueOf(newValue.charAt(newValue.length() - 1)));
            else if (newValue.length() == 1)
                set.value(object, newValue.charAt(0));
            else
                set.value(object, '\0');
        });
    }

    public interface Set<E, O> {
        void value(O object, E value);
    }

    public interface Get<E, O> {
        E value(O object);
    }
}
