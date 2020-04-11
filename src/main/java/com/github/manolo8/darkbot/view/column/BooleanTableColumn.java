package com.github.manolo8.darkbot.view.column;

import javafx.beans.property.SimpleBooleanProperty;
import javafx.scene.control.CheckBox;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableRow;

public class BooleanTableColumn<E>
        extends TableColumn<E, Boolean> {

    private Sync.Set<Boolean, E> set;

    public BooleanTableColumn(String text, int width, Sync.Get<Boolean, E> get) {
        super(text);

        setMinWidth(width);


        setCellFactory(param -> new FuckJava<>());
        setCellValueFactory(param -> new SimpleBooleanProperty(get.value(param.getValue())));
    }

    public BooleanTableColumn(String text, int width, Sync.Get<Boolean, E> get, Sync.Set<Boolean, E> set) {
        this(text, width, get);

        setEditable(true);

        this.set = set;
        setOnEditCommit(event -> set.value(event.getRowValue(), event.getNewValue()));
    }

    private static class FuckJava<E> extends TableCell<E, Boolean> {

        private final CheckBox checkBox;

        public FuckJava() {
            checkBox = new CheckBox();

            checkBox.selectedProperty().addListener((observable, oldValue, newValue) -> {

                Sync.Set set = ((BooleanTableColumn) getTableColumn()).set;

                if (set != null) {

                    TableRow row = getTableRow();

                    if (row != null) {

                        Object object = getTableView().getItems().get(row.getIndex());

                        if (object != null)
                            //noinspection unchecked
                            set.value(object, newValue);
                    }

                }
            });
        }

        @Override
        protected void updateItem(Boolean item, boolean empty) {
            this.getStyleClass().add("check-box-table-cell");

            if (empty) {
                setGraphic(null);
            } else {
                checkBox.setSelected(item);
                setGraphic(checkBox);
            }
        }
    }

}
