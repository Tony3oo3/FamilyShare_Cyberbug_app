package com.cyberbug.api;

import android.net.Uri;

import com.cyberbug.api.APIRequest;
import com.cyberbug.api.AsyncRESTDispatcher;

/**
 * Class that wraps all the necessary REST API calls of Families Share server.
 * All the requests are executed with an async task.
 */
public class FSAPIWrapper {
    private AsyncRESTDispatcher dispatcher;
    private final String baseURL;

    public FSAPIWrapper(String baseURL) {
        this.dispatcher = null;
        this.baseURL = baseURL;
    }

    private void clearDispatcher(){
        if(dispatcher != null){
            dispatcher.cancel(true);
        }
    }

    private String strToURI(String key, String value){
        Uri.Builder b = new Uri.Builder();
        b.appendQueryParameter(key, value);
        return b.toString().substring(1);
    }

    // Users API
    // Tested
    public void registerUser(UserRegInfo user, UIUpdaterVoid<?> preExec, UIUpdaterResponse<?> postExec){
        clearDispatcher();
        String endpoint = baseURL + "/api/users/";
        APIRequest req = new APIRequest(endpoint, "POST", user.getURI());
        this.dispatcher = new AsyncRESTDispatcher(preExec, postExec);
        this.dispatcher.execute(req);
    }

    // Tested
    public void userLogin(LoginUser user, UIUpdaterVoid<?> preExec, UIUpdaterResponse<?> postExec){
        clearDispatcher();
        String endpoint = baseURL + "/api/users/authenticate/email";
        APIRequest req = new APIRequest(endpoint, "POST", user.getURI());
        this.dispatcher = new AsyncRESTDispatcher(preExec, postExec);
        this.dispatcher.execute(req);
    }

    // Tested
    public void getUserProfile(String authToken, String userId, UIUpdaterVoid<?> preExec, UIUpdaterResponse<?> postExec){
        clearDispatcher();
        String endpoint = baseURL + "/api/users/" + userId + "/profile";
        APIRequest req = new APIRequest(endpoint, "GET", null);
        req.addHeader("Authorization", "Bearer " + authToken);
        this.dispatcher = new AsyncRESTDispatcher(preExec, postExec);
        this.dispatcher.execute(req);
    }

    // Tested
    public void getUser(String authToken, String thisUserId, UIUpdaterVoid<?> preExec, UIUpdaterResponse<?> postExec){
        clearDispatcher();
        String endpoint = baseURL + "/api/users/" + thisUserId;
        APIRequest req = new APIRequest(endpoint, "GET", null);
        req.addHeader("Authorization", "Bearer " + authToken);
        this.dispatcher = new AsyncRESTDispatcher(preExec, postExec);
        this.dispatcher.execute(req);
    }

    // Tested
    public void joinGroup(String authToken, String thisUserId, String groupId, UIUpdaterVoid<?> preExec, UIUpdaterResponse<?> postExec){
        clearDispatcher();
        String endpoint = baseURL + "/api/users/" + thisUserId + "/groups";
        APIRequest req = new APIRequest(endpoint, "POST", strToURI("group_id", groupId));
        req.addHeader("Authorization", "Bearer " + authToken);
        this.dispatcher = new AsyncRESTDispatcher(preExec, postExec);
        this.dispatcher.execute(req);
    }

    // Tested
    public void exitGroup(String authToken, String thisUserId, String groupId, UIUpdaterVoid<?> preExec, UIUpdaterResponse<?> postExec){
        clearDispatcher();
        String endpoint = baseURL + "/api/users/" + thisUserId + "/groups/" + groupId;
        APIRequest req = new APIRequest(endpoint, "DELETE", null);
        req.addHeader("Authorization", "Bearer " + authToken);
        this.dispatcher = new AsyncRESTDispatcher(preExec, postExec);
        this.dispatcher.execute(req);
    }

    // Tested
    public void getJoinedGroups(String authToken, String thisUserId, UIUpdaterVoid<?> preExec, UIUpdaterResponse<?> postExec){
        clearDispatcher();
        String endpoint = baseURL + "/api/users/" + thisUserId + "/groups";
        APIRequest req = new APIRequest(endpoint, "GET", null);
        req.addHeader("Authorization", "Bearer " + authToken);
        this.dispatcher = new AsyncRESTDispatcher(preExec, postExec);
        this.dispatcher.execute(req);
    }

    public void updateProfile(String authToken, String thisUserId, UserProfileInfo thisUserInfo, UIUpdaterVoid<?> preExec, UIUpdaterResponse<?> postExec){
        clearDispatcher();
        String endpoint = baseURL + "/api/users/" + thisUserId + "/profile";
        APIRequest req = new APIRequest(endpoint, "PATCH", thisUserInfo.getURI());
        req.addHeader("Authorization", "Bearer " + authToken);
        this.dispatcher = new AsyncRESTDispatcher(preExec, postExec);
        this.dispatcher.execute(req);
    }

    // Groups API
    public void createGroup(String authToken, NewGroupInfo group, UIUpdaterVoid<?> preExec, UIUpdaterResponse<?> postExec){
        clearDispatcher();
        String endpoint = baseURL + "/api/groups";
        APIRequest req = new APIRequest(endpoint, "POST", group.getURI());
        req.addHeader("Authorization", "Bearer " + authToken);
        this.dispatcher = new AsyncRESTDispatcher(preExec, postExec);
        this.dispatcher.execute(req);
    }

    public void getAllGroups(String authToken, UIUpdaterVoid<?> preExec, UIUpdaterResponse<?> postExec){
        clearDispatcher();
        String endpoint = baseURL + "/api/groups";
        APIRequest req = new APIRequest(endpoint, "GET", null);
        req.addHeader("Authorization", "Bearer " + authToken);
        req.addHeader("searchBy", "all");
        this.dispatcher = new AsyncRESTDispatcher(preExec, postExec);
        this.dispatcher.execute(req);
    }

    public void getGroupById(String groupId, UIUpdaterVoid<?> preExec, UIUpdaterResponse<?> postExec){
        clearDispatcher();
        String endpoint = baseURL + "/api/groups/" + groupId;
        APIRequest req = new APIRequest(endpoint, "GET", null);
        this.dispatcher = new AsyncRESTDispatcher(preExec, postExec);
        this.dispatcher.execute(req);
    }

    public void deleteGroup(String authToken, String groupId, UIUpdaterVoid<?> preExec, UIUpdaterResponse<?> postExec){
        clearDispatcher();
        String endpoint = baseURL + "/api/groups/" + groupId;
        APIRequest req = new APIRequest(endpoint, "DELETE", null);
        req.addHeader("Authorization", "Bearer " + authToken);
        this.dispatcher = new AsyncRESTDispatcher(preExec, postExec);
        this.dispatcher.execute(req);
    }

    public void updateGroupInfo(String authToken, String groupId, GroupInfo thisGroupInfo, UIUpdaterVoid<?> preExec, UIUpdaterResponse<?> postExec){
        clearDispatcher();
        String endpoint = baseURL + "/api/groups/" + groupId;
        APIRequest req = new APIRequest(endpoint, "PATCH", thisGroupInfo.getURI());
        req.addHeader("Authorization", "Bearer " + authToken);
        this.dispatcher = new AsyncRESTDispatcher(preExec, postExec);
        this.dispatcher.execute(req);
    }

    public void updateGroupSettings(String authToken, String groupId, String settings, UIUpdaterVoid<?> preExec, UIUpdaterResponse<?> postExec){
        clearDispatcher();
        String endpoint = baseURL + "/api/groups/" + groupId + "/settings";
        APIRequest req = new APIRequest(endpoint, "PATCH", strToURI("settingsPatch", settings));
        req.addHeader("Authorization", "Bearer " + authToken);
        this.dispatcher = new AsyncRESTDispatcher(preExec, postExec);
        this.dispatcher.execute(req);
    }

    public void getGroupSettings(String groupId, UIUpdaterVoid<?> preExec, UIUpdaterResponse<?> postExec){
        clearDispatcher();
        String endpoint = baseURL + "/api/groups/" + groupId + "/settings";
        APIRequest req = new APIRequest(endpoint, "GET", null);
        this.dispatcher = new AsyncRESTDispatcher(preExec, postExec);
        this.dispatcher.execute(req);
    }

    public void getGroupMembers(String groupId, UIUpdaterVoid<?> preExec, UIUpdaterResponse<?> postExec){
        clearDispatcher();
        String endpoint = baseURL + "/api/groups/" + groupId + "/members";
        APIRequest req = new APIRequest(endpoint, "GET", null);
        this.dispatcher = new AsyncRESTDispatcher(preExec, postExec);
        this.dispatcher.execute(req);
    }

    public void removeUserFromGroup(String authToken, String groupId, String userId, UIUpdaterVoid<?> preExec, UIUpdaterResponse<?> postExec){
        clearDispatcher();
        String endpoint = baseURL + "/api/groups/" + groupId + "/members/" + userId;
        APIRequest req = new APIRequest(endpoint, "DELETE", null);
        req.addHeader("Authorization", "Bearer " + authToken);
        this.dispatcher = new AsyncRESTDispatcher(preExec, postExec);
        this.dispatcher.execute(req);
    }

    // Objects API
    public void insertObject(String authToken, String id, ObjectInfo obj, UIUpdaterVoid<?> preExec, UIUpdaterResponse<?> postExec){
        clearDispatcher();
        String endpoint = baseURL + "/api/objects/" + id;
        APIRequest req = new APIRequest(endpoint, "POST", obj.getURI());
        req.addHeader("Authorization", "Bearer " + authToken);
        this.dispatcher = new AsyncRESTDispatcher(preExec, postExec);
        this.dispatcher.execute(req);
    }

    public void getUserObjects(String authToken, String thisUserId, UIUpdaterVoid<?> preExec, UIUpdaterResponse<?> postExec) {
        clearDispatcher();
        String endpoint = baseURL + "/api/objects/" + thisUserId;
        APIRequest req = new APIRequest(endpoint, "GET", null);
        req.addHeader("Authorization", "Bearer " + authToken);
        this.dispatcher = new AsyncRESTDispatcher(preExec, postExec);
        this.dispatcher.execute(req);
    }

    public void searchObject(){

    }

    public static class UserRegInfo{
        public final String name;
        public final String lastname;
        public final String number;
        public final String email;
        public final String password;
        public final String visible;
        public final String language;
        public final String deviceToken;

        public UserRegInfo(String name, String lastname, String number, String email, String password, String visible, String language, String deviceToken) {
            this.name = name;
            this.lastname = lastname;
            this.number = number;
            this.email = email;
            this.password = password;
            this.visible = visible;
            this.language = language;
            this.deviceToken = deviceToken;
        }

        public UserRegInfo(String name, String lastname, String number, String email, String password, String visible, String deviceToken) {
            this.name = name;
            this.lastname = lastname;
            this.number = number;
            this.email = email;
            this.password = password;
            this.visible = visible;
            this.language = "IT";
            this.deviceToken = deviceToken;
        }

        public String getURI(){
            Uri.Builder b = new Uri.Builder();
            b.appendQueryParameter("given_name", this.name);
            b.appendQueryParameter("family_name", this.lastname);
            b.appendQueryParameter("number", this.number);
            b.appendQueryParameter("email", this.email);
            b.appendQueryParameter("password", this.password);
            b.appendQueryParameter("visible", this.visible);
            b.appendQueryParameter("language", this.language);
            b.appendQueryParameter("deviceToken", this.deviceToken);
            return b.toString().substring(1);
        }
    }
    public static class LoginUser{
        public final String email;
        public final String password;
        public final String deviceToken;
        public final String language;
        public final String origin;

        public LoginUser(String email, String password, String deviceToken, String language, String origin) {
            this.email = email;
            this.password = password;
            this.deviceToken = deviceToken;
            this.language = language;
            this.origin = origin;
        }

        public LoginUser(String email, String password, String deviceToken) {
            this.email = email;
            this.password = password;
            this.deviceToken = deviceToken;
            this.language = "IT";
            this.origin = "android";
        }

        public String getURI(){
            Uri.Builder b = new Uri.Builder();
            b.appendQueryParameter("email", this.email);
            b.appendQueryParameter("password", this.password);
            b.appendQueryParameter("deviceToken", this.deviceToken);
            b.appendQueryParameter("language", this.language);
            b.appendQueryParameter("origin", this.origin);
            return b.toString().substring(1);
        }
    }
    public static class UserProfileInfo{
        public final String name;
        public final String lastname;
        public final String email;
        public final String phone;
        public final String phoneType;
        public final String visible;
        public final String street;
        public final String number;
        public final String city;
        public final String description;
        public final String contactOption;

        public UserProfileInfo(String name, String lastname, String email, String phone, String phoneType, String visible, String street, String number, String city, String description, String contactOption) {
            this.name = name;
            this.lastname = lastname;
            this.email = email;
            this.phone = phone;
            this.phoneType = phoneType;
            this.visible = visible;
            this.street = street;
            this.number = number;
            this.city = city;
            this.description = description;
            this.contactOption = contactOption;
        }

        public String getURI(){
            Uri.Builder b = new Uri.Builder();
            b.appendQueryParameter("given_name", this.name);
            b.appendQueryParameter("family_name", this.lastname);
            b.appendQueryParameter("email", this.email);
            b.appendQueryParameter("phone", this.phone);
            b.appendQueryParameter("phone_type", this.phoneType);
            b.appendQueryParameter("visible", this.visible);
            b.appendQueryParameter("street", this.street);
            b.appendQueryParameter("number", this.number);
            b.appendQueryParameter("city", this.city);
            b.appendQueryParameter("description", this.description);
            b.appendQueryParameter("contact_option", this.contactOption);
            return b.toString().substring(1);
        }
    }
    public static class NewGroupInfo{
        // TODO subclass of GroupInfo?
        public final String description;
        public final String location;
        public final String name;
        public final String visible;
        public final String ownerId;
        public final String contactType;
        public final String contactInfo;

        public NewGroupInfo(String description, String location, String name, String visible, String ownerId, String contactType, String contactInfo) {
            this.description = description;
            this.location = location;
            this.name = name;
            this.visible = visible;
            this.ownerId = ownerId;
            this.contactType = contactType;
            this.contactInfo = contactInfo;
        }

        public String getURI(){
            Uri.Builder b = new Uri.Builder();
            b.appendQueryParameter("description", this.description);
            b.appendQueryParameter("location", this.location);
            b.appendQueryParameter("name", this.name);
            b.appendQueryParameter("visible", this.visible);
            b.appendQueryParameter("owner_id", this.ownerId);
            b.appendQueryParameter("contact_type", this.contactType);
            b.appendQueryParameter("contact_info", this.contactInfo);
            return b.toString().substring(1);
        }

    }
    public static class GroupInfo{
        public final String visible;
        public final String name;
        public final String description;
        public final String location;
        public final String background;
        public final String contactType;
        public final String contactInfo;

        public GroupInfo(String visible, String name, String description, String location, String background, String contactType, String contactInfo) {
            this.visible = visible;
            this.name = name;
            this.description = description;
            this.location = location;
            this.background = background;
            this.contactType = contactType;
            this.contactInfo = contactInfo;
        }

        public String getURI(){
            Uri.Builder b = new Uri.Builder();
            b.appendQueryParameter("visible", this.visible);
            b.appendQueryParameter("name", this.name);
            b.appendQueryParameter("description", this.description);
            b.appendQueryParameter("location", this.location);
            b.appendQueryParameter("contact_type", this.contactType);
            b.appendQueryParameter("contact_info", this.contactInfo);
            return b.toString().substring(1);
        }
    }
    public static class ObjectInfo{
        public final String objectName;
        public final String objectDescription;

        public ObjectInfo(String objectName, String objectDescription) {
            this.objectName = objectName;
            this.objectDescription = objectDescription;
        }

        public String getURI(){
            Uri.Builder b = new Uri.Builder();
            b.appendQueryParameter("object_name", this.objectName);
            b.appendQueryParameter("object_description", this.objectDescription);
            return b.toString().substring(1);
        }
    }
}