package com.cyberbug.view;

import android.os.Bundle;

import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.grafica.R;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link ViewProfileFrag#newInstance} factory method to
 * create an instance of this fragment.
 */
public class ViewProfileFrag extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_ERROR_MESSAGE = "errorMessage";
    private String errorMessage = null;

    // TODO: Rename and change types of parameters
    private TextView name;
    private TextView surname;
    private TextView gender;
    private TextView birthdate;
    private TextView email;
    private TextView phone;

    public ViewProfileFrag() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param errorMessage Parameter 1.
     * @return A new instance of fragment ViewProfileFrag.
     */
    // TODO: Rename and change types and number of parameters
    public static InfoObjFrag newInstance(String errorMessage) {
        InfoObjFrag iof = new InfoObjFrag();
        Bundle args = new Bundle();
        args.putString(ARG_ERROR_MESSAGE, errorMessage);
        iof.setArguments(args);
        return iof;
    }

    /*
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            name = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }
    */

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
        name = v.findViewById(R.id.txt_view_name);
        surname = v.findViewById(R.id.txt_view_surname);
        gender = v.findViewById(R.id.txt_view_gender);
        birthdate = v.findViewById(R.id.txt_view_birthdate);
        email = v.findViewById(R.id.txt_view_email);
        phone = v.findViewById(R.id.txt_view_phone);

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
}