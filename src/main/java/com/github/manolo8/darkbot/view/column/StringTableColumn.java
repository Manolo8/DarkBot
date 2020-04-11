package com.github.manolo8.darkbot.view.column;

import javafx.beans.property.SimpleStringProperty;
import javafx.scene.control.TableColumn;
import javafx.scene.control.cell.TextFieldTableCell;
import javafx.util.converter.DefaultStringConverter;

public class StringTableColumn<E>
        extends TableColumn<E, String> {

    public StringTableColumn(String text, int width, Sync.Get<String, E> get) {
        super(text);

        setCellValueFactory(param -> new SimpleStringProperty(get.value(param.getValue())));

        setMinWidth(width);
    }

    public StringTableColumn(String text, int width, Sync.Get<String, E> get, Sync.Set<String, E> set) {
        this(text, width, get);

        setEditable(true);
        setCellFactory(param -> new TextFieldTableCell<>(new DefaultStringConverter()));
        setOnEditCommit(event -> set.value(event.getRowValue(), event.getNewValue()));
    }
}
