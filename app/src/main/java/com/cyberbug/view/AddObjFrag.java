package com.cyberbug.view;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentManager;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import com.cyberbug.api.APIRequest;
import com.cyberbug.api.APIResponse;
import com.cyberbug.api.AsyncRESTDispatcher;
import com.cyberbug.api.FSAPIWrapper;
import com.cyberbug.api.UIUpdaterResponse;
import com.cyberbug.api.UIUpdaterVoid;
import com.cyberbug.R;
import com.google.android.material.snackbar.Snackbar;

import java.util.List;

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
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
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
        cancObj.setOnClickListener(this::onClickCancelButton);
        Button addObj = v.findViewById(R.id.btn_obj_add);
        addObj.setOnClickListener(this::onClickAddObjButton);

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
        FSAPIWrapper.ObjectData obj = new FSAPIWrapper.ObjectData(name, desc, MainActivity.sData.thisUserId);
        UIUpdaterVoid<FragmentActivity> preUpdater = new UIUpdaterVoid<>(this.requireActivity(), AddObjFrag::onPreAddObjectRequest);
        UIUpdaterResponse<FragmentActivity> postUpdater = new UIUpdaterResponse<>(this.requireActivity(), this::onPostAddObjectRequest);
        APIRequest req = MainActivity.fsAPI.insertObjectRequest(MainActivity.sData.authToken, MainActivity.sData.thisUserId ,obj);
        new AsyncRESTDispatcher(preUpdater, postUpdater).execute(req);

    }
    private static void onPreAddObjectRequest(FragmentActivity activity) {
        // Changes to LoadingFragment

        activity.findViewById(R.id.add_object_main_layout).setVisibility(View.GONE);
        activity.findViewById(R.id.add_object_loading_layout).setVisibility(View.VISIBLE);
    }

    private void onPostAddObjectRequest(FragmentActivity activity, List<APIResponse> responseList) {
        APIResponse res = responseList.get(0);
        // 200 ok
        // 400 bad request
        // 401 user not authenticated or unauthorized
        // else server error
        if (res.responseCode == 200) {
            //this.returnToMyObjects(getString(R.string.obj_addition_success));
            errorMessage = getString(R.string.obj_addition_success);
        } else {
            // some error occurred, return to the fragment and show a snack bar
            //FragmentTransaction fragmentTransaction = activity.getSupportFragmentManager().beginTransaction();


            switch (res.responseCode){
                case 400:
                    errorMessage = getString(R.string.bad_request);
                    break;
                case 401:
                    errorMessage = getString(R.string.user_not_authenticated);
                    break;
                default:
                    errorMessage = getString(R.string.server_error_generic);
            }
        }
        activity.findViewById(R.id.add_object_loading_layout).setVisibility(View.GONE);
        activity.findViewById(R.id.add_object_main_layout).setVisibility(View.VISIBLE);
        Snackbar.make(this.requireView(), errorMessage, Snackbar.LENGTH_LONG).show();
    }
    /*
    private void returnToMyObjects(String message){
        MyObjectsFragment myObjectsFragment = MyObjectsFragment.newInstance(message);
        FragmentManager fragmentManager = this.getChildFragmentManager();
        fragmentManager.popBackStack();
        fragmentManager.beginTransaction().replace(R.id.home_fragment_container, myObjectsFragment).commit();
    }
    */
}