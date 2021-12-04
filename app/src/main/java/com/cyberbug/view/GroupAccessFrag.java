package com.cyberbug.view;

import android.content.DialogInterface;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.widget.SearchView;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;

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

import com.cyberbug.api.APIRequest;
import com.cyberbug.api.APIResponse;
import com.cyberbug.api.AsyncRESTDispatcher;
import com.cyberbug.api.UIUpdaterResponse;
import com.cyberbug.api.UIUpdaterVoid;
import com.cyberbug.model.Group;
import com.example.grafica.R;
import com.google.android.material.snackbar.Snackbar;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class GroupAccessFrag extends Fragment {

    private static GroupAccessFrag thisFrag = null;

    private Group tempSelectedGroup = null;

    public static GroupAccessFrag newInstance() {
        if (thisFrag == null)
            thisFrag = new GroupAccessFrag();
        return thisFrag;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_group_access, container, false);

        // Sets up search bar
        if (getParentFragment() instanceof HomeFragment) {
            HomeFragment parent = (HomeFragment) getParentFragment();
            parent.setOptionsMenu(this::createSearchActionMenu);
        }

        // Set toolbar title
        Fragment parent = this.getParentFragment();
        if (parent != null) {
            // Check to avoid crash if the parent fragment does not exists (should not happen)
            Toolbar tBar = parent.requireView().findViewById(R.id.toolbar);
            tBar.setTitle(getString(R.string.access_group));
        }


        ListView lv = v.findViewById(R.id.listview_access_group);
        lv.setOnItemClickListener(this::onMenuItemClickShowDialog);

        return v;
    }


    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        populateAllGroupList();
    }

    private void populateAllGroupList() {
        APIRequest getAllGroups = MainActivity.fsAPI.getAllGroupsRequest(MainActivity.sData.authToken);
        UIUpdaterVoid<FragmentActivity> preUpdater = new UIUpdaterVoid<>(this.requireActivity(), this::showLoading);
        UIUpdaterResponse<FragmentActivity> postUpdater = new UIUpdaterResponse<>(this.requireActivity(), this::insertAllGroupAndShowList);
        new AsyncRESTDispatcher(preUpdater, postUpdater).execute(getAllGroups);
    }

    private void showLoading(FragmentActivity act) {
        ProgressBar pr = act.findViewById(R.id.progressBar_access_group);
        pr.setVisibility(View.VISIBLE);
        ListView lw = act.findViewById(R.id.listview_access_group);
        lw.setVisibility(View.GONE);
    }

    private void insertAllGroupAndShowList(FragmentActivity act, List<APIResponse> resList) {
        APIResponse res = resList.get(0);
        if (res.responseCode == 200 && res.jsonResponseArray != null) {
            // OK
            ListView groupList = this.requireView().findViewById(R.id.listview_access_group);
            List<Group> allGroups = new ArrayList<>();

            for (int i = 0; i < res.jsonResponseArray.length(); i++) {
                try {
                    JSONObject g = res.jsonResponseArray.getJSONObject(i);
                    String name = g.getString("name");
                    String id = g.getString("group_id");
                    allGroups.add(new Group(id, name));
                }catch (JSONException e){
                    showGenericErrorSnackBar();
                }
            }

            // Set the ArrayAdapter
            ArrayAdapter<Group> myGroupsAdapter = new ArrayAdapter<>(this.requireContext(), R.layout.textview_group, allGroups);
            groupList.setAdapter(myGroupsAdapter);
            // Show the search bar
            if (getParentFragment() != null) getParentFragment().setHasOptionsMenu(true);
            showGroupListView(act);

        }else if(res.responseCode == 404){
            // No groups were found
            showDefaultListMessage(act);
            showGroupListView(act);
        }else{
            // An error occurred
            showDefaultListMessage(act);
            showGenericErrorSnackBar();
            showGroupListView(act);
        }
    }

    private void showGenericErrorSnackBar(){
        if(this.getView() != null){
            Snackbar.make(this.getView(), getString(R.string.server_error_generic), Snackbar.LENGTH_LONG).show();
        }
    }

    private void showDefaultListMessage(FragmentActivity act) {
        // populate list with default message
        ListView groupList = this.requireView().findViewById(R.id.listview_access_group);
        // set the item as always disabled (non clickable)
        ArrayAdapter<String> defMess = new ArrayAdapter<String>(this.requireContext(), R.layout.textview_group) {
            public boolean isEnabled(int position) {
                return false;
            }
        };
        defMess.add(getString(R.string.no_group_found));
        groupList.setAdapter(defMess);

        // Hide the search bar
        if (getParentFragment() != null) getParentFragment().setHasOptionsMenu(false);
        showGroupListView(act);
    }

    private void showGroupListView(FragmentActivity act){
        ProgressBar pr = act.findViewById(R.id.progressBar_access_group);
        pr.setVisibility(View.GONE);
        ListView lw = act.findViewById(R.id.listview_access_group);
        lw.setVisibility(View.VISIBLE);
    }

    private void createSearchActionMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {
        inflater.inflate(R.menu.search_menu, menu);
        MenuItem menuItem = menu.findItem(R.id.menu_item_search);
        SearchView searchView = (SearchView) menuItem.getActionView();
        searchView.setQueryHint(getString(R.string.search_a_group));

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            private final ListView allGroups = GroupAccessFrag.this.requireView().findViewById(R.id.listview_access_group);

            private void filterView(String s) {
                if (allGroups.getAdapter() instanceof ArrayAdapter) {
                    ArrayAdapter<?> ad = (ArrayAdapter<?>) allGroups.getAdapter();
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

    private void onMenuItemClickShowDialog(AdapterView<?> parent, View view, int position, long id){
        Object clicked = parent.getItemAtPosition(position);
        if (clicked instanceof Group) {
            Group g = (Group) clicked;
            this.tempSelectedGroup = g;
            new AlertDialog.Builder(this.requireContext())
                    .setTitle(getString(R.string.join_group))
                    .setMessage(getString(R.string.sure_want_to_join) + " " + g.toString() + "?")
                    .setIcon(android.R.drawable.ic_input_add)
                    .setPositiveButton(R.string.yes, this::handleJoinGroup)
                    .setNegativeButton(R.string.no, null).show();
        }
    }

    private void handleJoinGroup(DialogInterface dialog, int which){
        if(this.tempSelectedGroup != null){
            // Dismiss the dialog
            dialog.dismiss();
            // Join the group
            joinGroup(this.tempSelectedGroup);
        }
    }

    private void joinGroup(Group g){
        APIRequest joinSelectedGroup = MainActivity.fsAPI.joinGroupRequest(MainActivity.sData.authToken, MainActivity.sData.thisUserId, g.id);
        UIUpdaterVoid<FragmentActivity> preUpdater = new UIUpdaterVoid<>(this.requireActivity(), this::showLoading);
        UIUpdaterResponse<FragmentActivity> postUpdater = new UIUpdaterResponse<>(this.requireActivity(), this::onPostJoinRequest);
        new AsyncRESTDispatcher(preUpdater, postUpdater).execute(joinSelectedGroup);
    }

    private void onPostJoinRequest(FragmentActivity act, List<APIResponse> resList){
        APIResponse res = resList.get(0);
        if(res.responseCode == 200){
            // OK
            // TODO maybe redirect somewhere?
            showGroupListView(act);
            if(this.getView() != null){
                Snackbar.make(this.getView(), getString(R.string.group_join_dome), Snackbar.LENGTH_LONG).show();
            }
        }else if(res.responseCode == 401){
            // Auth error
            MainActivity.logoutUser(this.requireActivity(),getString(R.string.authentication_error));
        }else{
            // Generic error
            showGenericErrorSnackBar();
            showGroupListView(act);
        }
    }
}