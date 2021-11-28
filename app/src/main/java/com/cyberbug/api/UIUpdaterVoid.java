package com.cyberbug.api;


//TODO forse si può usare una anonymous class invece che definirla in questo modo
// lo svantaggio è che se in caso bisognasse aggiungere qualcosa è più facile farlo con una classe

//TODO si potrebbe aggiungere un bound al type parameter

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
