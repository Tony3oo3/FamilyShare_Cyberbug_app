package com.cyberbug.view;

import android.os.Bundle;

import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.cyberbug.R;
import com.cyberbug.api.APIRequest;
import com.cyberbug.api.AsyncRESTDispatcher;
import com.cyberbug.api.FSAPIWrapper;
import com.cyberbug.api.UIUpdaterResponse;
import com.cyberbug.api.UIUpdaterVoid;
import com.google.android.material.snackbar.Snackbar;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link RequestObjFrag#newInstance} factory method to
 * create an instance of this fragment.
 */
public class RequestObjFrag extends Fragment {

    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_ERROR_MESSAGE = "errorMessage";
    private String errorMessage = null;

    private TextView objOwner;
    private TextView objDecription;
    private TextView returnData;


    public RequestObjFrag() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @return A new instance of fragment RequestObjFrag.
     */

    public static RequestObjFrag newInstance(String errorMessage) {
        RequestObjFrag iof = new RequestObjFrag();
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
        View v = inflater.inflate(R.layout.fragment_request_obj, container, false);

        // Initialize fragment components
        objOwner = v.findViewById(R.id.owner_obj_request);
        objDecription = v.findViewById(R.id.descr_obj_request);
        returnData = v.findViewById(R.id.return_date_obj_request);

        // Set buttons listeners
        Button cancelRequest = v.findViewById(R.id.btn_canc_requestObj);
        cancelRequest.setOnClickListener(this::onClickCancelButton);
        Button confirmRequest = v.findViewById(R.id.btn_confirm_requestObj);
        confirmRequest.setOnClickListener(this::onClickAddRequestObjButton);

        // Set fragment title
        Fragment parent = this.getParentFragment();
        if (parent != null) {
            // Check to avoid crash if the parent fragment does not exists (should not happen)
            Toolbar tBar = parent.requireView().findViewById(R.id.toolbar);
            tBar.setTitle(getString(R.string.request_an_object));
            parent.setHasOptionsMenu(false);
        }

        return v;
    }


    public void onClickCancelButton(View v){
    }

    public void onClickAddRequestObjButton(View v){
        // Get text

    }
}