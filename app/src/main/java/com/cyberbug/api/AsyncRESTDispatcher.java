package com.cyberbug.api;

import android.os.AsyncTask;

import androidx.core.util.Pair;

import org.json.JSONException;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

/**
 * Class responsible to make a REST API call to the Families Share server
 * It also update the UI before and after the call using callbacks passed in construction
 */
public class AsyncRESTDispatcher extends AsyncTask<APIRequest, Integer, List<APIResponse>> {
    private final UIUpdaterVoid<?> preExec;
    private final UIUpdaterResponse<?> postExec;

    public AsyncRESTDispatcher(UIUpdaterVoid<?> preExec, UIUpdaterResponse<?> postExec) {
        super();
        this.preExec = preExec;
        this.postExec = postExec;
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        preExec.updateUI();
    }

    @Override
    protected List<APIResponse> doInBackground(APIRequest... requests) {
        ArrayList<APIResponse> resArray = new ArrayList<>(requests.length);
        for(APIRequest req : requests) {
            APIResponse res = new APIResponse(400);
            try {
                URL endpoint = new URL(req.endpointUrl);
                HttpURLConnection connection = (HttpURLConnection) endpoint.openConnection();

                connection.setRequestMethod(req.method);
                if (req.method.equals("POST")) connection.setDoOutput(true);

                for (Pair<String, String> header : req.headers) {
                    connection.setRequestProperty(header.first, header.second);
                }

                connection.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
                connection.setRequestProperty("Accept", "*/*");
                if (req.bodyUri != null) {
                    OutputStream out = new BufferedOutputStream(connection.getOutputStream());
                    byte[] bodyByte = req.bodyUri.getBytes(StandardCharsets.UTF_8);
                    out.write(bodyByte, 0, bodyByte.length);
                    out.close();
                }
                connection.connect();

                int code = connection.getResponseCode();
                String jsonResponse = "";
                if (code == 200) {
                    InputStream in = new BufferedInputStream(connection.getInputStream());
                    Scanner inScanner = new Scanner(in);
                    jsonResponse = inScanner.nextLine();
                    in.close();
                    inScanner.close();
                }
                connection.disconnect();
                System.out.println("JSON response = " + jsonResponse);
                res = new APIResponse(code, jsonResponse);
            } catch (IOException | JSONException e) {
                e.printStackTrace();
                resArray.add(new APIResponse(400));
            }
            resArray.add(res);
        }
        return resArray;
    }

    @Override
    protected void onPostExecute(List<APIResponse> apiResponse) {
        super.onPostExecute(apiResponse);
        postExec.updateUI(apiResponse);
    }
}
