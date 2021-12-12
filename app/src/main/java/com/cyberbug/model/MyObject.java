package com.cyberbug.model;

import androidx.annotation.NonNull;

import org.json.JSONException;
import org.json.JSONObject;

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

    @NonNull
    @Override
    public String toString() {
        return objName;
    }

    public static MyObject newFromJson(JSONObject jObj) throws JSONException {
        return new MyObject(
                jObj.getString("object_id"),
                jObj.getString("object_name"),
                jObj.getString("object_description"),
                jObj.getString("owner"),
                jObj.getString("shared_with_user")
        );
    }
}
