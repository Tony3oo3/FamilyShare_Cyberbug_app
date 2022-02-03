package com.cyberbug.view;

import android.content.DialogInterface;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import com.cyberbug.api.APIRequest;
import com.cyberbug.api.APIResponse;
import com.cyberbug.api.AsyncRESTDispatcher;
import com.cyberbug.api.UIUpdaterResponse;
import com.cyberbug.api.UIUpdaterVoid;
import com.cyberbug.R;
import com.cyberbug.functional.BiConsumer;
import com.cyberbug.model.Group;
import com.google.android.material.snackbar.Snackbar;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * Fragment used to show object information and to loan, delete, return an object.
 * It is also used to remove a loan and accept a loan request
 */
public class InfoObjFrag extends Fragment {

    private final String objectId;
    private String objectName;
    private final Mode buttonMode;

    // enum used to set fragment layout
    public enum Mode{
        LOAN,
        REMOVE_LOAN,
        DELETE,
        ACCEPT_REQ,
        RETURN,
        DEFAULT
    }

    public InfoObjFrag(String objectId, InfoObjFrag.Mode buttonMode) {
        this.objectId = objectId;
        this.buttonMode = buttonMode;
    }

    public static InfoObjFrag newInstance(String objectId, InfoObjFrag.Mode buttonMode) {
        return new InfoObjFrag(objectId, buttonMode);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        // Create fragment view
        View v = inflater.inflate(R.layout.fragment_info_obj, container, false);

        // Set buttons listeners and text
        Button btn = v.findViewById(R.id.btn_info_object);
        Button btnIgn = v.findViewById(R.id.btn_ignore_req);
        switch (buttonMode) {
            case LOAN:
                // Button is loan
                btn.setText(R.string.btn_obj_loan);
                btn.setOnClickListener(this::onClickLoanButton);
                break;
            case REMOVE_LOAN:
                // Button is remove
                btn.setText(R.string.btn_obj_remove_loan);
                btn.setOnClickListener(this::onClickRemoveLoanButton);
                break;
            case DELETE:
                // Delete object from system
                btn.setText(R.string.btn_obj_delete);
                btn.setOnClickListener(this::onClickDeleteButton);
                break;
            case ACCEPT_REQ:
                // Accept share request
                btn.setText(R.string.btn_obj_accept);
                btn.setOnClickListener(this::onClickAcceptButton);
                btnIgn.setVisibility(View.VISIBLE);
                btnIgn.setText(R.string.btn_obj_ignore_txt);
                btnIgn.setOnClickListener(this::onClickIgnoreReqButton);
                break;
            case RETURN:
                // Return object to owner
                btn.setText(R.string.btn_obj_return);
                btn.setOnClickListener(this::onClickReturnButton);
                break;
            default:
                btn.setVisibility(View.GONE);
                break;
        }


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
        populateTextViews(view);
    }

    // Utility
    private void showLoading(View v) {
        v.findViewById(R.id.info_object_main_layout).setVisibility(View.GONE);
        v.findViewById(R.id.info_object_loading_layout).setVisibility(View.VISIBLE);
    }

    private void showPage(View v){
        v.findViewById(R.id.info_object_loading_layout).setVisibility(View.GONE);
        v.findViewById(R.id.info_object_main_layout).setVisibility(View.VISIBLE);
    }

    private void showSnackBar(String message, View v){
        if (this.getView() != null) {
            Snackbar.make(v, message, Snackbar.LENGTH_LONG).show();
        }
    }

    private void showDialog(BiConsumer<DialogInterface, Integer> onYes, String message, String title, int icon){
        new AlertDialog.Builder(this.requireContext())
                .setTitle(title)
                .setMessage(message)
                .setIcon(icon)
                .setPositiveButton(R.string.yes, onYes::consume)
                .setNegativeButton(R.string.no, null).show();
    }


    // UI loaders
    private void populateTextViews(View v){
        if(this.getActivity() != null) {
            APIRequest getObjectInfo = MainActivity.fsAPI.searchObjectRequest(MainActivity.sData.authToken, objectId);
            UIUpdaterVoid<View> preUpdater = new UIUpdaterVoid<>(v, this::showLoading);
            UIUpdaterResponse<FragmentActivity> postUpdater = new UIUpdaterResponse<>(this.getActivity(), this::populateAndShowObjectInfo);
            new AsyncRESTDispatcher(preUpdater, postUpdater).execute(getObjectInfo);
        }
    }

    // Method that shows the object information get from APIResponse
    private void populateAndShowObjectInfo(FragmentActivity act, List<APIResponse> resList){
        APIResponse res = resList.get(0);
        boolean waitToShowPage = false;
        try{
            if(res.responseCode == 200 && res.jsonResponse != null){
                // OK
                JSONObject obj = res.jsonResponse;
                TextView nameText = act.findViewById(R.id.txt_info_obj_name);
                this.objectName = obj.getString("object_name");
                nameText.setText(this.objectName);
                TextView descText = act.findViewById(R.id.txt_obj_desc);
                descText.setText(obj.getString("object_description"));
                TextView stateText = act.findViewById(R.id.txt_obj_state);
                String state = (obj.getString("shared_with_user").equals("null")) ? getString(R.string.not_shared) : getString(R.string.shared);
                stateText.setText(state);

                // Disables loan request button if object is already lent

                if(state.equals(getString(R.string.shared)) && buttonMode == Mode.LOAN){
                    Button btn = act.findViewById(R.id.btn_info_object);
                    btn.setVisibility(View.GONE);
                }

                // Get the group ids
                JSONArray JSONGroups = obj.getJSONArray("group_ids");
                List<String> sharedGroups = new ArrayList<>();
                for(int i = 0; i < JSONGroups.length(); i++){
                    sharedGroups.add((String)JSONGroups.get(i));
                }
                setGroupNamesFromIds(act, sharedGroups);


                // Get the user name from id
                APIRequest ownerNameRequest = MainActivity.fsAPI.getUserProfileRequest(MainActivity.sData.authToken, obj.getString("owner"));
                UIUpdaterVoid<FragmentActivity> preUpdater = new UIUpdaterVoid<>(act, (x)->{}); // do nothing
                UIUpdaterResponse<FragmentActivity> postUpdater = new UIUpdaterResponse<>(act, this::setOwnerName);
                new AsyncRESTDispatcher(preUpdater, postUpdater).execute(ownerNameRequest);
                waitToShowPage = true;
            }else if(res.responseCode == 401){
                MainActivity.logoutUser(this.requireActivity(), getString(R.string.user_not_authenticated));
            }else{
                // Generic error
                this.showSnackBar(getString(R.string.server_error_generic), this.requireView());
            }
        }catch(JSONException e) {
            e.printStackTrace();
            this.showSnackBar(getString(R.string.server_error_generic), this.requireView());
        }finally {
            if(!waitToShowPage) this.showPage(this.requireView());
        }
    }

    private void setGroupNamesFromIds(FragmentActivity act, List<String> sharedGroups) {
        APIRequest[] req = new APIRequest[sharedGroups.size()];
        for(int i = 0; i < sharedGroups.size(); i++){
            // build a request for the id
            req[i] = MainActivity.fsAPI.getGroupByIdRequest(sharedGroups.get(i));
        }
        UIUpdaterVoid<?> preUpdater = new UIUpdaterVoid<>(null, (x) -> {});
        UIUpdaterResponse<FragmentActivity> postUpdater = new UIUpdaterResponse<>(act, this::populateAndShowGroupList);
        new AsyncRESTDispatcher(preUpdater, postUpdater).execute(req);
    }

    private void populateAndShowGroupList(FragmentActivity act, List<APIResponse> resList) {
        ListView sharedGroupsText = act.findViewById(R.id.txt_obj_shared_groups);
        List<Group> sharedGroups = new ArrayList<>();

        boolean error = false;
        // For each response construct a Group object and add it to the Adapter
        // if some responses are not good then set the error flag
        for (APIResponse res : resList) {
            try {
                if (res.responseCode == 200 && res.jsonResponse != null) {
                    String name = res.jsonResponse.getString("name");
                    String id = res.jsonResponse.getString("group_id");
                    sharedGroups.add(new Group(id, name));
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

        ArrayAdapter<Group> myGroupsAdapter = new ArrayAdapter<>(this.requireContext(), R.layout.textview_group, sharedGroups);
        sharedGroupsText.setAdapter(myGroupsAdapter);
    }

    private void setOwnerName(FragmentActivity act, List<APIResponse> resList) {
        APIResponse res = resList.get(0);
        try{
            if(res.responseCode == 200 && res.jsonResponse != null){
                TextView ownerName = act.findViewById(R.id.txt_obj_owner);
                String name = res.jsonResponse.getString("family_name") + " " + res.jsonResponse.getString("given_name");
                ownerName.setText(name);
            }else if(res.responseCode == 401){
                MainActivity.logoutUser(this.requireActivity(), getString(R.string.user_not_authenticated));
            }else{
                this.showSnackBar(getString(R.string.server_error_generic), this.requireView());
            }
        }catch (JSONException e){
            e.printStackTrace();
            this.showSnackBar(getString(R.string.server_error_generic), this.requireView());
        }finally {
            this.showPage(this.requireView());
        }
    }

    // Click listeners


    public void onClickLoanButton(View v){
        this.showDialog(this::onLoanRequest, getString(R.string.sure_want_to_loan), this.objectName, android.R.drawable.ic_input_add);
    }
    public void onLoanRequest(DialogInterface d, int i){
        d.dismiss();
        if(this.getView() != null) {
            UIUpdaterVoid<View> preUpdater = new UIUpdaterVoid<>(this.getView(), this::showLoading);
            UIUpdaterResponse<View> postUpdater = new UIUpdaterResponse<>(this.getView(), this::onPostInfoObjectRequest);
            APIRequest req = MainActivity.fsAPI.loanObjectRequest(MainActivity.sData.authToken, objectId);
            new AsyncRESTDispatcher(preUpdater, postUpdater).execute(req);
        }
    }
    public void onClickAcceptButton(View v){
        this.showDialog(this::onAcceptRequest, getString(R.string.sure_want_to_accept), this.objectName, android.R.drawable.ic_input_add);
    }
    public void onAcceptRequest(DialogInterface d, int i){
        d.dismiss();
        if(this.getView() != null) {
            UIUpdaterVoid<View> preUpdater = new UIUpdaterVoid<>(this.getView(), this::showLoading);
            UIUpdaterResponse<View> postUpdater = new UIUpdaterResponse<>(this.getView(), this::onPostAcceptRequest);
            APIRequest req = MainActivity.fsAPI.acceptShareReq(MainActivity.sData.authToken, objectId);
            new AsyncRESTDispatcher(preUpdater, postUpdater).execute(req);
        }
    }

    public void onClickRemoveLoanButton(View v){
        this.showDialog(this::onRemoveLoanRequest, getString(R.string.sure_want_to_remove_loan), this.objectName, android.R.drawable.ic_dialog_alert);
    }

    private void onRemoveLoanRequest(DialogInterface d, int i){
        d.dismiss();
        if(this.getView() != null) {
            UIUpdaterVoid<View> preUpdater = new UIUpdaterVoid<>(this.getView(), this::showLoading);
            UIUpdaterResponse<View> postUpdater = new UIUpdaterResponse<>(this.getView(), this::onPostRemoveLoanRequest);
            APIRequest req = MainActivity.fsAPI.removeSharedObjectFromGroupRequest(MainActivity.sData.authToken, objectId, MainActivity.sData.selectedGroup.id);
            new AsyncRESTDispatcher(preUpdater, postUpdater).execute(req);
        }
    }

    public void onClickIgnoreReqButton(View v){
        this.showDialog(this::onIgnoreReqRequest, getString(R.string.sure_want_to_ignore), this.objectName, android.R.drawable.ic_dialog_alert);
    }

    private void onIgnoreReqRequest(DialogInterface d, int i){
        d.dismiss();
        if(this.getView() != null) {
            UIUpdaterVoid<View> preUpdater = new UIUpdaterVoid<>(this.getView(), this::showLoading);
            UIUpdaterResponse<View> postUpdater = new UIUpdaterResponse<>(this.getView(), this::onPostIgnoreReqRequest);
            APIRequest req = MainActivity.fsAPI.ignoreShareReq(MainActivity.sData.authToken, objectId);
            new AsyncRESTDispatcher(preUpdater, postUpdater).execute(req);
        }
    }

    private void onClickDeleteButton(View v){
        this.showDialog(this::sendDeleteRequest, getString(R.string.sure_want_to_delete), this.objectName, android.R.drawable.ic_dialog_alert);
    }

    private void sendDeleteRequest(DialogInterface d, int i){
        d.dismiss();
        // Send the request to the server
        if(this.getView() != null) {
            UIUpdaterVoid<View> preUpdater = new UIUpdaterVoid<>(this.getView(), this::showLoading);
            UIUpdaterResponse<View> postUpdater = new UIUpdaterResponse<>(this.getView(), this::onPostDeleteRequest);
            APIRequest req = MainActivity.fsAPI.deleteObjectRequest(MainActivity.sData.authToken, objectId);
            new AsyncRESTDispatcher(preUpdater, postUpdater).execute(req);
        }
    }

    private void onClickReturnButton(View v){
        this.showDialog(this::sendReturnRequest, getString(R.string.sure_want_to_return), this.objectName, android.R.drawable.ic_dialog_alert);
    }

    private void sendReturnRequest(DialogInterface d, int i){
        d.dismiss();
        // Send the request to the server
        if(this.getView() != null) {
            UIUpdaterVoid<View> preUpdater = new UIUpdaterVoid<>(this.getView(), this::showLoading);
            UIUpdaterResponse<View> postUpdater = new UIUpdaterResponse<>(this.getView(), this::onPostReturnRequest);
            APIRequest req = MainActivity.fsAPI.returnObjectRequest(MainActivity.sData.authToken, objectId);
            new AsyncRESTDispatcher(preUpdater, postUpdater).execute(req);
        }
    }

    // Click request handlers
    private void onPostInfoObjectRequest(View v, List<APIResponse> responseList) {
        APIResponse res = responseList.get(0);
        String snackMessage = "";
        switch (res.responseCode){
            case 200:
                snackMessage = getString(R.string.share_req_txt);
                break;
            case 400:
                snackMessage = getString(R.string.bad_request);
                break;
            case 401:
                MainActivity.logoutUser(this.requireActivity(), getString(R.string.user_not_authenticated));
                break;
            default:
                snackMessage = getString(R.string.server_error_generic);
        }
        if(res.responseCode != 401) {
            this.showPage(v);
            this.showSnackBar(snackMessage, v);
        }
    }

    private void onPostRemoveLoanRequest(View v, List<APIResponse> responseList) {
        APIResponse res = responseList.get(0);
        String snackMessage = "";
        switch (res.responseCode){
            case 200:
                snackMessage = getString(R.string.obj_remove_share);
                break;
            case 400:
                snackMessage = getString(R.string.bad_request);
                break;
            case 401:
                MainActivity.logoutUser(this.requireActivity(), getString(R.string.user_not_authenticated));
                break;
            default:
                snackMessage = getString(R.string.server_error_generic);
        }
        if(res.responseCode != 401) {
            this.showPage(v);
            this.showSnackBar(snackMessage, v);
        }
    }

    private void onPostDeleteRequest(View v, List<APIResponse> responseList) {
        APIResponse res = responseList.get(0);
        String snackMessage = "";
        switch (res.responseCode){
            case 200:
                snackMessage = getString(R.string.obj_deleted);
                HomeFragment.homeFragmentManager.beginTransaction()
                        .replace(R.id.home_fragment_container, MyObjectsFragment.newInstance(false, false))
                        .commit();
                break;
            case 400:
                snackMessage = getString(R.string.bad_request);
                break;
            case 401:
                MainActivity.logoutUser(this.requireActivity(), getString(R.string.user_not_authenticated));
                break;
            default:
                snackMessage = getString(R.string.server_error_generic);
        }

        if(res.responseCode != 401 && res.responseCode != 200) {
            this.showPage(v);
            this.showSnackBar(snackMessage, v);
        }
    }

    private void onPostReturnRequest(View v, List<APIResponse> responseList) {
        APIResponse res = responseList.get(0);
        String snackMessage = "";
        switch (res.responseCode){
            case 200:
                snackMessage = getString(R.string.obj_returned);
                HomeFragment.homeFragmentManager.beginTransaction()
                        .replace(R.id.home_fragment_container, MyObjectsFragment.newInstance(false, false))
                        .commit();
                break;
            case 400:
                snackMessage = getString(R.string.bad_request);
                break;
            case 401:
                MainActivity.logoutUser(this.requireActivity(), getString(R.string.user_not_authenticated));
                break;
            default:
                snackMessage = getString(R.string.server_error_generic);
        }

        if(res.responseCode != 401 && res.responseCode != 200) {
            this.showPage(v);
            this.showSnackBar(snackMessage, v);
        }
    }

    private void onPostAcceptRequest(View v, List<APIResponse> responseList) {
        APIResponse res = responseList.get(0);
        String snackMessage = "";
        switch (res.responseCode){
            case 200:
                snackMessage = getString(R.string.obj_req_ignored);
                HomeFragment.homeFragmentManager.beginTransaction()
                        .replace(R.id.home_fragment_container, MyObjectsFragment.newInstance(false, false))
                        .commit();
                break;
            case 400:
                snackMessage = getString(R.string.bad_request);
                break;
            case 401:
                MainActivity.logoutUser(this.requireActivity(), getString(R.string.user_not_authenticated));
                break;
            default:
                snackMessage = getString(R.string.server_error_generic);
        }

        if(res.responseCode != 401 && res.responseCode != 200) {
            this.showPage(v);
            this.showSnackBar(snackMessage, v);
        }
    }

    private void onPostIgnoreReqRequest(View v, List<APIResponse> responseList) {
        APIResponse res = responseList.get(0);
        String snackMessage = "";
        switch (res.responseCode){
            case 200:
                snackMessage = getString(R.string.obj_req_ignored);
                HomeFragment.homeFragmentManager.beginTransaction()
                        .replace(R.id.home_fragment_container, MyObjectsFragment.newInstance(false, false))
                        .commit();
                break;
            case 400:
                snackMessage = getString(R.string.bad_request);
                break;
            case 401:
                MainActivity.logoutUser(this.requireActivity(), getString(R.string.user_not_authenticated));
                break;
            default:
                snackMessage = getString(R.string.server_error_generic);
        }

        if(res.responseCode != 401 && res.responseCode != 200) {
            this.showPage(v);
            this.showSnackBar(snackMessage, v);
        }
    }
}