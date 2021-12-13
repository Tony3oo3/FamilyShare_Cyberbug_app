package com.cyberbug.view;

import android.content.DialogInterface;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.widget.SearchView;
import androidx.appcompat.widget.Toolbar;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
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
import com.cyberbug.functional.QuadConsumer;
import com.cyberbug.model.MyObject;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;

import org.json.JSONArray;
import org.json.JSONException;

import java.util.ArrayList;
import java.util.List;

public class MyObjectsFragment extends Fragment {

    private final boolean shareMode;
    private final boolean disableAddButton;
    private MyObject selectedObject;

    public MyObjectsFragment(boolean shareMode, boolean disableAddButton) {
        this.shareMode = shareMode;
        this.disableAddButton = disableAddButton;
    }

    public static MyObjectsFragment newInstance(boolean shareMode, boolean disableAddButton) {
        return new MyObjectsFragment(shareMode, disableAddButton);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View v = inflater.inflate(R.layout.fragment_my_objs, container, false);

        FloatingActionButton shareObj = v.findViewById(R.id.add_new_obj_button);
        if(!disableAddButton) {
            // Set the action button callback
            shareObj.setOnClickListener(this::onAddObjectClick);
        }else{
            shareObj.setVisibility(View.GONE);
        }

        HomeFragment.setOptionsMenu(this::createSearchActionMenu);

        // Set toolbar title
        Fragment parent = this.getParentFragment();
        if (parent != null) {
            // Check to avoid crash if the parent fragment does not exists (should not happen)
            Toolbar tBar = parent.requireView().findViewById(R.id.toolbar);
            tBar.setTitle(getString(R.string.my_objs));
        }

        // Populate the list
        populateSharedObject(v);

        // Set the item click listener
        ListView lv = v.findViewById(R.id.listview_my_objects);
        lv.setOnItemClickListener(this::onMenuItemClick);

        return v;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
    }

    public void onMenuItemClick(AdapterView<?> parent, View view, int position, long id) {
        // If a callback is set exec that else show object info
        if(shareMode){
            showDialogShareObject(parent, view, position, id);
        }else {
            Object clicked = parent.getItemAtPosition(position);
            if (clicked instanceof MyObject && HomeFragment.homeFragmentManager != null) {
                MyObject g = (MyObject) clicked;
                InfoObjFrag objFrag = InfoObjFrag.newInstance(null, g.id, null);
                HomeFragment.homeBackStack.add(this);
                HomeFragment.homeFragmentManager.beginTransaction().replace(R.id.home_fragment_container, objFrag).commit();
            }
        }
    }

    private void onAddObjectClick(View view) {
        AddObjFrag addObjFrag = AddObjFrag.newInstance(null);
        HomeFragment.homeBackStack.add(this);
        HomeFragment.homeFragmentManager.beginTransaction().replace(R.id.home_fragment_container, addObjFrag).commit();
    }


    private void populateSharedObject(View v){
        APIRequest myObjectsRequest = MainActivity.fsAPI.getMyObjectRequest(MainActivity.sData.authToken, MainActivity.sData.thisUserId);
        UIUpdaterVoid<View> preUpdater = new UIUpdaterVoid<>(v, this::showLoading);
        UIUpdaterResponse<View> postUpdater = new UIUpdaterResponse<>(v, this::postGetMySharedObjects);
        new AsyncRESTDispatcher(preUpdater, postUpdater).execute(myObjectsRequest);
    }

    private void showLoading(View v){
        ProgressBar pr = v.findViewById(R.id.progressBar_my_objects);
        pr.setVisibility(View.VISIBLE);
        ConstraintLayout lay = v.findViewById(R.id.my_objects_layout);
        lay.setVisibility(View.GONE);
    }

    private void showListView(View v){
        ProgressBar pr = v.findViewById(R.id.progressBar_my_objects);
        pr.setVisibility(View.GONE);
        ConstraintLayout lay = v.findViewById(R.id.my_objects_layout);
        lay.setVisibility(View.VISIBLE);
    }

    private void showSnackBar(String message, View v){
        if (this.getView() != null) {
            Snackbar.make(v, message, Snackbar.LENGTH_LONG).show();
        }
    }

    private void showDefaultListMessage(View v) {
        // populate list with default message
        ListView groupList = v.findViewById(R.id.listview_my_objects);
        // set the item as always disabled (non clickable)
        ArrayAdapter<String> defMess = new ArrayAdapter<String>(this.requireContext(), R.layout.textview_group) {
            public boolean isEnabled(int position) {
                return false;
            }
        };
        defMess.add(getString(R.string.no_object_inserted));
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
            ListView lv = v.findViewById(R.id.listview_my_objects);
            if(lv.getAdapter() == null || lv.getAdapter().isEmpty()) {
                this.showDefaultListMessage(v);
            }

            // Show the list
            showListView(v);
        }

    }

    private void setMySharedObjectsListView(JSONArray jArr, View v) throws JSONException{
        ListView sharedObjectsListView = v.findViewById(R.id.listview_my_objects);
        List<MyObject> sharedObjects = new ArrayList<>();


        for(int i = 0; i<jArr.length(); i++){
            sharedObjects.add(MyObject.newFromJson(jArr.getJSONObject(i)));
        }

        ArrayAdapter<MyObject> sharedObjectsAdapter = new ArrayAdapter<>(this.requireContext(), R.layout.textview_group, sharedObjects);
        sharedObjectsListView.setAdapter(sharedObjectsAdapter);
    }

    // Callback used if share is true
    private void showDialogShareObject(AdapterView<?> parent, View view, int position, long id){
        // Show dialog to ask if the user wants to share the object with the group
        Object clicked = parent.getItemAtPosition(position);
        if (clicked instanceof MyObject) {
            MyObject g = (MyObject) clicked;
            this.selectedObject = g;
            new AlertDialog.Builder(this.requireContext())
                    .setTitle(g.toString())
                    .setMessage(getString(R.string.sure_want_to_share))
                    .setIcon(android.R.drawable.ic_input_add)
                    .setPositiveButton(R.string.yes, this::shareObject)
                    .setNegativeButton(R.string.no, null).show();
        }
    }

    private void shareObject(DialogInterface dialogInterface, int i) {
        if(this.selectedObject != null && MainActivity.sData.selectedGroup != null){
            APIRequest shareObjRequest = MainActivity.fsAPI.shareObjectWithGroup(
                    MainActivity.sData.authToken,
                    MainActivity.sData.selectedGroup.id,
                    this.selectedObject.id
            );
            UIUpdaterVoid<View> preUpdater = new UIUpdaterVoid<>(this.requireView() ,(x) -> {});
            UIUpdaterResponse<View> postUpdate = new UIUpdaterResponse<>(this.requireView(), this::onPostShareObject);
            new AsyncRESTDispatcher(preUpdater, postUpdate).execute(shareObjRequest);
        }
    }

    private void onPostShareObject(View v, List<APIResponse> resList){
        APIResponse res = resList.get(0);
        if(res.responseCode == 200){
            // Show ok message
            this.showSnackBar(getString(R.string.object_shared) , v);
        }else if(res.responseCode == 401){
            MainActivity.logoutUser(this.requireActivity(), getString(R.string.user_not_authenticated));
        }else{
            // Generic error mesage
            this.showSnackBar(getString(R.string.server_error_generic), v);
        }
    }

    // Sets the search
    private void createSearchActionMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {
        inflater.inflate(R.menu.search_menu, menu);
        MenuItem menuItem = menu.findItem(R.id.menu_item_search);
        SearchView searchView = (SearchView) menuItem.getActionView();
        searchView.setQueryHint(getString(R.string.search_an_object));

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            private final ListView myObjects = MyObjectsFragment.this.requireView().findViewById(R.id.listview_my_objects);

            private void filterView(String s) {
                if (myObjects.getAdapter() instanceof ArrayAdapter) {
                    ArrayAdapter<?> ad = (ArrayAdapter<?>) myObjects.getAdapter();
                    ad.getFilter().filter(s);
                }
            }

            @Override
            public boolean onQueryTextSubmit(String query) {
                filterView(query);
                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                filterView(newText);
                return true;
            }
        });
    }

}