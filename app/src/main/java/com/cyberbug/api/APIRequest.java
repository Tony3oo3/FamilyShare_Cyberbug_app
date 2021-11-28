package com.cyberbug.api;

import androidx.core.util.Pair;

import java.util.ArrayList;
import java.util.Collection;

/**
 * Class that has all the objects required to send a request to the Families Share API
 * It also includes a post request callback used to perform saving or ecc. of the result
 */
public class APIRequest {
    public final String endpointUrl;
    public final String method; // TODO crea un enum che contiene tutti i valori possibili per essere safe
    public final String bodyUri;
    public final Collection<Pair<String,String>> headers;

    public APIRequest(String endpointUrl, String method, String bodyUri) {
        this.endpointUrl = endpointUrl;
        this.method = method;
        this.bodyUri = bodyUri;
        this.headers = new ArrayList<>();
    }

    public APIRequest(String endpointUrl, String method, String bodyUri, Collection<Pair<String, String>> headers) {
        this.endpointUrl = endpointUrl;
        this.method = method;
        this.bodyUri = bodyUri;
        this.headers = headers;
    }

    public void addHeader(String key, String value){
        headers.add(new Pair<>(key, value));
    }
}
