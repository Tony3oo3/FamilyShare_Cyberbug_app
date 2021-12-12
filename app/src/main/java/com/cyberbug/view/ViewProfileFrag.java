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

public class ViewProfileFrag extends Fragment {

    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_ERROR_MESSAGE = "errorMessage";
    private String errorMessage = null;

    private String userId;

    private TextView nameText;
    private TextView surnameText;
    private TextView genderText;
    private TextView birthdateText;
    private TextView emailText;
    private TextView phoneText;

    public ViewProfileFrag(String UserId) {
        this.userId = MainActivity.sData.thisUserId;
    }

    public static ViewProfileFrag newInstance(String errorMessage, String userId) {
        ViewProfileFrag iof = new ViewProfileFrag(userId);
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
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
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
        genderText = v.findViewById(R.id.txt_view_gender);
        birthdateText = v.findViewById(R.id.txt_view_birthdate);
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
        if (res.responseCode == 200 && res.jsonResponseArray != null) {
            // All ok
            try {
                JSONObject usr = res.jsonResponseArray.getJSONObject(0);
                String name = usr.getString("user_name");
                String lastname = usr.getString("user_lastname");
                String gender = usr.getString("user_gender");
                String birthdate = usr.getString("user_birthdate");
                String email = usr.getString("user_email");
                String phone = usr.getString("user_phone");

                nameText.setText(name);
                surnameText.setText(lastname);
                genderText.setText(gender);
                birthdateText.setText(birthdate);
                emailText.setText(email);
                phoneText.setText(phone);

            } catch (JSONException e) {
                // Just to debug, user error feedback given below
                e.printStackTrace();
            }

        } else {
            // some error occurred, return to the fragment and show a snack bar
            //FragmentTransaction fragmentTransaction = activity.getSupportFragmentManager().beginTransaction();
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
        act.findViewById(R.id.progressBar_UserProfile).setVisibility(View.GONE);
        act.findViewById(R.id.info_user_main_layout).setVisibility(View.VISIBLE);
        Snackbar.make(this.requireView(), errorMessage, Snackbar.LENGTH_LONG).show();
    }

}