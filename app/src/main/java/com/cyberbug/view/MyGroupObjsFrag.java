package com.cyberbug.view;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.ProgressBar;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import com.cyberbug.R;
import com.cyberbug.api.APIRequest;
import com.cyberbug.api.APIResponse;
import com.cyberbug.api.AsyncRESTDispatcher;
import com.cyberbug.api.UIUpdaterResponse;
import com.cyberbug.api.UIUpdaterVoid;
import com.cyberbug.model.Group;
import com.cyberbug.model.MyObject;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;

import org.json.JSONArray;
import org.json.JSONException;

import java.util.ArrayList;
import java.util.List;

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

        // TODO Set the item click listener
        ListView lv = v.findViewById(R.id.listview_your_shared_obj);
        lv.setOnItemClickListener(this::onMenuItemClick);

        return v;
    }

    private void onShareObjectClick(View v){
        // TODO go to share object page
    }

    public void onMenuItemClick(AdapterView<?> parent, View view, int position, long id) {
        Object clicked = parent.getItemAtPosition(position);
        if (clicked instanceof MyObject && HomeFragment.homeFragmentManager != null) {
            MyObject g = (MyObject) clicked;
            InfoObjFrag objFrag = InfoObjFrag.newInstance(null, g.id);
            HomeFragment.homeFragmentManager.beginTransaction().replace(R.id.home_fragment_container, objFrag).addToBackStack(null).commit();
        }
    }

    private void populateSharedObject(View v){
        APIRequest sharedObjectsRequest = MainActivity.fsAPI.getMySharedGroupObjectsRequest(MainActivity.sData.authToken, MainActivity.sData.selectedGroupId);
        UIUpdaterVoid<View> preUpdater = new UIUpdaterVoid<>(v, this::showLoading);
        UIUpdaterResponse<View> postUpdater = new UIUpdaterResponse<>(v, this::postGetMySharedObjects);
        new AsyncRESTDispatcher(preUpdater, postUpdater).execute(sharedObjectsRequest);
    }

    private void showLoading(View v){
        ProgressBar pr = v.findViewById(R.id.progressBar_mySharedObj);
        pr.setVisibility(View.VISIBLE);
        ListView lw = v.findViewById(R.id.listview_your_shared_obj);
        lw.setVisibility(View.GONE);
    }

    private void showListView(View v){
        ProgressBar pr = v.findViewById(R.id.progressBar_mySharedObj);
        pr.setVisibility(View.GONE);
        ListView lw = v.findViewById(R.id.listview_your_shared_obj);
        lw.setVisibility(View.VISIBLE);
    }

    private void showSnackBar(String message, View v){
        if (this.getView() != null) {
            Snackbar.make(v, message, Snackbar.LENGTH_LONG).show();
        }
    }

    private void showDefaultListMessage(View v) {
        // populate list with default message
        ListView groupList = v.findViewById(R.id.listview_your_shared_obj);
        // set the item as always disabled (non clickable)
        ArrayAdapter<String> defMess = new ArrayAdapter<String>(this.requireContext(), R.layout.textview_group) {
            public boolean isEnabled(int position) {
                return false;
            }
        };
        defMess.add(getString(R.string.no_shared_objects));
        groupList.setAdapter(defMess);
    }

    private void postGetMySharedObjects(View v, List<APIResponse> resList){
        APIResponse res = resList.get(0);

        // Create myObjects
        try {
            if (res.responseCode == 200) {
                // OK
                if(res.jsonResponseArray != null){
                    // Construct the list
                    this.setMySharedObjectsListView(res.jsonResponseArray, v);
                }else{
                    // Generic error
                    this.showSnackBar(getString(R.string.server_error_generic), v);
                }

            } else if (res.responseCode == 401) {
                // Logout user, unauthorized
                MainActivity.logoutUser(this.requireActivity(), getString(R.string.authentication_error));

            }else if (res.responseCode != 404) {
                // Generic error
                this.showSnackBar(getString(R.string.server_error_generic), v);
            }
            // if 404 do nothing and go the finally clause
        }catch (JSONException e){
            e.printStackTrace();
            this.showSnackBar(getString(R.string.server_error_generic), v);
        }finally {
            // Show default list message if nothing is shown
            ListView lv = v.findViewById(R.id.listview_your_shared_obj);
            if(lv.getAdapter().isEmpty()) {
                this.showDefaultListMessage(v);
            }

            // Show the list
            showListView(v);
        }

    }

    private void setMySharedObjectsListView(JSONArray jArr, View v) throws JSONException{
        ListView sharedObjectsListView = v.findViewById(R.id.listview_your_shared_obj);
        List<MyObject> sharedObjects = new ArrayList<>();


        for(int i = 0; i<jArr.length(); i++){
            sharedObjects.add(MyObject.newFromJson(jArr.getJSONObject(i)));
        }

        ArrayAdapter<MyObject> sharedObjectsAdapter = new ArrayAdapter<>(this.requireContext(), R.layout.textview_group, sharedObjects);
        sharedObjectsListView.setAdapter(sharedObjectsAdapter);
    }

}