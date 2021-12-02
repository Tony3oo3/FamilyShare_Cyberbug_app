package com.cyberbug.view;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.widget.SearchView;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.cyberbug.api.APIRequest;
import com.cyberbug.api.APIResponse;
import com.cyberbug.api.AsyncRESTDispatcher;
import com.cyberbug.api.UIUpdaterResponse;
import com.cyberbug.api.UIUpdaterVoid;
import com.cyberbug.model.Group;
import com.example.grafica.R;
import com.google.android.material.navigation.NavigationView;
import com.google.android.material.snackbar.Snackbar;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;

public class MyGroupsFragment extends Fragment {

    public static MyGroupsFragment newInstance() {
        return new MyGroupsFragment();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_my_groups, container, false);

        // Sets up search bar
        if(getParentFragment() instanceof HomeFragment){
            HomeFragment parent = (HomeFragment) getParentFragment();
            parent.setOptionsMenu(this::createSearchActionMenu);
        }

        // Set toolbar title
        Fragment parent = this.getParentFragment();
        if (parent != null) {
            // Check to avoid crash if the parent fragment does not exists (should not happen)
            Toolbar tBar = parent.requireView().findViewById(R.id.toolbar);
            tBar.setTitle(getString(R.string.my_groups));
        }

        populateGroupList();
        ListView groupList = v.findViewById(R.id.my_groups_listview);
        groupList.setOnItemClickListener(this::onMenuItemClick);

        return v;
    }

    public void onMenuItemClick(AdapterView<?> parent, View view, int position, long id) {
        Object clicked = parent.getItemAtPosition(position);
        if(clicked instanceof Group){
            Group g = (Group) clicked;
            // TODO navigation to the specific group page
            new AlertDialog.Builder(this.requireContext())
                    .setTitle("TODO")
                    .setMessage(g.toString())
                    .setIcon(android.R.drawable.ic_dialog_alert)
                    .setPositiveButton(R.string.yes, null)
                    .setNegativeButton(R.string.no, null).show();
        }
    }


    private void populateGroupList() {
        // First we get all the group the users is in
        // Second we search each group using the id and we get the names
        // Third we can populate the UI list
        APIRequest getJoinedGroups = MainActivity.fsAPI.getJoinedGroupsRequest(MainActivity.sData.authToken, MainActivity.sData.thisUserId);
        UIUpdaterVoid<FragmentActivity> preUpdater = new UIUpdaterVoid<>(this.requireActivity(), MyGroupsFragment::showLoadingFragment);
        UIUpdaterResponse<FragmentActivity> postUpdater = new UIUpdaterResponse<>(this.requireActivity(), this::searchGroupFromId);
        new AsyncRESTDispatcher(preUpdater, postUpdater).execute(getJoinedGroups);
    }

    private static void showLoadingFragment(FragmentActivity act) {
        LoadingFragment loadingFragment = LoadingFragment.newInstance();
        FragmentTransaction fragTrans = act.getSupportFragmentManager().beginTransaction();
        fragTrans.replace(R.id.home_fragment_container, loadingFragment);
        fragTrans.commit();
    }

    // Callback called with APIResponse containing all joined group ids
    private void searchGroupFromId(FragmentActivity act, List<APIResponse> resList) {
        APIResponse res = resList.get(0);
        if (res.responseCode == 200 && res.jsonResponseArray != null) {
            // All ok
            // For each id we need to search the group with the API
            try {
                // Build the APIRequests to get the names of the groups
                APIRequest[] req = new APIRequest[res.jsonResponseArray.length()];
                for (int i = 0; i < res.jsonResponseArray.length(); i++) {
                    JSONObject gr = res.jsonResponseArray.getJSONObject(i);
                    String id = gr.getString("group_id");
                    req[i] = MainActivity.fsAPI.getGroupByIdRequest(id);
                }
                // Do nothing before the requests
                UIUpdaterVoid<?> preUpdater = new UIUpdaterVoid<>(null, (x) -> {
                });
                // Populate the UI list and change fragment
                UIUpdaterResponse<?> postUpdater = new UIUpdaterResponse<>(null, this::populateAndShowGroupList);
                new AsyncRESTDispatcher(preUpdater, postUpdater).execute(req);
                return;
            } catch (JSONException e) {
                // Just to debug, user error feedback given below
                e.printStackTrace();
            }
        } else if (res.responseCode == 401) {
            MainActivity.logoutUser(this.requireActivity().getSupportFragmentManager(), getString(R.string.authentication_error));
            return;
        }

        // no group found
        this.showDefaultListMessage();
        if (res.responseCode != 404 && this.getView() != null) {
            // in this case there was an error
            Snackbar.make(this.getView(), getString(R.string.server_error_generic), Snackbar.LENGTH_LONG).show();
        }
    }

    // First arg is not used in this callback because it require this fragment
    private void populateAndShowGroupList(Object nullObj, List<APIResponse> resList) {
        ListView groupList = this.requireView().findViewById(R.id.my_groups_listview);
        ArrayAdapter<Group> groups = new ArrayAdapter<>(this.requireContext(), R.layout.textview_group);
        boolean error = false;
        // For each response construct a Group object and add it to the Adapter
        // if some responses are not good then set the error flag
        for (APIResponse res : resList) {
            try {
                if (res.responseCode == 200 && res.jsonResponse != null) {
                    String name = res.jsonResponse.getString("name");
                    String id = res.jsonResponse.getString("group_id");
                    groups.add(new Group(id, name));
                } else {
                    error = true;
                }
            } catch (JSONException e) {
                e.printStackTrace();
                error = true;
            }
        }

        if (error && this.getView() != null) {
            Snackbar.make(this.getView(), getString(R.string.server_error_generic), Snackbar.LENGTH_LONG).show();
        }

        groupList.setAdapter(groups);
        // Show the search bar
        if(getParentFragment() != null) getParentFragment().setHasOptionsMenu(true);
        // Revert the fragment
        FragmentManager fragMan = this.requireActivity().getSupportFragmentManager();
        fragMan.beginTransaction().replace(R.id.home_fragment_container, this).commit();
    }

    private void showDefaultListMessage() {
        // populate list with default message
        ListView groupList = this.requireView().findViewById(R.id.my_groups_listview);
        // set the item as always disabled (non clickable)
        ArrayAdapter<String> defMess = new ArrayAdapter<String>(this.requireContext(), R.layout.textview_group) {
            public boolean isEnabled(int position) {
                return false;
            }
        };
        defMess.add(getString(R.string.no_group_joined));
        groupList.setAdapter(defMess);

        // Hide the search bar
        if(getParentFragment() != null) getParentFragment().setHasOptionsMenu(false);
        // Revert the fragment
        FragmentManager fragMan = this.requireActivity().getSupportFragmentManager();
        fragMan.beginTransaction().replace(R.id.home_fragment_container, this).commit();
    }

    private void createSearchActionMenu(@NonNull Menu menu, @NonNull MenuInflater inflater){
        inflater.inflate(R.menu.search_menu, menu);
        MenuItem menuItem = menu.findItem(R.id.menu_item_search);
        SearchView searchView = (SearchView) menuItem.getActionView();
        searchView.setQueryHint(getString(R.string.search_a_group));

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            private final ListView myGroups = MyGroupsFragment.this.requireView().findViewById(R.id.my_groups_listview);

            private void filterView(String s){
                if(myGroups.getAdapter() instanceof ArrayAdapter){
                    ArrayAdapter<?> ad = (ArrayAdapter<?>) myGroups.getAdapter();
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