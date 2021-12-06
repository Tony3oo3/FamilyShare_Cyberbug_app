package com.cyberbug.view;

import android.os.Bundle;

import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import com.cyberbug.api.APIRequest;
import com.cyberbug.api.AsyncRESTDispatcher;
import com.cyberbug.api.FSAPIWrapper;
import com.cyberbug.api.UIUpdaterResponse;
import com.cyberbug.api.UIUpdaterVoid;
import com.example.grafica.R;
import com.google.android.material.snackbar.Snackbar;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link AddObjFrag#newInstance} factory method to
 * create an instance of this fragment.
 */
public class AddObjFrag extends Fragment {

    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_ERROR_MESSAGE = "errorMessage";
    private String errorMessage = null;

    private EditText objName;
    private EditText objDesc;

    public AddObjFrag() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param errorMessage Parameter 1.
     * @return A new instance of fragment AddObjFrag.
     */
    // TODO: Rename and change types and number of parameters
    public static AddObjFrag newInstance(String errorMessage) {
        AddObjFrag aof = new AddObjFrag();
        Bundle args = new Bundle();
        args.putString(ARG_ERROR_MESSAGE, errorMessage);
        aof.setArguments(args);
        return aof;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        // Check for args
        if (getArguments() != null) {
            errorMessage = getArguments().getString(ARG_ERROR_MESSAGE);
        }
        // Create fragment view
        View v = inflater.inflate(R.layout.fragment_add_obj, container, false);

        // Initialize fragment components
        objName = v.findViewById(R.id.txt_obj_name);
        objDesc = v.findViewById(R.id.txt_obj_desc);

        // Set buttons listeners
        Button cancObj = v.findViewById(R.id.btn_canc_obj_add);
        Button addObj = v.findViewById(R.id.btn_obj_add);

        // Set fragment title
        Fragment parent = this.getParentFragment();
        if (parent != null) {
            // Check to avoid crash if the parent fragment does not exists (should not happen)
            Toolbar tBar = parent.requireView().findViewById(R.id.toolbar);
            tBar.setTitle(getString(R.string.add_obj));
            parent.setHasOptionsMenu(false);
        }

        return v;
    }

    public void onClickCancelButton( View v){
        // Clear the TextViews
        objName.getText().clear();
        objDesc.getText().clear();
    }

    public void onClickAddObjButton( View v){
        // Get text
        String name = objName.getText().toString();
        String desc = objDesc.getText().toString();

        // Error handling
        if(name.isEmpty() || desc.isEmpty()) {
            Snackbar.make(this.requireView(), getString(R.string.insert_required_fields), Snackbar.LENGTH_LONG).show();
            return;
        }

        // All is ok
        // Send the request to the serve
        /*FSAPIWrapper.NewGroupInfo group = new FSAPIWrapper.NewGroupInfo(description, location, name, "true", MainActivity.sData.thisUserId, null, null);
        UIUpdaterVoid<FragmentActivity> preUpdater = new UIUpdaterVoid<>(this.requireActivity(), CreateGroupFrag::onPreCreateGroupRequest);
        UIUpdaterResponse<FragmentActivity> postUpdater = new UIUpdaterResponse<>(this.requireActivity(), this::onPostCreateGroupRequest);
        APIRequest req = MainActivity.fsAPI.createGroupRequest(MainActivity.sData.authToken, group);
        new AsyncRESTDispatcher(preUpdater, postUpdater).execute(req);*/

    }
}