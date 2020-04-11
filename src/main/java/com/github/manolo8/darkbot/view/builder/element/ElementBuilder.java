package com.github.manolo8.darkbot.view.builder.element;

import com.github.manolo8.darkbot.core.manager.Core;
import com.github.manolo8.darkbot.core.objects.PetGear;
import com.github.manolo8.darkbot.view.builder.element.component.ICharField;
import com.github.manolo8.darkbot.view.builder.element.component.ICheckBox;
import com.github.manolo8.darkbot.view.builder.element.component.ILabel;
import com.github.manolo8.darkbot.view.builder.element.component.IPetModules;
import javafx.collections.ObservableList;
import javafx.scene.Node;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class ElementBuilder {

    public static ElementBuilder INSTANCE;

    private final HashMap<Class<? extends Annotation>, Handler> handlers;
    private final Core                                          core;

    ElementBuilder(Core core) {
        this.core = core;
        this.handlers = new HashMap<>();

        fill();
    }

    public static void init(Core core) {
        INSTANCE = new ElementBuilder(core);
    }

    public Node[] create(Field field, Object object) {

        SafeField safeField = new SafeField(object, field);

        List<Node> nodes = new ArrayList<>();

        Annotation[] annotations = field.getDeclaredAnnotations();

        for (Annotation annotation : annotations) {

            Handler handler = handlers.get(annotation.annotationType());

            if (handler == null)
                continue;

            Node node = handler.create(annotation, safeField);

            nodes.add(node);
        }

        return nodes.toArray(new Node[0]);
    }

    private void fill() {
        registerAnnotationHandler(ILabel.class, this::createLabel);
        registerAnnotationHandler(ICheckBox.class, this::createCheckBox);
        registerAnnotationHandler(ICharField.class, this::createCharField);
        registerAnnotationHandler(IPetModules.class, this::createPetModulesCombo);
    }

    private <E extends Annotation> void registerAnnotationHandler(Class<E> clazz, Handler<E> handler) {
        this.handlers.put(clazz, handler);
    }

    private Node createLabel(ILabel annotation, SafeField field) {
        return new Label(annotation.value());
    }

    private Node createCheckBox(ICheckBox annotation, SafeField field) {

        CheckBox checkBox = new CheckBox();

        checkBox.setSelected(field.getBoolean());
        checkBox.selectedProperty().addListener((observable, oldValue, newValue) -> field.setBoolean(newValue));

        return checkBox;
    }

    private Node createCharField(ICharField annotation, SafeField field) {
        TextField textField = new TextField();

        textField.setText(String.valueOf(field.getChar()));

        textField.textProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue.length() > 1)
                textField.setText(String.valueOf(newValue.charAt(newValue.length() - 1)));
            else if (newValue.length() == 1)
                field.setChar(newValue.charAt(0));
            else
                field.setChar('\0');
        });

        return textField;
    }

    private Node createPetModulesCombo(IPetModules annotation, SafeField safeField) {

        ComboBox<PetGear> comboBox = new ComboBox<>();

        int gerId = safeField.getInt();

        comboBox.getItems().addAll(core.getPetManager().getAllGears());

        ObservableList<PetGear> list = comboBox.getItems();

        for (int i = 0, listSize = list.size(); i < listSize; i++) {
            PetGear gear = list.get(i);
            if (gear.id == gerId) {
                comboBox.getSelectionModel().select(i);
                break;
            }
        }

        comboBox.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> safeField.setInt(newValue.id));

        return comboBox;
    }

    private interface Handler<E extends Annotation> {
        Node create(E annotation, SafeField field);
    }

    private class SafeField {

        private final Object object;
        private final Field  field;

        public SafeField(Object object, Field field) {
            this.object = object;
            this.field = field;

            this.field.setAccessible(true);
        }

        public void setBoolean(boolean value) {
            try {
                field.setBoolean(object, value);
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            }
        }

        public void setInt(int value) {
            try {
                field.setInt(object, value);
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            }
        }

        public void setChar(char value) {
            try {
                field.setChar(object, value);
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            }
        }

        public boolean getBoolean() {
            try {
                return field.getBoolean(object);
            } catch (IllegalAccessException e) {
                e.printStackTrace();
                return false;
            }
        }

        public char getChar() {
            try {
                return field.getChar(object);
            } catch (IllegalAccessException e) {
                e.printStackTrace();
                return '\0';
            }
        }

        public int getInt() {
            try {
                return field.getInt(object);
            } catch (IllegalAccessException e) {
                e.printStackTrace();
                return 0;
            }
        }
    }
}
