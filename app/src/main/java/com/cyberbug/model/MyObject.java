package com.cyberbug.model;

public class MyObject {
    public final String id;
    public final String objName;
    public final String objDescription;
    public final String objOwner;
    public final String state;

    public MyObject(String id, String objName, String objDescription, String objOwner, String state){
        this.id = id;
        this.objName = objName;
        this.objDescription = objDescription;
        this.objOwner = objOwner;
        this.state = state;
    }

    @Override
    public String toString() {
        return objName;
    }
}
