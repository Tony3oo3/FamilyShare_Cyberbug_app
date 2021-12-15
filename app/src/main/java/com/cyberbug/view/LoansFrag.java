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

import com.cyberbug.api.APIRequest;
import com.cyberbug.api.APIResponse;
import com.cyberbug.api.AsyncRESTDispatcher;
import com.cyberbug.api.UIUpdaterResponse;
import com.cyberbug.api.UIUpdaterVoid;
import com.cyberbug.R;
import com.google.android.material.snackbar.Snackbar;

import org.json.JSONException;

import java.util.ArrayList;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link LoansFrag#newInstance} factory method to
 * create an instance of this fragment.
 */
public class LoansFrag extends Fragment {

    private static final String ARG_ERROR_MESSAGE = "errorMessage";
    private String errorMessage = null;

    private ListView lentObjs;
    private ListView borrowedObjs;

    public LoansFrag() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param errorMessage Parameter 1.
     * @return A new instance of fragment LoansFrag.
     */
    // TODO: Rename and change types and number of parameters
    public static LoansFrag newInstance(String errorMessage) {
        LoansFrag lf = new LoansFrag();
        Bundle args = new Bundle();
        args.putString(ARG_ERROR_MESSAGE, errorMessage);
        lf.setArguments(args);
        return lf;
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
        View v = inflater.inflate(R.layout.fragment_loans, container, false);

        // Initialize fragment components
        lentObjs = v.findViewById(R.id.txt_lent_object);
        borrowedObjs = v.findViewById(R.id.txt_borrowed_object);

        // Set fragment title
        Fragment parent = this.getParentFragment();
        if (parent != null) {
            // Check to avoid crash if the parent fragment does not exists (should not happen)
            Toolbar tBar = parent.requireView().findViewById(R.id.toolbar);
            tBar.setTitle(getString(R.string.obj_loans));
            parent.setHasOptionsMenu(false);
        }

        return v;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        populateLentTextView();
    }

    private void populateLentTextView() {
        APIRequest getLentObjs = MainActivity.fsAPI.getUserLentObjectsRequest(MainActivity.sData.authToken, MainActivity.sData.thisUserId);
        UIUpdaterVoid<FragmentActivity> preUpdater = new UIUpdaterVoid<>(this.requireActivity(), this::showLoading);
        UIUpdaterResponse<FragmentActivity> postUpdater = new UIUpdaterResponse<>(this.requireActivity(), this::populateBorrowedTextView);
        new AsyncRESTDispatcher(preUpdater, postUpdater).execute(getLentObjs);
    }

    private void populateBorrowedTextView(FragmentActivity act, List<APIResponse> resList) {
        String error = "";
        List<String> lent = new ArrayList<>();
        // if some responses are not good then set the error flag
        for (APIResponse res : resList) {
            try {
                if (res.responseCode == 200) {
                    if(res.jsonResponse != null) {
                        String name = res.jsonResponse.getString("object_name");
                        String id = res.jsonResponse.getString("object_id");
                        String owner = res.jsonResponse.getString("onwer");
                        lent.add("Dovrai restituire " + name + " a " + owner);
                    }
                    else{
                        error = getString(R.string.no_lent_objs);
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
        ArrayAdapter<String> myLentObjectAdapter = new ArrayAdapter<>(this.requireContext(), R.layout.textview_group, lent);
        lentObjs.setAdapter(myLentObjectAdapter);

        APIRequest getBorrowedObjs = MainActivity.fsAPI.getUserBorrowedObjectsRequest(MainActivity.sData.authToken, MainActivity.sData.thisUserId);
        UIUpdaterVoid<FragmentActivity> preUpdater = new UIUpdaterVoid<>(null, (x) -> {});
        UIUpdaterResponse<FragmentActivity> postUpdater = new UIUpdaterResponse<>(this.requireActivity(), this::showBorrowedTextView);
        new AsyncRESTDispatcher(preUpdater, postUpdater).execute(getBorrowedObjs);
    }

    private void showBorrowedTextView(FragmentActivity act, List<APIResponse> resList) {
        String error = "";
        List<String> borrowed = new ArrayList<>();
        // if some responses are not good then set the error flag
        for (APIResponse res : resList) {
            try {
                if (res.responseCode == 200) {
                    if(res.jsonResponse != null) {
                        String name = res.jsonResponse.getString("object_name");
                        String id = res.jsonResponse.getString("object_id");
                        String owner = res.jsonResponse.getString("onwer");
                        borrowed.add("Dovrai restituire " + name + " a " + owner);
                    }
                    else{
                        error = getString(R.string.no_borrowed_objs);
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
        ArrayAdapter<String> myBorrowedObjectAdapter = new ArrayAdapter<>(this.requireContext(), R.layout.textview_group, borrowed);
        borrowedObjs.setAdapter(myBorrowedObjectAdapter);

        act.findViewById(R.id.loans_loading_layout).setVisibility(View.GONE);
        act.findViewById(R.id.loans_main_fragment).setVisibility(View.VISIBLE);
    }

    private void showLoading(FragmentActivity act) {
        act.findViewById(R.id.loans_main_fragment).setVisibility(View.GONE);
        act.findViewById(R.id.loans_loading_layout).setVisibility(View.VISIBLE);
    }

}