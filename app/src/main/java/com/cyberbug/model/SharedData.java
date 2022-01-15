package com.cyberbug.model;

/**
 * Class that saves the current state of the app
 */
public class SharedData {
    public String authToken;
    public String thisUserId;
    public Group selectedGroup;

    @SuppressWarnings("unused")
    public SharedData(String authToken, String thisUserId, Group thisGroup) {
        this.authToken = authToken;
        this.thisUserId = thisUserId;
        this.selectedGroup = thisGroup;
    }

    public SharedData(String authToken, String thisUserId) {
        this.authToken = authToken;
        this.thisUserId = thisUserId;
        this.selectedGroup = null;
    }

    public SharedData() {
        this.authToken = null;
        this.thisUserId = null;
        this.selectedGroup = null;
    }

}
