package com.cyberbug.model;

/**
 * Class that saves the current state of the app
 */
public class SharedData {
    public String authToken;
    public String thisUserId;
    public String selectedGroupId;

    public SharedData(String authToken, String thisUserId, String thisGroupId) {
        this.authToken = authToken;
        this.thisUserId = thisUserId;
        this.selectedGroupId = thisGroupId;
    }

    public SharedData(String authToken, String thisUserId) {
        this.authToken = authToken;
        this.thisUserId = thisUserId;
        this.selectedGroupId = null;
    }

    public SharedData() {
        this.authToken = null;
        this.thisUserId = null;
        this.selectedGroupId = null;
    }

}
