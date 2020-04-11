package com.github.manolo8.darkbot.view.column;

import javafx.beans.property.SimpleIntegerProperty;
import javafx.scene.control.TableColumn;
import javafx.scene.control.cell.TextFieldTableCell;
import javafx.util.converter.IntegerStringConverter;

public class IntegerTableColumn<E>
        extends TableColumn<E, Integer> {

    public IntegerTableColumn(String text, int width, Sync.Get<Integer, E> get) {
        super(text);

        setMinWidth(width);
        setCellValueFactory(param -> new SimpleIntegerProperty(get.value(param.getValue())).asObject());
    }

    public IntegerTableColumn(String text, int width, Sync.Get<Integer, E> get, Sync.Set<Integer, E> set) {
        this(text, width, get);

        setEditable(true);
        setCellFactory(param -> new TextFieldTableCell<>(new IntegerStringConverter()));
        setOnEditCommit(event -> set.value(event.getRowValue(), event.getNewValue()));
    }
}
