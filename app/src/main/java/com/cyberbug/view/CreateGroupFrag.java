package com.example.grafica;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import com.google.android.material.snackbar.Snackbar;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link CreateGroupFrag#newInstance} factory method to
 * create an instance of this fragment.
 */
public class CreateGroupFrag extends Fragment {

    private static final String ARG_ERROR_MESSAGE = "errorMessage";

    private String errorMessage = null;

    private EditText nameET;
    private EditText locationET;
    private EditText descriptionET;

    public CreateGroupFrag() {
        // Required empty public constructor
    }


    public static CreateGroupFrag newInstance(String errorMessage) {
        CreateGroupFrag createGroupFrag = new CreateGroupFrag();
        Bundle args = new Bundle();
        createGroupFrag.setArguments(args);
        return createGroupFrag;
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


    }
}