package com.cyberbug.api;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * Class that has all the required objects that represents a REST API response
 * It includes the response code, a parsed json ecc.
 */
public class APIResponse {
    public final int responseCode;
    public final JSONObject jsonResponse;
    public final JSONArray jsonResponseArray;


    public APIResponse(int responseCode, String jsonResponseStr) throws JSONException {
        this.responseCode = responseCode;

        JSONObject tempJsonObj = null;
        JSONArray tempJsonArr = null;
        if(responseCode == 200 && jsonResponseStr.length() > 0 ) {
            if (jsonResponseStr.charAt(0) == '{'){
                tempJsonObj = new JSONObject(jsonResponseStr);
            }else if (jsonResponseStr.charAt(0) == '['){
                tempJsonArr = new JSONArray(jsonResponseStr);
            }
        }

        this.jsonResponse = tempJsonObj;
        this.jsonResponseArray = tempJsonArr;
    }

    public APIResponse(int responseCode){
        this.responseCode = responseCode;
        this.jsonResponse = null;
        this.jsonResponseArray = null;
    }
}
