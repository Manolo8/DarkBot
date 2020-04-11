package com.github.manolo8.darkbot.view.column;

import javafx.beans.property.SimpleDoubleProperty;
import javafx.scene.control.TableColumn;
import javafx.scene.control.cell.TextFieldTableCell;
import javafx.util.converter.DoubleStringConverter;

;

public class DoubleTableColumn<E>
        extends TableColumn<E, Double> {

    public DoubleTableColumn(String text, int width, Sync.Get<Double, E> get) {
        super(text);

        setMinWidth(width);
        setCellValueFactory(param -> new SimpleDoubleProperty(get.value(param.getValue())).asObject());
    }

    public DoubleTableColumn(String text, int width, Sync.Get<Double, E> get, Sync.Set<Double, E> set) {
        this(text, width, get);

        setEditable(true);
        setCellFactory(param -> new TextFieldTableCell<>(new DoubleStringConverter()));
        setOnEditCommit(event -> set.value(event.getRowValue(), event.getNewValue()));
    }
}
