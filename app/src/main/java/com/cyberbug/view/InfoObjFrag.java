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
import android.widget.Button;
import android.widget.TextView;

import com.cyberbug.api.APIRequest;
import com.cyberbug.api.APIResponse;
import com.cyberbug.api.AsyncRESTDispatcher;
import com.cyberbug.api.UIUpdaterResponse;
import com.cyberbug.api.UIUpdaterVoid;
import com.cyberbug.R;
import com.cyberbug.functional.BiConsumer;
import com.google.android.material.snackbar.Snackbar;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;


public class InfoObjFrag extends Fragment {

    private final String objectId;
    private String objectName;
    private final Mode buttonMode;

    public enum Mode{
        LOAN,
        REMOVE_LOAN,
        DELETE
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
        if(buttonMode == Mode.LOAN) {
            // Button is loan
            btn.setText(R.string.btn_obj_loan);
            btn.setOnClickListener(this::onClickLoanButton);
        }else if(buttonMode == Mode.REMOVE_LOAN){
            // Button is remove
            btn.setText(R.string.btn_obj_remove_loan);
            btn.setOnClickListener(this::onClickRemoveLoanButton);
        }else if(buttonMode == Mode.DELETE){
            // Delete object from system
            btn.setText(R.string.btn_obj_delete);
            btn.setOnClickListener(this::onClickDeleteButton);
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
        APIRequest getObjectInfo = MainActivity.fsAPI.searchObjectRequest(MainActivity.sData.authToken, objectId);
        UIUpdaterVoid<View> preUpdater = new UIUpdaterVoid<>(v, this::showLoading);
        UIUpdaterResponse<View> postUpdater = new UIUpdaterResponse<>(v, this::populateAndShowObjectInfo);
        new AsyncRESTDispatcher(preUpdater, postUpdater).execute(getObjectInfo);
    }

    private void populateAndShowObjectInfo(View v, List<APIResponse> resList){
        APIResponse res = resList.get(0);
        boolean waitToShowPage = false;
        try{
            if(res.responseCode == 200 && res.jsonResponse != null){
                // OK
                JSONObject obj = res.jsonResponse;
                TextView nameText = v.findViewById(R.id.txt_info_obj_name);
                this.objectName = obj.getString("object_name");
                nameText.setText(this.objectName);
                TextView descText = v.findViewById(R.id.txt_obj_desc);
                descText.setText(obj.getString("object_description"));
                TextView stateText = v.findViewById(R.id.txt_obj_state);
                String state = (obj.getString("shared_with_user").equals("null")) ? getString(R.string.not_shared) : getString(R.string.shared);
                stateText.setText(state);
                // TODO get the groups when request is done
                // TextView sharedGroupsText = v.findViewById(R.id.txt_obj_shared_groups);

                // Get the user name from id
                APIRequest ownerNameRequest = MainActivity.fsAPI.getUserProfileRequest(MainActivity.sData.authToken, obj.getString("owner"));
                UIUpdaterVoid<View> preUpdater = new UIUpdaterVoid<>(v, (x)->{}); // do nothing
                UIUpdaterResponse<View> postUpdater = new UIUpdaterResponse<>(v, this::setOwnerName);
                new AsyncRESTDispatcher(preUpdater, postUpdater).execute(ownerNameRequest);
                waitToShowPage = true;
            }else if(res.responseCode == 401){
                MainActivity.logoutUser(this.requireActivity(), getString(R.string.user_not_authenticated));
            }else{
                // Generic error
                this.showSnackBar(getString(R.string.server_error_generic), v);
            }
        }catch(JSONException e) {
            e.printStackTrace();
            this.showSnackBar(getString(R.string.server_error_generic), v);
        }finally {
            // TODO show default?
            if(!waitToShowPage) this.showPage(v);
        }
    }

    private void setOwnerName(View v, List<APIResponse> resList) {
        APIResponse res = resList.get(0);
        try{
            if(res.responseCode == 200 && res.jsonResponse != null){
                TextView ownerName = v.findViewById(R.id.txt_obj_owner);
                String name = res.jsonResponse.getString("family_name") + " " + res.jsonResponse.getString("given_name");
                ownerName.setText(name);
            }else if(res.responseCode == 401){
                MainActivity.logoutUser(this.requireActivity(), getString(R.string.user_not_authenticated));
            }else{
                this.showSnackBar(getString(R.string.server_error_generic), v);
            }
        }catch (JSONException e){
            e.printStackTrace();
            this.showSnackBar(getString(R.string.server_error_generic), v);
        }finally {
            this.showPage(v);
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

    // Click request handlers
    private void onPostInfoObjectRequest(View v, List<APIResponse> responseList) {
        APIResponse res = responseList.get(0);
        String snackMessage = "";
        switch (res.responseCode){
            case 200:
                snackMessage = getString(R.string.obj_loan_success);
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
                snackMessage = getString(R.string.obj_loan_success);
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
                // snackMessage = getString(R.string.obj_deleted);
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