package com.cyberbug.view;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;

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

/**
 * The fragment that handles the objects requests
 */
public class RequestsFrag extends Fragment {

    private ListView incomeRequestsListView;
    private ListView outcomeRequestsListView;

    public static RequestsFrag newInstance() {
        return new RequestsFrag();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Create fragment view
        View v = inflater.inflate(R.layout.fragment_requests, container, false);

        // Initialize fragment components
        // TextView incomeRequestTitle = v.findViewById(R.id.income_requests_title);
        // TextView outcomeRequestTitle = v.findViewById(R.id.outcomes_requests_title);

        incomeRequestsListView = v.findViewById(R.id.income_requests_list);
        outcomeRequestsListView = v.findViewById(R.id.outcome_requests_list);

        // Set fragment title
        Fragment parent = this.getParentFragment();
        if (parent != null) {
            // Check to avoid crash if the parent fragment does not exists (should not happen)
            Toolbar tBar = parent.requireView().findViewById(R.id.toolbar);
            tBar.setTitle(getString(R.string.my_requests));
            parent.setHasOptionsMenu(false);
        }

        incomeRequestsListView.setOnItemClickListener(this::onMenuItemClick);
        return v;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        populateIncomingTextView();
    }

    private void populateIncomingTextView() {
        APIRequest getIncomeRequests = MainActivity.fsAPI.getIncomingRequestsObj(MainActivity.sData.authToken, MainActivity.sData.thisUserId);
        UIUpdaterVoid<FragmentActivity> preUpdater = new UIUpdaterVoid<>(this.requireActivity(), this::showLoading);
        UIUpdaterResponse<FragmentActivity> postUpdater = new UIUpdaterResponse<>(this.requireActivity(), this::populateIncomeRequestsList);
        new AsyncRESTDispatcher(preUpdater, postUpdater).execute(getIncomeRequests);
    }

    private void showLoading(FragmentActivity act) {
        act.findViewById(R.id.requests_main_layout).setVisibility(View.GONE);
        act.findViewById(R.id.progressBar_Requests).setVisibility(View.VISIBLE);
    }

    private void populateIncomeRequestsList(FragmentActivity act, List<APIResponse> resList) {
        String error = "";
        List<MyObject> inRequests = new ArrayList<>();
        APIResponse res = resList.get(0);
        // if some responses are not good then set the error flag

        try {
            if (res.responseCode == 200) {
                if (res.jsonResponseArray != null) {
                    JSONArray jRes = res.jsonResponseArray;
                    for(int i = 0; i < jRes.length(); i++) {
                        inRequests.add(MyObject.newFromJson(jRes.getJSONObject(i)));
                    }
                } else {
                    error = getString(R.string.no_requests);
                }
            } else {
                error = getString(R.string.server_error_generic);
            }
        } catch (JSONException e) {
            e.printStackTrace();
            error = getString(R.string.server_error_generic);
        }


        if (!error.equals("") && this.getView() != null) {
            Snackbar.make(this.getView(), error, Snackbar.LENGTH_LONG).show();
        }
        if(!inRequests.isEmpty()) {
            ArrayAdapter<MyObject> myIncomeRequestsAdapter = new ArrayAdapter<>(this.requireContext(), R.layout.textview_group, inRequests);
            incomeRequestsListView.setAdapter(myIncomeRequestsAdapter);
        } else {
            ArrayAdapter<String> defMess = new ArrayAdapter<String>(this.requireContext(), R.layout.textview_group) {
                public boolean isEnabled(int position) {
                    return false;
                }
            };
            defMess.add(getString(R.string.no_in_requests));
            incomeRequestsListView.setAdapter(defMess);
        }

        APIRequest getBorrowedObjs = MainActivity.fsAPI.getOutgoingRequestsObj(MainActivity.sData.authToken, MainActivity.sData.thisUserId);
        UIUpdaterVoid<FragmentActivity> preUpdater = new UIUpdaterVoid<>(null, (x) -> {});
        UIUpdaterResponse<FragmentActivity> postUpdater = new UIUpdaterResponse<>(this.requireActivity(), this::showOutcomeRequestsList);
        new AsyncRESTDispatcher(preUpdater, postUpdater).execute(getBorrowedObjs);
    }

    private void showOutcomeRequestsList(FragmentActivity act, List<APIResponse> resList) {
        String error = "";
        List<MyObject> outRequests = new ArrayList<>();
        APIResponse res = resList.get(0);
        // if some responses are not good then set the error flag
        try {
            if (res.responseCode == 200) {
                if (res.jsonResponseArray != null) {
                    JSONArray jRes = res.jsonResponseArray;
                    for(int i = 0; i < jRes.length(); i++) {
                        outRequests.add(MyObject.newFromJson(jRes.getJSONObject(i)));
                    }
                } else {
                    error = getString(R.string.no_requests);
                }
            } else {
                error = getString(R.string.server_error_generic);
            }
        } catch (JSONException e) {
            e.printStackTrace();
            error = getString(R.string.server_error_generic);
        }


        if (!error.equals("") && this.getView() != null) {
            Snackbar.make(this.getView(), error, Snackbar.LENGTH_LONG).show();
        }
        if(!outRequests.isEmpty()) {
            ArrayAdapter<MyObject> myOutcomeRequestsAdapter = new ArrayAdapter<>(this.requireContext(), R.layout.textview_group, outRequests);
            outcomeRequestsListView.setAdapter(myOutcomeRequestsAdapter);
        } else {
            ArrayAdapter<String> defMess = new ArrayAdapter<String>(this.requireContext(), R.layout.textview_group) {
                public boolean isEnabled(int position) {
                    return false;
                }
            };
            defMess.add(getString(R.string.no_out_requests));
            outcomeRequestsListView.setAdapter(defMess);
        }

        act.findViewById(R.id.progressBar_Requests).setVisibility(View.GONE);
        act.findViewById(R.id.requests_main_layout).setVisibility(View.VISIBLE);
    }

    public void onMenuItemClick(AdapterView<?> parent, View view, int position, long id) {
        Object clicked = parent.getItemAtPosition(position);
        if (HomeFragment.homeFragmentManager != null) {
            MyObject g = (MyObject) clicked;
            InfoObjFrag objFrag = InfoObjFrag.newInstance(g.id, InfoObjFrag.Mode.ACCEPT_REQ);
            HomeFragment.homeBackStack.add(this);
            HomeFragment.homeFragmentManager.beginTransaction().replace(R.id.home_fragment_container, objFrag).commit();

        }
    }
}