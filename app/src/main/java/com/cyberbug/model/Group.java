package com.cyberbug.model;

public class Group {
    public final String id;
    public final String name;

    public Group(String id, String name) {
        this.id = id;
        this.name = name;
    }

    @Override
    public String toString() {
        return name;
    }
}
