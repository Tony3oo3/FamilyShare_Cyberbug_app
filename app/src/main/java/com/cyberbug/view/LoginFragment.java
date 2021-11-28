package com.cyberbug.view;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentTransaction;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import com.cyberbug.api.APIResponse;
import com.cyberbug.api.FSAPIWrapper;
import com.cyberbug.api.UIUpdaterResponse;
import com.cyberbug.api.UIUpdaterVoid;
import com.cyberbug.model.SharedData;
import com.example.grafica.R;
import com.google.android.material.snackbar.Snackbar;

import org.json.JSONException;

import java.util.UUID;

public class LoginFragment extends Fragment {

    private static final String ARG_ERROR_MESSAGE = "errorMessage";

    private String errorMessage = null;
    private EditText emailET;
    private EditText passwordET;

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
        if(getArguments() != null){
            errorMessage = getArguments().getString(ARG_ERROR_MESSAGE);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Create fragment view
        View v = inflater.inflate(R.layout.fragment_login, container, false);

        // Initialize fragment components
        emailET = v.findViewById(R.id.txt_login_email);
        passwordET = v.findViewById(R.id.txt_login_password);

        // Set buttons listeners
        Button loginButton = v.findViewById(R.id.btn_login);
        loginButton.setOnClickListener(this::onClickLoginButton);
        return v;
    }
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        // Error message Snack Bar
        if(errorMessage != null){
            Snackbar.make(view, errorMessage, Snackbar.LENGTH_LONG).show();
        }
    }

    private void onClickLoginButton(View v){
        String token = UUID.randomUUID().toString();
        FSAPIWrapper.LoginUser user = new FSAPIWrapper.LoginUser(emailET.getText().toString(), passwordET.getText().toString(), token);
        UIUpdaterVoid<FragmentActivity> preUpdater = new UIUpdaterVoid<>(this.requireActivity(), LoginFragment::onPreLoginRequest);
        UIUpdaterResponse<FragmentActivity> postUpdater = new UIUpdaterResponse<>(this.requireActivity(), LoginFragment::onPostLoginRequest);
        MainActivity.fsapi.userLogin(user, preUpdater, postUpdater);
    }

    private static void onPreLoginRequest(FragmentActivity act){
        // Changes to LoadingFragment
        LoadingFragment loadingFragment = LoadingFragment.newInstance();
        FragmentTransaction fragTrans = act.getSupportFragmentManager().beginTransaction();
        fragTrans.replace(R.id.main_fragment_container, loadingFragment);
        fragTrans.commit();
    }

    private static void onPostLoginRequest(FragmentActivity act, APIResponse res){
        // Check the status code and then show the correct fragment
        boolean responseError = false;
        if(res.responseCode == 200){
            // user correctly logged in
            try {
                // save the token and userid
                String authToken = res.jsonResponse.getString("token");
                String userId = res.jsonResponse.getString("id");
                MainActivity.sData = new SharedData(authToken, userId);
                // go to the HomeFrag
                HomeFrag homeFragment = HomeFrag.newInstance();
                FragmentTransaction fragTrans = act.getSupportFragmentManager().beginTransaction();
                fragTrans.replace(R.id.main_fragment_container, homeFragment);
                fragTrans.commit();
            } catch (JSONException e) {
                responseError = true;
            }
        }

        if(res.responseCode != 200 || responseError){
            // 401: login incorrect
            // other: generic error
            // return to LoginFragment and show error message
            LoginFragment loginFragment = LoginFragment.newInstance(
                    res.responseCode == 401 ?
                            act.getString(R.string.login_error_credentials) :
                            act.getString(R.string.login_error_generic));

            FragmentTransaction fragTrans = act.getSupportFragmentManager().beginTransaction();
            fragTrans.replace(R.id.main_fragment_container, loginFragment);
            fragTrans.commit();
        }
    }
}