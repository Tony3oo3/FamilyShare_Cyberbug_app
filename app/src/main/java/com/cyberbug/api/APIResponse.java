package com.cyberbug.api;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Class that has all the required objects that represents a REST API response
 * It includes the response code, a parsed json ecc.
 */
public class APIResponse {
    public final int responseCode;
    public final JSONObject jsonResponse;

    public APIResponse(int responseCode, String jsonResponseStr) throws JSONException {
        this.responseCode = responseCode;
        this.jsonResponse = (responseCode == 200) ? new JSONObject(jsonResponseStr) : null;
    }
}
