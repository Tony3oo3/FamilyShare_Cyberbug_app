package com.cyberbug.model;

import androidx.annotation.NonNull;

/**
 * This class il used to build group objects that holds their id and name
 */
public class Group {
    public final String id;
    public final String name;

    public Group(String id, String name) {
        this.id = id;
        this.name = name;
    }

    @NonNull
    @Override
    public String toString() {
        return name;
    }
}
