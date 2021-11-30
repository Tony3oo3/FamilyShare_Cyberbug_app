package com.cyberbug.api;

import androidx.annotation.Nullable;

import com.cyberbug.api.APIResponse;
import com.cyberbug.functional.BiConsumer;

import java.util.Collection;
import java.util.List;


/**
 * Class that has all the necessary objects (including a callback) to update the UI
 * It needs an APIResponse to do so.
 */
public class UIUpdaterResponse<UIElement> {
    private final UIElement el;
    private final BiConsumer<UIElement, List<APIResponse>> updater;

    public UIUpdaterResponse(UIElement el, BiConsumer<UIElement, List<APIResponse>> updater) {
        this.el = el;
        this.updater = updater;
    }

    public void updateUI(@Nullable List<APIResponse> response){
        this.updater.consume(el, response);
    }
}
