package com.cyberbug.view;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
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

public class MyGroupsFragment extends Fragment {

    private static MyGroupsFragment thisFrag;

    public static MyGroupsFragment newInstance() {
        if (thisFrag == null)
            thisFrag = new MyGroupsFragment();
        return thisFrag;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_my_groups, container, false);

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
            tBar.setTitle(getString(R.string.my_groups));
        }

        ListView lv = v.findViewById(R.id.my_groups_listview);
        lv.setOnItemClickListener(this::onMenuItemClick);

        return v;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        populateGroupList();
    }


    public void onMenuItemClick(AdapterView<?> parent, View view, int position, long id) {
        Object clicked = parent.getItemAtPosition(position);
        if (clicked instanceof Group) {
            Group g = (Group) clicked;
            // TODO navigation to the specific group page
            GroupPageFrag groupPageFrag = GroupPageFrag.newInstance();
            FragmentManager fragmentManager = this.getParentFragmentManager();
            fragmentManager.popBackStack();
            fragmentManager.beginTransaction().replace(R.id.home_fragment_container, groupPageFrag).commit();
        }
    }


    private void populateGroupList() {
        // First we get all the group the users is in
        // Second we search each group using the id and we get the names
        // Third we can populate the UI list
        APIRequest getJoinedGroups = MainActivity.fsAPI.getJoinedGroupsRequest(MainActivity.sData.authToken, MainActivity.sData.thisUserId);
        UIUpdaterVoid<FragmentActivity> preUpdater = new UIUpdaterVoid<>(this.requireActivity(), this::showLoading);
        UIUpdaterResponse<FragmentActivity> postUpdater = new UIUpdaterResponse<>(this.requireActivity(), this::searchGroupFromId);
        new AsyncRESTDispatcher(preUpdater, postUpdater).execute(getJoinedGroups);
    }

    private void showLoading(FragmentActivity act) {
        ProgressBar pr = act.findViewById(R.id.progressBar_myGroups);
        pr.setVisibility(View.VISIBLE);
        ListView lw = act.findViewById(R.id.my_groups_listview);
        lw.setVisibility(View.GONE);
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
                UIUpdaterVoid<?> preUpdater = new UIUpdaterVoid<>(null, (x) -> {});
                // Populate the UI list and change fragment
                UIUpdaterResponse<FragmentActivity> postUpdater = new UIUpdaterResponse<>(act, this::populateAndShowGroupList);
                new AsyncRESTDispatcher(preUpdater, postUpdater).execute(req);
                return;
            } catch (JSONException e) {
                // Just to debug, user error feedback given below
                e.printStackTrace();
            }
        } else if (res.responseCode == 401) {
            MainActivity.logoutUser(this.requireActivity(),getString(R.string.authentication_error));
            return;
        }

        // no group found
        this.showDefaultListMessage(act);
        if (res.responseCode != 404 && this.getView() != null) {
            // in this case there was an error
            Snackbar.make(this.getView(), getString(R.string.server_error_generic), Snackbar.LENGTH_LONG).show();
        }
    }

    private void populateAndShowGroupList(FragmentActivity act, List<APIResponse> resList) {
        ListView groupList = this.requireView().findViewById(R.id.my_groups_listview);
        List<Group> myGroups = new ArrayList<>();

        boolean error = false;
        // For each response construct a Group object and add it to the Adapter
        // if some responses are not good then set the error flag
        for (APIResponse res : resList) {
            try {
                if (res.responseCode == 200 && res.jsonResponse != null) {
                    String name = res.jsonResponse.getString("name");
                    String id = res.jsonResponse.getString("group_id");
                    myGroups.add(new Group(id, name));
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

        ArrayAdapter<Group> myGroupsAdapter = new ArrayAdapter<>(this.requireContext(), R.layout.textview_group, myGroups);
        groupList.setAdapter(myGroupsAdapter);
        // Show the search bar
        if (getParentFragment() != null) getParentFragment().setHasOptionsMenu(true);
        showGroupListView(act);
    }

    private void showDefaultListMessage(FragmentActivity act) {
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
        if (getParentFragment() != null) getParentFragment().setHasOptionsMenu(false);
        showGroupListView(act);
    }

    private void showGroupListView(FragmentActivity act){
        ProgressBar pr = act.findViewById(R.id.progressBar_myGroups);
        pr.setVisibility(View.GONE);
        ListView lw = act.findViewById(R.id.my_groups_listview);
        lw.setVisibility(View.VISIBLE);
    }

    private void createSearchActionMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {
        inflater.inflate(R.menu.search_menu, menu);
        MenuItem menuItem = menu.findItem(R.id.menu_item_search);
        SearchView searchView = (SearchView) menuItem.getActionView();
        searchView.setQueryHint(getString(R.string.search_a_group));

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            private final ListView myGroups = MyGroupsFragment.this.requireView().findViewById(R.id.my_groups_listview);

            private void filterView(String s) {
                if (myGroups.getAdapter() instanceof ArrayAdapter) {
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