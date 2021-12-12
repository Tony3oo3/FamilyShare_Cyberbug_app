package com.cyberbug.view;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentManager;
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
import com.example.grafica.R;
import com.google.android.material.snackbar.Snackbar;

import java.util.List;
import java.util.UUID;

public class RegistrationFragment extends Fragment {

    private static final String ARG_ERROR_MESSAGE = "errorMessage";

    private String errorMessage = null;

    private EditText nameET;
    private EditText lastnameET;
    private EditText phoneNumberET;
    private EditText emailET;
    private EditText passwordET;
    private EditText confirmPasswordET;


    public static RegistrationFragment newInstance(String errorMessage) {
        RegistrationFragment rf = new RegistrationFragment();
        Bundle args = new Bundle();
        args.putString(ARG_ERROR_MESSAGE, errorMessage);
        rf.setArguments(args);
        return rf;
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Check for args
        if (getArguments() != null) {
            errorMessage = getArguments().getString(ARG_ERROR_MESSAGE);
        }

        // Create the view
        View v = inflater.inflate(R.layout.fragment_registration, container, false);

        // Get the form fields
        nameET = v.findViewById(R.id.txt_registration_name);
        lastnameET = v.findViewById(R.id.txt_registration_lastname);
        phoneNumberET = v.findViewById(R.id.txt_registration_phone);
        emailET = v.findViewById(R.id.txt_registration_email);
        passwordET = v.findViewById(R.id.txt_registration_password);
        confirmPasswordET = v.findViewById(R.id.txt_registration_repeat_password);

        // Set buttons click listeners
        Button cancel = v.findViewById(R.id.button_registration_cancel);
        cancel.setOnClickListener(this::onClickCancelButton);
        Button register = v.findViewById(R.id.button_registration_register);
        register.setOnClickListener(this::onClickRegisterButton);
        return v;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        // Error message Snack Bar
        if (errorMessage != null) {
            Snackbar.make(view, errorMessage, Snackbar.LENGTH_LONG).show();
            errorMessage = null; // To prevent the message to show up again
        }
    }

    private void goToLoginPage(String message) {
        LoginFragment loginFragment = LoginFragment.newInstance(message);
        FragmentManager fragmentManager = this.requireActivity().getSupportFragmentManager();
        // Here we need to pop the stack of the transaction because we are returning to the login screen
        fragmentManager.popBackStack();
        fragmentManager.beginTransaction().replace(R.id.main_fragment_container, loginFragment).commit();
    }

    // When the cancel button is pressed return to the login screen
    private void onClickCancelButton(View v) {
        this.goToLoginPage(null);
    }

    private void onClickRegisterButton(View v) {
        // Get all the text
        String name = nameET.getText().toString();
        String lastname = lastnameET.getText().toString();
        String phoneNumber = phoneNumberET.getText().toString();
        String email = emailET.getText().toString();
        String password = passwordET.getText().toString();
        String confirmedPassword = confirmPasswordET.getText().toString();

        // Error handling
        if(name.isEmpty() || lastname.isEmpty() || email.isEmpty() || password.isEmpty() || confirmedPassword.isEmpty()) {
            Snackbar.make(this.requireView(), getString(R.string.insert_required_fields), Snackbar.LENGTH_LONG).show();
            return;
        }
        if (password.length() < 8) {
            Snackbar.make(this.requireView(), getString(R.string.password_too_short), Snackbar.LENGTH_LONG).show();
            return;
        }
        if (!password.equals(confirmedPassword)) {
            Snackbar.make(this.requireView(), getString(R.string.passwords_dont_match), Snackbar.LENGTH_LONG).show();
            return;
        }
        // All is ok
        // Send the request to the server
        String token = UUID.randomUUID().toString();
        FSAPIWrapper.UserRegInfo user = new FSAPIWrapper.UserRegInfo(name, lastname, phoneNumber, email, password, "true", token);
        UIUpdaterVoid<FragmentActivity> preUpdater = new UIUpdaterVoid<>(this.requireActivity(), RegistrationFragment::onPreRegisterRequest);
        UIUpdaterResponse<FragmentActivity> postUpdater = new UIUpdaterResponse<>(this.requireActivity(), this::onPostRegisterRequest);
        APIRequest req = MainActivity.fsAPI.registerUserRequest(user);
        new AsyncRESTDispatcher(preUpdater, postUpdater).execute(req);
    }

    private static void onPreRegisterRequest(FragmentActivity act) {
        // Changes to LoadingFragment
        LoadingFragment loadingFragment = LoadingFragment.newInstance();
        FragmentTransaction fragTrans = act.getSupportFragmentManager().beginTransaction();
        fragTrans.replace(R.id.main_fragment_container, loadingFragment);
        fragTrans.commit();
    }

    private void onPostRegisterRequest(FragmentActivity act, List<APIResponse> responseList) {
        APIResponse res = responseList.get(0);
        // 200 ok
        // 500 server error (check fields)
        // 409 user already exists
        // else server error
        if (res.responseCode == 200) {
            // get back to the login page
            this.goToLoginPage(getString(R.string.user_registerd_success));
        } else {
            // some error occurred, return to the fragment and show a snack bar
            FragmentTransaction fragTrans = act.getSupportFragmentManager().beginTransaction();


            String errorMessage;
            switch (res.responseCode) {
                case 409:
                    errorMessage = getString(R.string.user_already_exists);
                    break;
                case 500:
                    errorMessage = getString(R.string.server_error_check_fields);
                    break;
                default:
                    errorMessage = getString(R.string.server_error_generic);
            }
            Bundle args = new Bundle();
            args.putString(ARG_ERROR_MESSAGE, errorMessage);
            this.setArguments(args);

            fragTrans.replace(R.id.main_fragment_container, this);
            fragTrans.commit();
        }
    }

}