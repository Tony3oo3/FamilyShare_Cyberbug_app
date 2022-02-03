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
 * Fragment used to list lent and borrowed objects
 */
public class LoansFrag extends Fragment {

    private ListView lentObjs;
    private ListView borrowedObjs;

    public LoansFrag() {
        // Required empty public constructor
    }

    /**
     * Factory method that creates a new instance of this LoansFrag
     */
    public static LoansFrag newInstance() {
        return new LoansFrag();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        // Create fragment view
        View v = inflater.inflate(R.layout.fragment_loans, container, false);

        // Initialize fragment components
        lentObjs = v.findViewById(R.id.txt_lent_object);
        borrowedObjs = v.findViewById(R.id.txt_borrowed_object);

        lentObjs.setOnItemClickListener(this::onLentMenuItemClick);
        borrowedObjs.setOnItemClickListener(this::onBorrowedMenuItemClick);

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
    // Gets lent objects request
    private void populateLentTextView() {
        APIRequest getLentObjs = MainActivity.fsAPI.getUserLentObjectsRequest(MainActivity.sData.authToken, MainActivity.sData.thisUserId);
        UIUpdaterVoid<FragmentActivity> preUpdater = new UIUpdaterVoid<>(this.requireActivity(), this::showLoading);
        UIUpdaterResponse<FragmentActivity> postUpdater = new UIUpdaterResponse<>(this.requireActivity(), this::populateBorrowedTextView);
        new AsyncRESTDispatcher(preUpdater, postUpdater).execute(getLentObjs);
    }
    // Populates lentObjs listview and gets borrowed objects
    private void populateBorrowedTextView(FragmentActivity act, List<APIResponse> resList) {
        String error = "";
        List<MyObject> lent = new ArrayList<>();
        APIResponse res = resList.get(0);
        // if some responses are not good then set the error flag

        try {
            if (res.responseCode == 200) {
                if(res.jsonResponseArray != null) {
                    JSONArray jRes = res.jsonResponseArray;
                    for(int i = 0; i < jRes.length(); i++) {
                        lent.add(MyObject.newFromJson(jRes.getJSONObject(i)));
                    }
                }
                else{
                    error = getString(R.string.no_lent_obj);
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

        // Shows default message if there are no lent objects
        if(!lent.isEmpty()) {
            ArrayAdapter<MyObject> myLentObjectAdapter = new ArrayAdapter<>(this.requireContext(), R.layout.textview_group, lent);
            lentObjs.setAdapter(myLentObjectAdapter);
        } else {
            ArrayAdapter<String> defMess = new ArrayAdapter<String>(this.requireContext(), R.layout.textview_group) {
                public boolean isEnabled(int position) {
                    return false;
                }
            };
            defMess.add(getString(R.string.no_lent_obj));
            lentObjs.setAdapter(defMess);
        }

        APIRequest getBorrowedObjs = MainActivity.fsAPI.getUserBorrowedObjectsRequest(MainActivity.sData.authToken, MainActivity.sData.thisUserId);
        UIUpdaterVoid<FragmentActivity> preUpdater = new UIUpdaterVoid<>(null, (x) -> {});
        UIUpdaterResponse<FragmentActivity> postUpdater = new UIUpdaterResponse<>(this.requireActivity(), this::showBorrowedTextView);
        new AsyncRESTDispatcher(preUpdater, postUpdater).execute(getBorrowedObjs);
    }
    // Shows borrowed objects
    private void showBorrowedTextView(FragmentActivity act, List<APIResponse> resList) {
        String error = "";
        List<MyObject> borrowed = new ArrayList<>();
        APIResponse res = resList.get(0);

        // if some responses are not good then set the error flag
        try {
            if (res.responseCode == 200) {
                if(res.jsonResponseArray != null) {
                    JSONArray jRes = res.jsonResponseArray;
                    for(int i = 0; i < jRes.length(); i++) {
                        borrowed.add(MyObject.newFromJson(jRes.getJSONObject(i)));
                    }
                }
                else{
                    error = getString(R.string.no_borrowed_obj);
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

        // Shows default message if there are no borrowed objects
        if(!borrowed.isEmpty()) {
            ArrayAdapter<MyObject> myBorrowedObjectAdapter = new ArrayAdapter<>(this.requireContext(), R.layout.textview_group, borrowed);
            borrowedObjs.setAdapter(myBorrowedObjectAdapter);
        } else {
            ArrayAdapter<String> defMess = new ArrayAdapter<String>(this.requireContext(), R.layout.textview_group) {
                public boolean isEnabled(int position) {
                    return false;
                }
            };
            defMess.add(getString(R.string.no_borrowed_obj));
            borrowedObjs.setAdapter(defMess);
        }

        act.findViewById(R.id.loans_loading_layout).setVisibility(View.GONE);
        act.findViewById(R.id.loans_main_fragment).setVisibility(View.VISIBLE);
    }

    private void showLoading(FragmentActivity act) {
        act.findViewById(R.id.loans_main_fragment).setVisibility(View.GONE);
        act.findViewById(R.id.loans_loading_layout).setVisibility(View.VISIBLE);
    }
    // Lent listview object click
    public void onLentMenuItemClick(AdapterView<?> parent, View view, int position, long id) {
        Object clicked = parent.getItemAtPosition(position);
        if (HomeFragment.homeFragmentManager != null) {
            MyObject g = (MyObject) clicked;
            InfoObjFrag objFrag = InfoObjFrag.newInstance(g.id, InfoObjFrag.Mode.DEFAULT);
            HomeFragment.homeBackStack.add(this);
            HomeFragment.homeFragmentManager.beginTransaction().replace(R.id.home_fragment_container, objFrag).commit();
        }
    }
    // Borrowed listview object click
    public void onBorrowedMenuItemClick(AdapterView<?> parent, View view, int position, long id) {
        Object clicked = parent.getItemAtPosition(position);
        if (HomeFragment.homeFragmentManager != null) {
            MyObject g = (MyObject) clicked;
            InfoObjFrag objFrag = InfoObjFrag.newInstance(g.id, InfoObjFrag.Mode.RETURN);
            HomeFragment.homeBackStack.add(this);
            HomeFragment.homeFragmentManager.beginTransaction().replace(R.id.home_fragment_container, objFrag).commit();

        }
    }

}