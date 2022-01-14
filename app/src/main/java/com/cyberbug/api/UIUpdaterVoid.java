package com.cyberbug.api;

import com.cyberbug.functional.Consumer;

/**
 * Class that has all the necessary objects (including a callback) to update the UI
 */
public class UIUpdaterVoid<UIElement> {
    private final UIElement el;
    private final Consumer<UIElement> updater;

    public UIUpdaterVoid(UIElement el, Consumer<UIElement> updater) {
        this.el = el;
        this.updater = updater;
    }

    public void updateUI(){
        this.updater.consume(el);
    }
}
