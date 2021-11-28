package com.cyberbug.api;

import androidx.annotation.Nullable;

import com.cyberbug.api.APIResponse;
import com.cyberbug.functional.BiConsumer;



/**
 * Class that has all the necessary objects (including a callback) to update the UI
 * It needs an APIResponse to do so.
 */
public class UIUpdaterResponse<UIElement> {
    private final UIElement el;
    private final BiConsumer<UIElement, APIResponse> updater;

    public UIUpdaterResponse(UIElement el, BiConsumer<UIElement, APIResponse> updater) {
        this.el = el;
        this.updater = updater;
    }

    public void updateUI(@Nullable APIResponse response){
        this.updater.consume(el, response);
    }
}
