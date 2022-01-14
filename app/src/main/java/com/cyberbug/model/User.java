package com.cyberbug.model;

import androidx.annotation.NonNull;

/**
 * A class that holds the the name and lastname of a user
 */
public class User {
    public final String name;
    public final String lastname;

    public User(String name, String lastname){
        this.name = name;
        this.lastname = lastname;
    }

    @NonNull
    @Override
    public String toString() {
        return name + " " + lastname;
    }
}
