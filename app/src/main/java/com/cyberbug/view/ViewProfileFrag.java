package com.cyberbug.view;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.cyberbug.api.APIRequest;
import com.cyberbug.api.APIResponse;
import com.cyberbug.api.AsyncRESTDispatcher;
import com.cyberbug.api.UIUpdaterResponse;
import com.cyberbug.api.UIUpdaterVoid;
import com.cyberbug.R;
import com.google.android.material.snackbar.Snackbar;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;

/**
 * A fragment that shows the current user profile
 */
public class ViewProfileFrag extends Fragment {

    private static final String ARG_ERROR_MESSAGE = "errorMessage";
    private String errorMessage = null;

    private TextView nameText;
    private TextView surnameText;
    private TextView emailText;
    private TextView phoneText;


    public static ViewProfileFrag newInstance(String errorMessage) {
        ViewProfileFrag iof = new ViewProfileFrag();
        Bundle args = new Bundle();
        args.putString(ARG_ERROR_MESSAGE, errorMessage);
        iof.setArguments(args);
        return iof;
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

        // Create a fragment view
        View v = inflater.inflate(R.layout.fragment_view_profile, container, false);

        // Initialize fragment components
        nameText = v.findViewById(R.id.txt_view_name);
        surnameText = v.findViewById(R.id.txt_view_surname);
        emailText = v.findViewById(R.id.txt_view_email);
        phoneText = v.findViewById(R.id.txt_view_phone);

        // Set fragment title
        Fragment parent = this.getParentFragment();
        if (parent != null) {
            // Check to avoid crash if the parent fragment does not exists (should not happen)
            Toolbar tBar = parent.requireView().findViewById(R.id.toolbar);
            tBar.setTitle(getString(R.string.my_profile_info));
            parent.setHasOptionsMenu(false);
        }

        return v;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        populateTextViews();
    }

    private void populateTextViews() {
        APIRequest getUserInfo = MainActivity.fsAPI.getUserProfileRequest(MainActivity.sData.authToken, MainActivity.sData.thisUserId);
        UIUpdaterVoid<FragmentActivity> preUpdater = new UIUpdaterVoid<>(this.requireActivity(), this::showLoading);
        UIUpdaterResponse<FragmentActivity> postUpdater = new UIUpdaterResponse<>(this.requireActivity(), this::populateAndShowUserInfo);
        new AsyncRESTDispatcher(preUpdater, postUpdater).execute(getUserInfo);
    }

    private void showLoading(FragmentActivity act){
            act.findViewById(R.id.info_user_main_layout).setVisibility(View.GONE);
            act.findViewById(R.id.progressBar_UserProfile).setVisibility(View.VISIBLE);
    }

    private void populateAndShowUserInfo(FragmentActivity act, List<APIResponse> resList){
        APIResponse res = resList.get(0);
        if (res.responseCode == 200) {
            // All ok
            try {
                JSONObject usr = res.jsonResponse;
                if(usr == null) throw new JSONException("JSON response null pointer exception");

                String name = usr.getString("given_name");
                String lastname = usr.getString("family_name");
                String email = usr.getString("email");
                String phone = usr.getString("phone");

                nameText.setText(name);
                surnameText.setText(lastname);
                emailText.setText(email);
                phoneText.setText(phone);

            } catch (JSONException e) {
                // Just to debug, user error feedback given below
                e.printStackTrace();
            }

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
            Snackbar.make(this.requireView(), errorMessage, Snackbar.LENGTH_LONG).show();
        }
        act.findViewById(R.id.progressBar_UserProfile).setVisibility(View.GONE);
        act.findViewById(R.id.info_user_main_layout).setVisibility(View.VISIBLE);
    }

}