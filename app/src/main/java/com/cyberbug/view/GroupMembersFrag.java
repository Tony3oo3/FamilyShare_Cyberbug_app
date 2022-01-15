package com.cyberbug.view;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.ProgressBar;

import androidx.fragment.app.Fragment;

import com.cyberbug.R;
import com.cyberbug.api.APIRequest;
import com.cyberbug.api.APIResponse;
import com.cyberbug.api.AsyncRESTDispatcher;
import com.cyberbug.api.UIUpdaterResponse;
import com.cyberbug.api.UIUpdaterVoid;
import com.cyberbug.model.User;
import com.google.android.material.snackbar.Snackbar;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * A fragment that shows the group members inside the group page
 */
public class GroupMembersFrag extends Fragment {

    public GroupMembersFrag() {
    }

    public static GroupMembersFrag newInstance() {
        return new GroupMembersFrag();
    }

    private void populateMemberList(View v) {
        APIRequest apiRequest= MainActivity.fsAPI.getGroupMembersRequest(MainActivity.sData.selectedGroup.id);
        UIUpdaterVoid<View> preUpdater = new UIUpdaterVoid<>(v, this::showLoading);
        UIUpdaterResponse<View> postUpdater = new UIUpdaterResponse<>(v, this::searchUserFromId);
        new AsyncRESTDispatcher(preUpdater, postUpdater).execute(apiRequest);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_group_members, container, false);
        // ListView lv = v.findViewById(R.id.group_member_listview);
        // lv.setOnItemClickListener(this::onMenuItemClick);
        populateMemberList(v);
        return v;
    }

    private void populateAndShowMemberList(View v, List<APIResponse> resList) {
        ListView memberList = this.requireView().findViewById(R.id.group_member_listview);
        List<User> members = new ArrayList<>();

        boolean error = false;
        for (APIResponse res : resList) {
            try {
                if (res.responseCode == 200 && res.jsonResponse != null) {
                    String name = res.jsonResponse.getString("given_name");
                    String lastname = res.jsonResponse.getString("family_name");
                    members.add(new User(name,lastname));
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

        ArrayAdapter<User> memberAdapter = new ArrayAdapter<>(this.requireContext(), R.layout.textview_group, members);
        memberList.setAdapter(memberAdapter);

        showMemberListView(v);
    }

    private void searchUserFromId(View v, List<APIResponse> resList) {
        APIResponse res = resList.get(0);
        if (res.responseCode == 200 && res.jsonResponseArray != null) {
            // All ok
            // For each id we need to search the group with the API
            try {
                // Build the APIRequests to get the names of the groups
                APIRequest[] req = new APIRequest[res.jsonResponseArray.length()];
                for (int i = 0; i < res.jsonResponseArray.length(); i++) {
                    JSONObject gr = res.jsonResponseArray.getJSONObject(i);
                    String id = gr.getString("user_id");
                    req[i] = MainActivity.fsAPI.getUserProfileRequest(MainActivity.sData.authToken, id);
                }
                // Do nothing before the requests
                UIUpdaterVoid<?> preUpdater = new UIUpdaterVoid<>(null, (x) -> {});
                // Populate the UI list and change fragment
                UIUpdaterResponse<View> postUpdater = new UIUpdaterResponse<>(v, this::populateAndShowMemberList);
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
        this.showDefaultListMessage(v);
        if (res.responseCode != 404 && this.getView() != null) {
            // in this case there was an error
            Snackbar.make(this.getView(), getString(R.string.server_error_generic), Snackbar.LENGTH_LONG).show();
        }
    }

    private void showDefaultListMessage(View v) {
        // populate list with default message
        ListView groupList = this.requireView().findViewById(R.id.group_member_listview);
        // set the item as always disabled (non clickable)
        ArrayAdapter<String> defMess = new ArrayAdapter<String>(this.requireContext(), R.layout.textview_group) {
            public boolean isEnabled(int position) {
                return false;
            }
        };
        defMess.add(getString(R.string.no_group_member));
        groupList.setAdapter(defMess);

        // Hide the search bar
        if (getParentFragment() != null) getParentFragment().setHasOptionsMenu(false);
        showMemberListView(v);
    }

    // Set list fragment visible
    private void showMemberListView(View v){
        ProgressBar pr = v.findViewById(R.id.progressBar_groupMember);
        pr.setVisibility(View.GONE);
        ListView lw = v.findViewById(R.id.group_member_listview);
        lw.setVisibility(View.VISIBLE);
    }

    // Set loading fragment visible
    private void showLoading(View v) {
        ProgressBar pr = v.findViewById(R.id.progressBar_groupMember);
        pr.setVisibility(View.VISIBLE);
        ListView lw = v.findViewById(R.id.group_member_listview);
        lw.setVisibility(View.GONE);
    }

}