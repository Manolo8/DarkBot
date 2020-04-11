package com.github.manolo8.darkbot.view.builder;

import com.github.manolo8.darkbot.core.utils.module.ModuleConfig;
import com.github.manolo8.darkbot.view.builder.element.ElementBuilder;
import javafx.geometry.HPos;
import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.control.Control;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Priority;

import java.lang.reflect.Field;

public class ConfigViewBuilder {

    private final Class        clazz;
    private final ModuleConfig config;

    public ConfigViewBuilder(ModuleConfig config) {
        this.clazz = config.getClass();
        this.config = config;
    }

    public Node build() {

        ElementBuilder builder = ElementBuilder.INSTANCE;

        Field[] fields = clazz.getDeclaredFields();

        GridPane gridPane = new GridPane();

        gridPane.getColumnConstraints().addAll(
                new ColumnConstraints(Control.USE_COMPUTED_SIZE, 160, Control.USE_COMPUTED_SIZE, Priority.NEVER, HPos.RIGHT, true),
                new ColumnConstraints(Control.USE_COMPUTED_SIZE, Control.USE_COMPUTED_SIZE, Control.USE_COMPUTED_SIZE, Priority.ALWAYS, HPos.LEFT, true)
        );

        for (int i = 0; i < fields.length; i++) {
            Field field = fields[i];

            Node[] nodes = builder.create(field, config);

            if (nodes.length == 2)
                GridPane.setMargin(nodes[1], new Insets(15, 15, 15, 15));

            gridPane.addRow(i, nodes);
        }

        return gridPane;
    }
}
