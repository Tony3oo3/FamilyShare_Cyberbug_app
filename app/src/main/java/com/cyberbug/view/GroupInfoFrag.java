package com.cyberbug.view;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.ScrollView;
import android.widget.TextView;

import com.cyberbug.api.APIRequest;
import com.cyberbug.api.APIResponse;
import com.cyberbug.api.AsyncRESTDispatcher;
import com.cyberbug.api.UIUpdaterResponse;
import com.cyberbug.api.UIUpdaterVoid;
import com.example.grafica.R;
import com.google.android.material.snackbar.Snackbar;

import org.json.JSONException;

import java.util.List;

public class GroupInfoFrag extends Fragment {

    public static GroupInfoFrag newInstance() {
        return new GroupInfoFrag();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_group_info, container, false);

        getAndShowGroupInfo(v, MainActivity.sData.selectedGroupId);

        return v;
    }

    private void getAndShowGroupInfo(View v, String groupId) {
        APIRequest getGroupInfoReq = MainActivity.fsAPI.getGroupByIdRequest(groupId);
        UIUpdaterVoid<View> preUpdater = new UIUpdaterVoid<>(v, this::preInfoRequest);
        UIUpdaterResponse<View> postUpdater = new UIUpdaterResponse<>(v, this::postInfoRequest);
        new AsyncRESTDispatcher(preUpdater, postUpdater).execute(getGroupInfoReq);
    }

    private void preInfoRequest(View v) {
        // TODO show loading
        ProgressBar pb = v.findViewById(R.id.progressBar_group_info);
        pb.setVisibility(View.VISIBLE);
        ScrollView sv = v.findViewById(R.id.scrollView_group_info);
        sv.setVisibility(View.GONE);
    }

    private void postInfoRequest(View v, List<APIResponse> resList) {
        APIResponse res = resList.get(0);
        if (res.responseCode == 200) {
            // OK
            // Set the field and get the owner name request
            if (res.jsonResponse != null) {
                try {
                    String groupName = res.jsonResponse.getString("name");
                    String groupLocation = res.jsonResponse.getString("location");
                    String groupDescription = res.jsonResponse.getString("description");

                    TextView name = v.findViewById(R.id.text_group_name);
                    TextView location = v.findViewById(R.id.text_group_loc);
                    EditText description = v.findViewById(R.id.text_group_descr);

                    name.setText(groupName);
                    location.setText(groupLocation);
                    description.setText(groupDescription);

                    // Get the owner name from id

                    APIRequest ownerNameReq = MainActivity.fsAPI.getUserProfileRequest(MainActivity.sData.authToken, res.jsonResponse.getString("owner_id"));
                    UIUpdaterVoid<View> preUpdater = new UIUpdaterVoid<>(v, (x) -> {});
                    UIUpdaterResponse<View> postUpdater = new UIUpdaterResponse<>(v, this::postOwnerNameRequest);
                    new AsyncRESTDispatcher(preUpdater, postUpdater).execute(ownerNameReq);
                } catch (JSONException e) {
                    e.printStackTrace();
                    // Generic error
                    Snackbar.make(v, R.string.server_error_generic, Snackbar.LENGTH_LONG).show();
                }
            }
        } else {
            // Generic error
            Snackbar.make(v, R.string.server_error_generic, Snackbar.LENGTH_LONG).show();
        }
    }

    private void postOwnerNameRequest(View v, List<APIResponse> resList){
        APIResponse res = resList.get(0);
        if(res.responseCode == 200){
            // OK
            // Change the owner name textView
            if(res.jsonResponse != null) {
                try {
                    String groupOwnerName = res.jsonResponse.getString("given_name") + " " + res.jsonResponse.getString("family_name");

                    TextView ownerName = v.findViewById(R.id.text_group_owner);
                    ownerName.setText(groupOwnerName);
                }catch (JSONException e){
                    e.printStackTrace();
                    // Generic error
                    Snackbar.make(v, R.string.server_error_generic, Snackbar.LENGTH_LONG).show();
                }
            }
            // TODO hide loading and show main layout
            ProgressBar pb = v.findViewById(R.id.progressBar_group_info);
            pb.setVisibility(View.GONE);
            ScrollView sv = v.findViewById(R.id.scrollView_group_info);
            sv.setVisibility(View.VISIBLE);
        }else{
            // Generic error
            Snackbar.make(v, R.string.server_error_generic, Snackbar.LENGTH_LONG).show();
        }
    }
}