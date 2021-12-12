package com.cyberbug.model;

public class User {
    public final String name;
    public final String lastname;

    public User(String name, String lastname){
        this.name = name;
        this.lastname = lastname;
    }

    @Override
    public String toString() {
        return name + " " + lastname;
    }
}
