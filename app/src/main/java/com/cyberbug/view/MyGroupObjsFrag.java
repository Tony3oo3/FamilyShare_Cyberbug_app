package com.cyberbug.view;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.ProgressBar;

import com.cyberbug.api.UIUpdaterResponse;
import com.cyberbug.api.UIUpdaterVoid;
import com.cyberbug.R;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

public class MyGroupObjsFrag extends Fragment {

    public static MyGroupObjsFrag newInstance() {
        return new MyGroupObjsFrag();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View v = inflater.inflate(R.layout.fragment_my_group_objs, container, false);

        // Set the action button callback
        FloatingActionButton shareObj = v.findViewById(R.id.actionButton_add_obj);
        shareObj.setOnClickListener(this::onShareObjectClick);

        // Populate the list
        populateSharedObject(v);

        // Set the item click listener


        return v;
    }

    private void onShareObjectClick(View v){
        // TODO go to share object page
    }

    private void populateSharedObject(View v){
        // APIResponse sharedObjectsRequest = MainActivity.fsAPI.getMySharedObjectsRequest();
        UIUpdaterVoid<FragmentActivity> preUpdater = new UIUpdaterVoid<>(this.requireActivity(), this::showLoading);
        UIUpdaterResponse<FragmentActivity> postUpdater = new UIUpdaterResponse<>(this.requireActivity(), null);
        // new AsyncRESTDispatcher(preUpdater, postUpdater).execute();
    }

    private void showLoading(FragmentActivity act){
        ProgressBar pr = act.findViewById(R.id.progressBar_mySharedObj);
        pr.setVisibility(View.VISIBLE);
        ListView lw = act.findViewById(R.id.listview_your_shared_obj);
        lw.setVisibility(View.GONE);
    }

}