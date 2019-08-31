package com.github.manolo8.darkbot.config.types.suppliers;

import javax.swing.*;
import javax.swing.event.EventListenerList;
import javax.swing.event.ListDataListener;
import java.util.List;

public abstract class OptionList<T> implements ComboBoxModel<String> {
    EventListenerList dataListeners = new EventListenerList();

    public abstract T getValue(String text);
    public abstract String getText(T value);
    public String getTooltip(String text) {
        return getTooltipFromVal(getValue(text));
    }
    public String getTooltipFromVal(T value) {
        return null;
    }
    public abstract List<String> getOptions();

    private Object selectedItem;

    public void setSelectedItem(Object item) {
        selectedItem = item;
    }
    public Object getSelectedItem() {
        return selectedItem;
    }

    public int getSize() {
        return getOptions().size();
    }

    public String getElementAt(int index) {
        return getOptions().get(index);
    }

    public void addListDataListener(ListDataListener l) {
        dataListeners.add(ListDataListener.class, l);
    }
    public void removeListDataListener(ListDataListener l) {
        dataListeners.remove(ListDataListener.class, l);
    }
}
