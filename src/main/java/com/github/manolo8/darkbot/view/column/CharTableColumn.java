package com.github.manolo8.darkbot.view.column;

import javafx.scene.control.TableColumn;
import javafx.scene.control.cell.TextFieldTableCell;
import javafx.util.converter.CharacterStringConverter;

public class CharTableColumn<E>
        extends TableColumn<E, Character> {

    public CharTableColumn(String text, int width, Sync.Get<Character, E> get) {
        super(text);

        setMinWidth(width);
        setCellValueFactory(param -> new SimpleCharacterProperty(get.value(param.getValue())));
    }

    public CharTableColumn(String text, int width, Sync.Get<Character, E> get, Sync.Set<Character, E> set) {
        this(text, width, get);

        setEditable(true);
        setCellFactory(param -> new TextFieldTableCell<>(new CharacterStringConverter()));
        setOnEditCommit(event -> set.value(event.getRowValue(), event.getNewValue() == null ? '\0' : event.getNewValue()));
    }
}
