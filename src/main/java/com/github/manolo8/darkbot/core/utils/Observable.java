package com.github.manolo8.darkbot.core.utils;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.function.Consumer;

public class Observable<C> {

    private final List<Consumer<C>> subscribers;

    public C value;

    public Observable() {
        this.subscribers = new ArrayList<>();
    }

    public Observable(C value) {
        this();
        this.value = value;
    }

    public void subscribe(Consumer<C> consumer) {
        this.subscribers.add(consumer);

        if (value != null)
            consumer.accept(value);
    }

    public void next(C value) {
        if (!Objects.equals(this.value, value)) {
            for (Consumer<C> subscribe : subscribers)
                subscribe.accept(value);

            this.value = value;
        }
    }

    public void clear() {
        this.subscribers.clear();
    }
}
