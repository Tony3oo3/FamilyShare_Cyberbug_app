package com.cyberbug.view;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.cyberbug.R;
import com.cyberbug.api.APIRequest;
import com.cyberbug.api.APIResponse;
import com.cyberbug.api.AsyncRESTDispatcher;
import com.cyberbug.api.UIUpdaterResponse;
import com.cyberbug.api.UIUpdaterVoid;
import com.google.android.material.snackbar.Snackbar;

import org.json.JSONException;

import java.util.ArrayList;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link RequestsFrag#newInstance} factory method to
 * create an instance of this fragment.
 */
public class RequestsFrag extends Fragment {

    private static final String ARG_ERROR_MESSAGE = "errorMessage";
    private String errorMessage = null;

    private TextView incomeRequestTitle;
    private TextView outcomeRequestTitle;
    private ListView incomeRequestsListView;
    private ListView outcomeRequestsListView;

    public RequestsFrag() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @return A new instance of fragment RequestsFrag.
     */
    public static RequestsFrag newInstance(String errorMessage) {
        RequestsFrag rf = new RequestsFrag();
        Bundle args = new Bundle();
        args.putString(ARG_ERROR_MESSAGE, errorMessage);
        rf.setArguments(args);
        return rf;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Check for args
        if (getArguments() != null) {
            errorMessage = getArguments().getString(ARG_ERROR_MESSAGE);
        }
        // Create fragment view
        View v = inflater.inflate(R.layout.fragment_requests, container, false);

        // Initialize fragment components
        incomeRequestTitle = v.findViewById(R.id.income_requests_title);
        incomeRequestsListView = v.findViewById(R.id.income_requests_list);

        outcomeRequestTitle = v.findViewById(R.id.outcomes_requests_title);
        outcomeRequestsListView = v.findViewById(R.id.outcome_requests_list);

        // Set fragment title
        Fragment parent = this.getParentFragment();
        if (parent != null) {
            // Check to avoid crash if the parent fragment does not exists (should not happen)
            Toolbar tBar = parent.requireView().findViewById(R.id.toolbar);
            tBar.setTitle(getString(R.string.my_requests));
            parent.setHasOptionsMenu(false);
        }

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
        List<String> outRequests = new ArrayList<>();
        // if some responses are not good then set the error flag
        for (APIResponse res : resList) {
            try {
                if (res.responseCode == 200) {
                    if (res.jsonResponse != null) {
                        String name = res.jsonResponse.getString("object_name");
                        String id = res.jsonResponse.getString("object_id");
                        String user = res.jsonResponse.getString("user_id");
                        outRequests.add("Richiesta di " + name + " da " + user);
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
        }

        if (!error.equals("") && this.getView() != null) {
            Snackbar.make(this.getView(), error, Snackbar.LENGTH_LONG).show();
        }
        ArrayAdapter<String> myIncomeRequestsAdapter = new ArrayAdapter<>(this.requireContext(), R.layout.textview_group, outRequests);
        incomeRequestsListView.setAdapter(myIncomeRequestsAdapter);

        APIRequest getBorrowedObjs = MainActivity.fsAPI.getOutgoingRequestsObj(MainActivity.sData.authToken, MainActivity.sData.thisUserId);
        UIUpdaterVoid<FragmentActivity> preUpdater = new UIUpdaterVoid<>(null, (x) -> {});
        UIUpdaterResponse<FragmentActivity> postUpdater = new UIUpdaterResponse<>(this.requireActivity(), this::showOutcomeRequestsList);
        new AsyncRESTDispatcher(preUpdater, postUpdater).execute(getBorrowedObjs);
    }

    private void showOutcomeRequestsList(FragmentActivity act, List<APIResponse> resList) {
        String error = "";
        List<String> outRequests = new ArrayList<>();
        // if some responses are not good then set the error flag
        for (APIResponse res : resList) {
            try {
                if (res.responseCode == 200) {
                    if (res.jsonResponse != null) {
                        String name = res.jsonResponse.getString("object_name");
                        String id = res.jsonResponse.getString("object_id");
                        String owner = res.jsonResponse.getString("owner");
                        String user = res.jsonResponse.getString("user_id");
                        outRequests.add("Richiesta di " + name + " a " + owner);
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
        }

        if (!error.equals("") && this.getView() != null) {
            Snackbar.make(this.getView(), error, Snackbar.LENGTH_LONG).show();
        }
        ArrayAdapter<String> myOutcomeRequestsAdapter = new ArrayAdapter<>(this.requireContext(), R.layout.textview_group, outRequests);
        outcomeRequestsListView.setAdapter(myOutcomeRequestsAdapter);

        act.findViewById(R.id.progressBar_Requests).setVisibility(View.GONE);
        act.findViewById(R.id.requests_main_layout).setVisibility(View.VISIBLE);
    }

}