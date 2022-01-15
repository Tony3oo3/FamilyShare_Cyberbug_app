package com.cyberbug.view;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;

import com.cyberbug.R;
import com.cyberbug.api.APIRequest;
import com.cyberbug.api.APIResponse;
import com.cyberbug.api.AsyncRESTDispatcher;
import com.cyberbug.api.FSAPIWrapper;
import com.cyberbug.api.UIUpdaterResponse;
import com.cyberbug.api.UIUpdaterVoid;
import com.google.android.material.snackbar.Snackbar;

import java.util.List;

/**
 * Fragment used to create a new group
 */
public class CreateGroupFrag extends Fragment {

    private static final String ARG_ERROR_MESSAGE = "errorMessage";

    private String errorMessage = null;

    private EditText nameET;
    private EditText locationET;
    private EditText descriptionET;

    public static CreateGroupFrag newInstance(String errorMessage) {
        CreateGroupFrag createGroupFrag = new CreateGroupFrag();
        Bundle args = new Bundle();
        args.putString(ARG_ERROR_MESSAGE, errorMessage);
        createGroupFrag.setArguments(args);
        return createGroupFrag;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        // Check for args
        if (getArguments() != null) {
            errorMessage = getArguments().getString(ARG_ERROR_MESSAGE);
        }

        // Create fragment view
        View v = inflater.inflate(R.layout.fragment_create_group, container, false);

        // Get the form fields
        nameET = v.findViewById(R.id.groupNameText);
        locationET = v.findViewById(R.id.groupLocationText);
        descriptionET = v.findViewById(R.id.groupDescrText);

        // Set buttons click listeners
        Button erase = v.findViewById(R.id.createGroupEraseButton);
        erase.setOnClickListener(this::onClickEraseButton);
        Button confirm = v.findViewById(R.id.createGroupConfirmButton);
        confirm.setOnClickListener(this::onClickConfirmButton);

        // Sets fragment tile
        Fragment parent = this.getParentFragment();
        if (parent != null) {
            // Check to avoid crash if the parent fragment does not exists (should not happen)
            Toolbar tBar = parent.requireView().findViewById(R.id.toolbar);
            tBar.setTitle(getString(R.string.create_group_title));
            parent.setHasOptionsMenu(false);
        }

        return v;
    }

    private void onClickEraseButton(View v) {
        // Clear the TextViews
        nameET.getText().clear();
        locationET.getText().clear();
        descriptionET.getText().clear();
    }

    private void onClickConfirmButton(View v) {

        // Get all the text
        String name = nameET.getText().toString();
        String location = locationET.getText().toString();
        String description = descriptionET.getText().toString();

        // Error handling
        if(name.isEmpty() || location.isEmpty() || description.isEmpty()) {
            Snackbar.make(this.requireView(), getString(R.string.insert_required_fields), Snackbar.LENGTH_LONG).show();
            return;
        }

        // All is ok
        // Send the request to the server


        FSAPIWrapper.NewGroupInfo group = new FSAPIWrapper.NewGroupInfo(description, location, name, "true", MainActivity.sData.thisUserId, null, null);
        UIUpdaterVoid<FragmentActivity> preUpdater = new UIUpdaterVoid<>(this.requireActivity(), CreateGroupFrag::onPreCreateGroupRequest);
        UIUpdaterResponse<FragmentActivity> postUpdater = new UIUpdaterResponse<>(this.requireActivity(), this::onPostCreateGroupRequest);
        APIRequest req = MainActivity.fsAPI.createGroupRequest(MainActivity.sData.authToken, group);
        new AsyncRESTDispatcher(preUpdater, postUpdater).execute(req);


    }

    private static void onPreCreateGroupRequest(FragmentActivity activity) {
        activity.findViewById(R.id.create_group_main_layout).setVisibility(View.GONE);
        activity.findViewById(R.id.create_group_loading_layout).setVisibility(View.VISIBLE);
    }

    private void onPostCreateGroupRequest(FragmentActivity activity, List<APIResponse> responseList) {
        APIResponse res = responseList.get(0);
        // 200 ok
        // 400 bad request
        // 401 user not authenticated or unauthorized
        // else server error
        if (res.responseCode == 200) {
            //this.returnToCreateGroup(getString(R.string.group_creation_success));
            errorMessage = getString(R.string.group_creation_success);
        } else {
            // some error occurred, return to the fragment and show a snack bar
            switch (res.responseCode){
                case 400:
                    errorMessage = getString(R.string.bad_request);
                    break;
                case 401:
                    errorMessage = getString(R.string.user_not_authenticated);
                    break;
                default:
                    errorMessage = getString(R.string.server_error_generic);
            }
        }
        activity.findViewById(R.id.create_group_loading_layout).setVisibility(View.GONE);
        activity.findViewById(R.id.create_group_main_layout).setVisibility(View.VISIBLE);
        Snackbar.make(this.requireView(), errorMessage, Snackbar.LENGTH_LONG).show();
    }

    /*
    private void returnToCreateGroup(String message){
        CreateGroupFrag createGroupFrag = CreateGroupFrag.newInstance(message);
        FragmentManager fragmentManager = this.getChildFragmentManager();
        fragmentManager.popBackStack();
        fragmentManager.beginTransaction().replace(R.id.home_fragment_container, createGroupFrag).commit();
    }
    */
}