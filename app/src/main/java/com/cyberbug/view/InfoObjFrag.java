package com.cyberbug.view;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.example.grafica.R;



/**
 * A simple {@link Fragment} subclass.
 * Use the {@link InfoObjFrag#newInstance} factory method to
 * create an instance of this fragment.
 */
public class InfoObjFrag extends Fragment {

    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_ERROR_MESSAGE = "errorMessage";
    private String errorMessage = null;

    private TextView owner;
    private TextView desc;
    private TextView state;
    private TextView sharedGroup;

    public InfoObjFrag() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param errorMessage Parameter 1.
     * @return A new instance of fragment InfoObjFrag.
     */
    // TODO: Rename and change types and number of parameters
    public static InfoObjFrag newInstance(String errorMessage) {
        InfoObjFrag iof = new InfoObjFrag();
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

        // Create fragment view
        View v = inflater.inflate(R.layout.fragment_info_obj, container, false);

        // Initialize fragment components
        owner = v.findViewById(R.id.txt_obj_owner);
        desc = v.findViewById(R.id.txt_obj_desc);
        state = v.findViewById(R.id.txt_obj_state);
        sharedGroup = v.findViewById(R.id.txt_obj_group);

        // Set buttons listeners
        Button abort = v.findViewById(R.id.btn_abort_loan);
        Button loan = v.findViewById(R.id.btn_obj_loan);

        // Set fragment title
        Fragment parent = this.getParentFragment();
        if (parent != null) {
            // Check to avoid crash if the parent fragment does not exists (should not happen)
            Toolbar tBar = parent.requireView().findViewById(R.id.toolbar);
            tBar.setTitle(getString(R.string.obj_info));
            parent.setHasOptionsMenu(false);
        }

        return v;

    }

}