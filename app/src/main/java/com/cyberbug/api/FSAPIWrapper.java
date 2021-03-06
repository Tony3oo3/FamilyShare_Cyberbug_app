package com.cyberbug.api;

import android.net.Uri;

/**
 * Class that wraps all the necessary REST API calls of Families Share server.
 * All the requests should be executed with {@link com.cyberbug.api.AsyncRESTDispatcher}AsyncRESTDispatcher
 */
public class FSAPIWrapper {
    private final String baseURL;

    public FSAPIWrapper(String baseURL) {
        this.baseURL = baseURL;
    }

    private String strToURI(String key, String value){
        Uri.Builder b = new Uri.Builder();
        b.appendQueryParameter(key, value);
        return b.toString().substring(1);
    }

    // Users API
    // Tested
    public APIRequest registerUserRequest(UserRegInfo user){
        String endpoint = baseURL + "/api/users/";
        return new APIRequest(endpoint, "POST", user.getURI());
    }

    // Tested
    public APIRequest userLoginRequest(LoginUser user){
        String endpoint = baseURL + "/api/users/authenticate/email";
        return new APIRequest(endpoint, "POST", user.getURI());
        
    }

    // Tested
    public APIRequest getUserProfileRequest(String authToken, String userId){
        String endpoint = baseURL + "/api/users/" + userId + "/profile";
        APIRequest req = new APIRequest(endpoint, "GET", null);
        req.addHeader("Authorization", "Bearer " + authToken);
        return req;
    }

    // Tested
    @SuppressWarnings("unused")
    public APIRequest getUserRequest(String authToken, String thisUserId){
        String endpoint = baseURL + "/api/users/" + thisUserId;
        APIRequest req = new APIRequest(endpoint, "GET", null);
        req.addHeader("Authorization", "Bearer " + authToken);
        return req;
    }

    // Tested
    public APIRequest joinGroupRequest(String authToken, String thisUserId, String groupId){
        String endpoint = baseURL + "/api/users/" + thisUserId + "/groups";
        APIRequest req = new APIRequest(endpoint, "POST", strToURI("group_id", groupId));
        req.addHeader("Authorization", "Bearer " + authToken);
        return req;
    }

    // Tested
    @SuppressWarnings("unused")
    public APIRequest exitGroupRequest(String authToken, String thisUserId, String groupId){
        String endpoint = baseURL + "/api/users/" + thisUserId + "/groups/" + groupId;
        APIRequest req = new APIRequest(endpoint, "DELETE", null);
        req.addHeader("Authorization", "Bearer " + authToken);
        return req;
    }

    // Tested
    public APIRequest getJoinedGroupsRequest(String authToken, String thisUserId){
        String endpoint = baseURL + "/api/users/" + thisUserId + "/groups";
        APIRequest req = new APIRequest(endpoint, "GET", null);
        req.addHeader("Authorization", "Bearer " + authToken);
        return req;
    }

    @SuppressWarnings("unused")
    public APIRequest updateProfileRequest(String authToken, String thisUserId, UserProfileInfo thisUserInfo){
        String endpoint = baseURL + "/api/users/" + thisUserId + "/profile";
        APIRequest req = new APIRequest(endpoint, "PATCH", thisUserInfo.getURI());
        req.addHeader("Authorization", "Bearer " + authToken);
        return req;
    }

    // Groups API
    public APIRequest createGroupRequest(String authToken, NewGroupInfo group){
        String endpoint = baseURL + "/api/groups";
        APIRequest req = new APIRequest(endpoint, "POST", group.getURI());
        req.addHeader("Authorization", "Bearer " + authToken);
        return req;
    }

    public APIRequest getAllGroupsRequest(String authToken){
        String endpoint = baseURL + "/api/groups?searchBy=all";
        APIRequest req = new APIRequest(endpoint, "GET", null);
        req.addHeader("Authorization", "Bearer " + authToken);
        return req;
    }

    public APIRequest getGroupByIdRequest(String groupId){
        String endpoint = baseURL + "/api/groups/" + groupId;
        return new APIRequest(endpoint, "GET", null);
    }

    @SuppressWarnings("unused")
    public APIRequest deleteGroupRequest(String authToken, String groupId){
        String endpoint = baseURL + "/api/groups/" + groupId;
        APIRequest req = new APIRequest(endpoint, "DELETE", null);
        req.addHeader("Authorization", "Bearer " + authToken);
        return req;
    }

    @SuppressWarnings("unused")
    public APIRequest updateGroupInfoRequest(String authToken, String groupId, GroupInfo thisGroupInfo){
        String endpoint = baseURL + "/api/groups/" + groupId;
        APIRequest req = new APIRequest(endpoint, "PATCH", thisGroupInfo.getURI());
        req.addHeader("Authorization", "Bearer " + authToken);
        return req;
    }

    @SuppressWarnings("unused")
    public APIRequest updateGroupSettingsRequest(String authToken, String groupId, String settings){
        String endpoint = baseURL + "/api/groups/" + groupId + "/settings";
        APIRequest req = new APIRequest(endpoint, "PATCH", strToURI("settingsPatch", settings));
        req.addHeader("Authorization", "Bearer " + authToken);
        return req;
    }

    @SuppressWarnings("unused")
    public APIRequest getGroupSettingsRequest(String groupId){
        String endpoint = baseURL + "/api/groups/" + groupId + "/settings";
        return new APIRequest(endpoint, "GET", null);
    }

    public APIRequest getGroupMembersRequest(String groupId){
        String endpoint = baseURL + "/api/groups/" + groupId + "/members";
        return new APIRequest(endpoint, "GET", null);
    }

    @SuppressWarnings("unused")
    public APIRequest removeUserFromGroupRequest(String authToken, String groupId, String userId){
        String endpoint = baseURL + "/api/groups/" + groupId + "/members/" + userId;
        APIRequest req = new APIRequest(endpoint, "DELETE", null);
        req.addHeader("Authorization", "Bearer " + authToken);
        return req;
    }

    // Objects API
    public APIRequest insertObjectRequest(String authToken, String thisUserId, ObjectData obj){
        String endpoint = baseURL + "/api/objects/" + thisUserId;
        APIRequest req = new APIRequest(endpoint, "POST", obj.getURI());
        req.addHeader("Authorization", "Bearer " + authToken);
        return req;
    }

    public APIRequest getUserLentObjectsRequest(String authToken, String thisUserId) {
        String endpoint = baseURL + "/api/objects/" + thisUserId + "/lentObjects";
        APIRequest req = new APIRequest(endpoint, "GET", null);
        req.addHeader("Authorization", "Bearer " + authToken);
        return req;
    }

    public APIRequest getUserBorrowedObjectsRequest(String authToken, String thisUserId) {
        String endpoint = baseURL + "/api/objects/" + thisUserId + "/borrowedObjects";
        APIRequest req = new APIRequest(endpoint, "GET", null);
        req.addHeader("Authorization", "Bearer " + authToken);
        return req;
    }

    public APIRequest searchObjectRequest(String authToken, String objectId){
        String endpoint = baseURL + "/api/objects/" + objectId + "/search";
        APIRequest req = new APIRequest(endpoint, "GET", null);
        req.addHeader("Authorization", "Bearer " + authToken);
        return req;
    }
    public APIRequest getMyObjectRequest(String authToken, String thisUserId){
        String endpoint = baseURL + "/api/objects/" + thisUserId + "/objects";
        APIRequest req = new APIRequest(endpoint, "GET", null);
        req.addHeader("Authorization", "Bearer " + authToken);
        return req;
    }
    public APIRequest loanObjectRequest(String authToken, String obj){
        String endpoint = baseURL + "/api/objects/" + obj + "/share";
        APIRequest req = new APIRequest(endpoint, "GET", null);
        req.addHeader("Authorization", "Bearer " + authToken);
        return req;
    }

    public APIRequest getMySharedGroupObjectsRequest(String authToken, String groupId){
        String endpoint = baseURL + "/api/objects/" + groupId + "/mySharedObjs";
        APIRequest req = new APIRequest(endpoint, "GET", null);
        req.addHeader("Authorization", "Bearer " + authToken);
        return req;
    }

    public APIRequest shareObjectWithGroup(String authToken, String groupId, String objId){
        String endpoint = baseURL + "/api/objects/group/" + objId + "/shareObj";
        APIRequest req = new APIRequest(endpoint, "POST", strToURI("group_id", groupId));
        req.addHeader("Authorization", "Bearer " + authToken);
        return req;
    }

    public APIRequest getSharedGroupObjectsRequest(String authToken, String groupId){
        String endpoint = baseURL + "/api/objects/" + groupId + "/sharedObjs";
        APIRequest req = new APIRequest(endpoint, "GET", null);
        req.addHeader("Authorization", "Bearer " + authToken);
        return req;
    }

    public APIRequest getIncomingRequestsObj(String authToken, String userID){
        String endpoint = baseURL + "/api/objects/" + userID + "/getInShareRequests";
        APIRequest req = new APIRequest(endpoint, "GET", null);
        req.addHeader("Authorization", "Bearer " + authToken);
        return req;
    }

    public APIRequest getOutgoingRequestsObj(String authToken, String userID){
        String endpoint = baseURL + "/api/objects/" + userID + "/getOutShareRequests";
        APIRequest req = new APIRequest(endpoint, "GET", null);
        req.addHeader("Authorization", "Bearer " + authToken);
        return req;
    }
    public APIRequest acceptShareReq(String authToken, String objId){
        String endpoint = baseURL + "/api/objects/" + objId + "/share/accept";
        APIRequest req = new APIRequest(endpoint, "GET", null);
        req.addHeader("Authorization", "Bearer " + authToken);
        return req;
    }
    public APIRequest ignoreShareReq(String authToken, String objId){
        String endpoint = baseURL + "/api/objects/" + objId + "/share/ignore";
        APIRequest req = new APIRequest(endpoint, "GET", null);
        req.addHeader("Authorization", "Bearer " + authToken);
        return req;
    }

    public APIRequest removeSharedObjectFromGroupRequest(String authToken, String objectId, String groupId){
        String endpoint = baseURL + "/api/objects/group/" + objectId + "/shareObj/remove";
        APIRequest req = new APIRequest(endpoint, "POST", strToURI("group_id", groupId));
        req.addHeader("Authorization", "Bearer " + authToken);
        return req;
    }

    public APIRequest deleteObjectRequest(String authToken, String objectId){
        String endpoint = baseURL + "/api/objects/" + objectId + "/remove";
        APIRequest req = new APIRequest(endpoint, "GET", null);
        req.addHeader("Authorization", "Bearer " + authToken);
        return req;
    }
    public APIRequest returnObjectRequest(String authToken, String objectId){
        String endpoint = baseURL + "/api/objects/" + objectId + "/share/return";
        APIRequest req = new APIRequest(endpoint, "GET", null);
        req.addHeader("Authorization", "Bearer " + authToken);
        return req;
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

        @SuppressWarnings("unused")
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

        @SuppressWarnings("unused")
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

    public static class ObjectData {
        public final String objName;
        public final String objDescription;

        public ObjectData(String objName, String objDescription){
            this.objName = objName;
            this.objDescription = objDescription;
        }
        public String getURI(){
            Uri.Builder b = new Uri.Builder();
            b.appendQueryParameter("object_name", this.objName);
            b.appendQueryParameter("object_description", this.objDescription);
            return b.toString().substring(1);
        }
    }
}