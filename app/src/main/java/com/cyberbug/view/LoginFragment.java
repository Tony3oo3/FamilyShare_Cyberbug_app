package com.cyberbug.view;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.SwitchCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentTransaction;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import com.cyberbug.api.APIRequest;
import com.cyberbug.api.APIResponse;
import com.cyberbug.api.AsyncRESTDispatcher;
import com.cyberbug.api.FSAPIWrapper;
import com.cyberbug.api.UIUpdaterResponse;
import com.cyberbug.api.UIUpdaterVoid;
import com.cyberbug.model.SharedData;
import com.example.grafica.R;
import com.google.android.material.snackbar.Snackbar;

import org.json.JSONException;

import java.util.List;
import java.util.UUID;

// TODO fix save credentials

public class LoginFragment extends Fragment {

    private static final String ARG_ERROR_MESSAGE = "errorMessage";

    private String errorMessage = null;

    private EditText emailET;
    private EditText passwordET;
    private SwitchCompat keepAccessSW;

    public static LoginFragment newInstance(String errorMessage) {
        LoginFragment lf = new LoginFragment();
        Bundle args = new Bundle();
        args.putString(ARG_ERROR_MESSAGE, errorMessage);
        lf.setArguments(args);
        return lf;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // if we have saved the token -> skip this and ho to the home fragment

    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Check for args
        if (getArguments() != null) {
            errorMessage = getArguments().getString(ARG_ERROR_MESSAGE);
        }

        // Create fragment view
        View v = inflater.inflate(R.layout.fragment_login, container, false);

        // Initialize fragment components
        emailET = v.findViewById(R.id.txt_login_email);
        passwordET = v.findViewById(R.id.txt_login_password);
        keepAccessSW = v.findViewById(R.id.switch_keep_access);

        // Set buttons listeners
        Button loginButton = v.findViewById(R.id.btn_login);
        loginButton.setOnClickListener(this::onClickLoginButton);
        Button registerButton = v.findViewById(R.id.btn_register);
        registerButton.setOnClickListener(this::onClickRegisterButton);
        return v;
    }
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // Error message Snack Bar
        if(errorMessage != null){
            Snackbar.make(view, errorMessage, Snackbar.LENGTH_LONG).show();
            errorMessage = null; // To prevent the message to show up again
        }
    }

    private void onClickRegisterButton(View v){
        RegistrationFragment registrationFragment = RegistrationFragment.newInstance(null);
        FragmentTransaction fragTrans = this.requireActivity().getSupportFragmentManager().beginTransaction();
        fragTrans.replace(R.id.main_fragment_container, registrationFragment);
        fragTrans.addToBackStack("login");
        fragTrans.commit();
    }

    private void onClickLoginButton(View v){
        String email = emailET.getText().toString();
        String password = passwordET.getText().toString();
        String token = UUID.randomUUID().toString();

        // Error handling
        if((email.isEmpty() || password.isEmpty()) && this.getView() != null){
            Snackbar.make(this.getView(), getString(R.string.insert_email_password), Snackbar.LENGTH_LONG).show();
            return;
        }

        // All ok
        FSAPIWrapper.LoginUser user = new FSAPIWrapper.LoginUser(email, password, token);
        UIUpdaterVoid<FragmentActivity> preUpdater = new UIUpdaterVoid<>(this.requireActivity(), LoginFragment::onPreLoginRequest);
        UIUpdaterResponse<FragmentActivity> postUpdater = new UIUpdaterResponse<>(this.requireActivity(), this::onPostLoginRequest);
        APIRequest req = MainActivity.fsAPI.userLoginRequest(user);
        new AsyncRESTDispatcher(preUpdater, postUpdater).execute(req);
    }

    private static void onPreLoginRequest(FragmentActivity act){
        // Changes to LoadingFragment
        LoadingFragment loadingFragment = LoadingFragment.newInstance();
        FragmentTransaction fragTrans = act.getSupportFragmentManager().beginTransaction();
        fragTrans.replace(R.id.main_fragment_container, loadingFragment);
        fragTrans.commit();
    }

    private void onPostLoginRequest(FragmentActivity act, List<APIResponse> responseList){
        // This is always safe because the API call have at least a request
        APIResponse res = responseList.get(0);
        // Check the status code and then show the correct fragment
        boolean responseError = false;
        if(res.responseCode == 200 && res.jsonResponse != null){
            // user correctly logged in
            try {
                // save the token and userid
                String authToken = res.jsonResponse.getString("token");
                String userId = res.jsonResponse.getString("id");
                MainActivity.sData = new SharedData(authToken, userId);
                if(keepAccessSW.isChecked()){
                    // Saving the token and id in memory
                    SharedPreferences sharedPref = act.getPreferences(Context.MODE_PRIVATE);
                    SharedPreferences.Editor editor = sharedPref.edit();
                    editor.putString("authToken", authToken);
                    editor.putString("userId", userId);
                    editor.apply();
                }

                // go to the HomeFrag
                this.goToHomeFragment(act);
            } catch (JSONException e) {
                responseError = true;
            }
        }

        if(res.responseCode != 200 || responseError){
            // 401: login incorrect
            // other: generic error
            // return to LoginFragment and show error message
            Bundle args = new Bundle();
            args.putString(ARG_ERROR_MESSAGE, res.responseCode == 401 ?
                    act.getString(R.string.login_error_credentials) :
                    act.getString(R.string.server_error_generic));
            this.setArguments(args);

            FragmentTransaction fragTrans = act.getSupportFragmentManager().beginTransaction();
            fragTrans.replace(R.id.main_fragment_container, this);
            fragTrans.commit();
        }
    }

    private void goToHomeFragment(FragmentActivity act){
        HomeFragment homeFragment = HomeFragment.newInstance();
        FragmentTransaction fragTrans = act.getSupportFragmentManager().beginTransaction();
        fragTrans.replace(R.id.main_fragment_container, homeFragment);
        fragTrans.commit();
    }


}