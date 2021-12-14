package com.cyberbug.view;

import android.os.Bundle;

import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;

import android.text.Layout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.ProgressBar;

import com.cyberbug.R;
import com.cyberbug.api.APIRequest;
import com.cyberbug.api.APIResponse;
import com.cyberbug.api.AsyncRESTDispatcher;
import com.cyberbug.api.UIUpdaterResponse;
import com.cyberbug.api.UIUpdaterVoid;
import com.cyberbug.model.MyObject;
import com.google.android.material.snackbar.Snackbar;

import org.json.JSONArray;
import org.json.JSONException;

import java.util.ArrayList;
import java.util.List;

public class GroupBoardFrag extends Fragment {

    private final GroupPageFrag parentFrag;

    public GroupBoardFrag(GroupPageFrag parent){
        this.parentFrag = parent;
    }

    public static GroupBoardFrag newInstance(GroupPageFrag parent) {
        return new GroupBoardFrag(parent);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_group_board, container, false);

        // Populate the list
        populateBoard(v);

        // Set the item click listener
        ListView lv = v.findViewById(R.id.listview_board);
        lv.setOnItemClickListener(this::onMenuItemClick);

        return v;
    }

    private void populateBoard(View v){
        APIRequest sharedObjectsRequest = MainActivity.fsAPI.getSharedGroupObjectsRequest(MainActivity.sData.authToken, MainActivity.sData.selectedGroup.id);
        UIUpdaterVoid<View> preUpdater = new UIUpdaterVoid<>(v, this::showLoading);
        UIUpdaterResponse<View> postUpdater = new UIUpdaterResponse<>(v, this::postGetBoard);
        new AsyncRESTDispatcher(preUpdater, postUpdater).execute(sharedObjectsRequest);
    }

    private void postGetBoard(View v, List<APIResponse> resList){
        APIResponse res = resList.get(0);

        // Create myObjects
        try {
            if (res.responseCode == 200) {
                // OK
                if(res.jsonResponseArray != null){
                    // Construct the list
                    this.setBoardListView(res.jsonResponseArray, v);
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
            ListView lv = v.findViewById(R.id.listview_board);
            if(lv.getAdapter() == null || lv.getAdapter().isEmpty()) {
                this.showDefaultListMessage(v);
            }

            // Show the list
            showListView(v);
        }

    }

    private void setBoardListView(JSONArray jArr, View v) throws JSONException{
        ListView sharedObjectsListView = v.findViewById(R.id.listview_board);
        List<MyObject> sharedObjects = new ArrayList<>();


        for(int i = 0; i<jArr.length(); i++){
            sharedObjects.add(MyObject.newFromJson(jArr.getJSONObject(i)));
        }

        ArrayAdapter<MyObject> sharedObjectsAdapter = new ArrayAdapter<>(this.requireContext(), R.layout.textview_group, sharedObjects);
        sharedObjectsListView.setAdapter(sharedObjectsAdapter);
    }

    private void showSnackBar(String message, View v){
        if (this.getView() != null) {
            Snackbar.make(v, message, Snackbar.LENGTH_LONG).show();
        }
    }

    public void onMenuItemClick(AdapterView<?> parent, View view, int position, long id) {
        Object clicked = parent.getItemAtPosition(position);
        if (clicked instanceof MyObject && HomeFragment.homeFragmentManager != null) {
            MyObject g = (MyObject) clicked;
            InfoObjFrag objFrag = InfoObjFrag.newInstance(g.id, InfoObjFrag.Mode.LOAN);
            HomeFragment.homeBackStack.add(this.parentFrag);
            HomeFragment.homeFragmentManager.beginTransaction().replace(R.id.home_fragment_container, objFrag).commit();
        }
    }

    private void showDefaultListMessage(View v) {
        // populate list with default message
        ListView groupList = v.findViewById(R.id.listview_board);
        // set the item as always disabled (non clickable)
        ArrayAdapter<String> defMess = new ArrayAdapter<String>(this.requireContext(), R.layout.textview_group) {
            public boolean isEnabled(int position) {
                return false;
            }
        };
        defMess.add(getString(R.string.empty_board));
        groupList.setAdapter(defMess);
    }

    private void showLoading(View v){
        ProgressBar pr = v.findViewById(R.id.progressBar_board);
        pr.setVisibility(View.VISIBLE);
        ConstraintLayout l = v.findViewById(R.id.layout_board);
        l.setVisibility(View.GONE);
    }

    private void showListView(View v){
        ProgressBar pr = v.findViewById(R.id.progressBar_board);
        pr.setVisibility(View.GONE);
        ConstraintLayout l = v.findViewById(R.id.layout_board);
        l.setVisibility(View.VISIBLE);
    }

}