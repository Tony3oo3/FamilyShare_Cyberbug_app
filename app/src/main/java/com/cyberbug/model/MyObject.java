package com.cyberbug.model;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * This class holds all the "object" information
 */
public class MyObject implements Comparable<MyObject>{
    public final String id;
    public final String objName;
    public final String objDescription;
    public final String objOwner;
    public final String req_userId;
    public final String shared_with;
    public final String state;

    public MyObject(String id, String objName, String objDescription, String objOwner, String req_userId, String shared_with,String state){
        this.id = id;
        this.objName = objName;
        this.objDescription = objDescription;
        this.objOwner = objOwner;
        this.req_userId = req_userId;
        this.shared_with = shared_with;
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
                jObj.getString("req_to_share"),
                jObj.getString("shared_with_user"),
                jObj.getString("shared_with_user")
        );
    }


    @Override
    public int compareTo(MyObject o) {
        return this.id.compareTo(o.id);
    }

    @Override
    public boolean equals(@Nullable Object obj) {
        if(obj instanceof MyObject)
            return this.id.equals(((MyObject) obj).id);
        else return false;
    }
}
