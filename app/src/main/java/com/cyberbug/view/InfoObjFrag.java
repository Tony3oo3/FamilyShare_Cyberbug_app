package com.cyberbug.view;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentManager;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.cyberbug.api.APIRequest;
import com.cyberbug.api.APIResponse;
import com.cyberbug.api.AsyncRESTDispatcher;
import com.cyberbug.api.FSAPIWrapper;
import com.cyberbug.api.UIUpdaterResponse;
import com.cyberbug.api.UIUpdaterVoid;
import com.cyberbug.model.MyObject;
import com.example.grafica.R;
import com.google.android.material.snackbar.Snackbar;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;


/**
 * A simple {@link Fragment} subclass.
 * Use the {@link InfoObjFrag#newInstance} factory method to
 * create an instance of this fragment.
 */
public class InfoObjFrag extends Fragment {

    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_ERROR_MESSAGE = "errorMessage";
    private String errorMessage = null;

    private String objectId;

    private TextView owner;
    private TextView desc;
    private TextView state;
    private TextView sharedGroup;

    public InfoObjFrag() {
        // Required empty public constructor
    }

    public InfoObjFrag(String objectId) {
        this.objectId = objectId;
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param errorMessage Parameter 1.
     * @param  objectId Parameter 2
     * @return A new instance of fragment InfoObjFrag.
     */
    public static InfoObjFrag newInstance(String errorMessage, String objectId) {
        InfoObjFrag iof = new InfoObjFrag(objectId);
        Bundle args = new Bundle();
        args.putString(ARG_ERROR_MESSAGE, errorMessage);
        iof.setArguments(args);
        return iof;
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
        View v = inflater.inflate(R.layout.fragment_info_obj, container, false);

        // Initialize fragment components
        owner = v.findViewById(R.id.txt_obj_owner);
        desc = v.findViewById(R.id.txt_obj_desc);
        state = v.findViewById(R.id.txt_obj_state);
        sharedGroup = v.findViewById(R.id.txt_obj_group);

        // Set buttons listeners
        Button loan = v.findViewById(R.id.btn_obj_loan);
        loan.setOnClickListener(this::onClickLoanButton);

        // Set fragment title
        Fragment parent = this.getParentFragment();
        if (parent != null) {
            // Check to avoid crash if the parent fragment does not exists (should not happen)
            Toolbar tBar = parent.requireView().findViewById(R.id.toolbar);
            tBar.setTitle(getString(R.string.obj_info));
            parent.setHasOptionsMenu(false);
        }

        return v;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        populateTextViews();
    }

    private void populateTextViews(){
        APIRequest getObjectInfo = MainActivity.fsAPI.searchObject(MainActivity.sData.authToken, objectId);
        UIUpdaterVoid<FragmentActivity> preUpdater = new UIUpdaterVoid<>(this.requireActivity(), this::showLoading);
        UIUpdaterResponse<FragmentActivity> postUpdater = new UIUpdaterResponse<>(this.requireActivity(), this::searchObjectFromId);
        new AsyncRESTDispatcher(preUpdater, postUpdater).execute(getObjectInfo);
    }

    private void showLoading(FragmentActivity act) {
        ProgressBar pr = act.findViewById(R.id.progressBar_myGroups);
        pr.setVisibility(View.VISIBLE);
        owner.setVisibility(View.GONE);
        desc.setVisibility(View.GONE);
        sharedGroup.setVisibility(View.GONE);
        state.setVisibility(View.GONE);
    }

    private void searchObjectFromId(FragmentActivity act, List<APIResponse> resList){
        APIResponse res = resList.get(0);
        if (res.responseCode == 200 && res.jsonResponseArray != null) {
            // All ok
            // There is one id, we need to get the info from the object with that id
            try {
                JSONObject obj = res.jsonResponseArray.getJSONObject(0);
                String id = obj.getString("object_id");
                String name = obj.getString("object_name");
                String desc = obj.getString("object_description");
                String owner = obj.getString("owner");
                String state = obj.getString("shared_with_user");

                MyObject o = new MyObject(id,name,desc,owner,state);

                // Do nothing before the requests
                UIUpdaterVoid<?> preUpdater = new UIUpdaterVoid<>(null, (x) -> {});
                // Populate the UI list and change fragment
                //UIUpdaterResponse<FragmentActivity> postUpdater = new UIUpdaterResponse<>(act, this::populateAndShowObjectInfo);
                //new AsyncRESTDispatcher(preUpdater, postUpdater).execute(req);
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
        //this.showDefaultListMessage(act);
        if (res.responseCode != 404 && this.getView() != null) {
            // in this case there was an error
            Snackbar.make(this.getView(), getString(R.string.server_error_generic), Snackbar.LENGTH_LONG).show();
        }
    }

    private void populateAndShowObjectInfo(MyObject obj){

    }

    public void onClickLoanButton( View v){
        // Get text
        String name = null;
        String desc = null;

        // Error handling
        if(name.isEmpty() || desc.isEmpty()) {
            Snackbar.make(this.requireView(), getString(R.string.insert_required_fields), Snackbar.LENGTH_LONG).show();
            return;
        }

        // All is ok
        // Send the request to the serve
        // TODO change this
        FSAPIWrapper.ObjectData obj = new FSAPIWrapper.ObjectData(name, desc, MainActivity.sData.thisUserId);
        UIUpdaterVoid<FragmentActivity> preUpdater = new UIUpdaterVoid<>(this.requireActivity(), InfoObjFrag::onPreInfoObjectRequest);
        UIUpdaterResponse<FragmentActivity> postUpdater = new UIUpdaterResponse<>(this.requireActivity(), this::onPostInfoObjectRequest);
        APIRequest req = MainActivity.fsAPI.insertObjectRequest(MainActivity.sData.authToken, MainActivity.sData.thisUserId ,obj);
        new AsyncRESTDispatcher(preUpdater, postUpdater).execute(req);
    }

    private static void onPreInfoObjectRequest(FragmentActivity activity) {
        // Changes to LoadingFragment
        activity.findViewById(R.id.info_object_main_layout).setVisibility(View.GONE);
        activity.findViewById(R.id.info_object_loading_layout).setVisibility(View.VISIBLE);
    }

    private void onPostInfoObjectRequest(FragmentActivity activity, List<APIResponse> responseList) {
        APIResponse res = responseList.get(0);
        // 200 ok
        // 400 bad request
        // 401 user not authenticated or unauthorized
        // else server error
        if (res.responseCode == 200) {
            //this.returnToMyObjects(getString(R.string.obj_loan_success));
            errorMessage = getString(R.string.obj_loan_success);
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
        activity.findViewById(R.id.info_object_loading_layout).setVisibility(View.GONE);
        activity.findViewById(R.id.info_object_main_layout).setVisibility(View.VISIBLE);
        Snackbar.make(this.requireView(), errorMessage, Snackbar.LENGTH_LONG).show();
    }
    private void returnToMyObjects(String message){
        MyGroupObjsFrag myGroupObjs = MyGroupObjsFrag.newInstance(message);
        FragmentManager fragmentManager = this.getChildFragmentManager();
        fragmentManager.popBackStack();
        fragmentManager.beginTransaction().replace(R.id.home_fragment_container, myGroupObjs).commit();
    }


}