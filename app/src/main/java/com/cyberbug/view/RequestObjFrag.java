package com.cyberbug.view;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;

import com.cyberbug.R;

/**
 * The fragment that handles the object requests
 */
public class RequestObjFrag extends Fragment {

    public static RequestObjFrag newInstance() {
        return new RequestObjFrag();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Create a fragment view
        View v = inflater.inflate(R.layout.fragment_request_obj, container, false);

        // Initialize fragment components
        // TextView objOwner = v.findViewById(R.id.owner_obj_request);
        // TextView objDecription = v.findViewById(R.id.descr_obj_request);
        // TextView returnData = v.findViewById(R.id.return_date_obj_request);

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


    public void onClickCancelButton(View v){}

    public void onClickAddRequestObjButton(View v){}
}