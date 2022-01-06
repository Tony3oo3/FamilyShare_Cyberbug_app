package com.cyberbug.view;

import android.annotation.SuppressLint;
import android.content.DialogInterface;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import com.cyberbug.controller.HomeBackStack;
import com.cyberbug.functional.BiConsumer;
import com.cyberbug.R;
import com.google.android.material.navigation.NavigationView;

public class HomeFragment extends Fragment {

    private BiConsumer<Menu,MenuInflater> onCreateMenuCallback;

    public static FragmentManager homeFragmentManager;
    public static HomeBackStack homeBackStack;

    private static HomeFragment thisFrag;

    public static HomeFragment newInstance() {
        thisFrag = new HomeFragment();
        return thisFrag;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        this.setUpSideMenu(view);
        NavigationView menu = view.findViewById(R.id.nav_view_side_menu);
        menu.setNavigationItemSelectedListener(this::onMenuItemClicked);

        HomeFragment.homeFragmentManager = this.getChildFragmentManager();
        HomeFragment.homeBackStack = new HomeBackStack(HomeFragment.homeFragmentManager);

        switchHomeFragment(MyGroupsFragment.newInstance());
    }

    private void setUpSideMenu(View view){
        Toolbar toolbar = view.findViewById(R.id.toolbar);

        if (getActivity() instanceof AppCompatActivity) {
            ((AppCompatActivity) getActivity()).setSupportActionBar(toolbar);
        }

        DrawerLayout drawer = view.findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(getActivity(), drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);

        toggle.setDrawerIndicatorEnabled(true);
        toggle.syncState();
        drawer.addDrawerListener(toggle);
    }

    private void switchHomeFragment(Fragment f){
        HomeFragment.homeFragmentManager.beginTransaction().replace(R.id.home_fragment_container, f).commit();
        // Clears the back stack
        homeBackStack.clear();
    }

    @SuppressLint("NonConstantResourceId")
    private boolean onMenuItemClicked(MenuItem item){
        // Close drawer
        DrawerLayout drawer = requireView().findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);

        // Navigate to different pages
        switch (item.getItemId()) {
            case R.id.nav_profile:
                this.switchHomeFragment(ViewProfileFrag.newInstance(null, MainActivity.sData.thisUserId));
                break;

            case R.id.nav_my_objs:
                this.switchHomeFragment(MyObjectsFragment.newInstance(false, false));
                break;

            case R.id.nav_my_groups:
                this.switchHomeFragment(MyGroupsFragment.newInstance());
                break;

            case R.id.nav_group_access:
                this.switchHomeFragment(GroupAccessFrag.newInstance());
                break;

            case R.id.nav_create_group:
                this.switchHomeFragment(CreateGroupFrag.newInstance(null));
                break;

            case R.id.nav_loan:
                this.switchHomeFragment(LoansFrag.newInstance(null));
                break;

            case R.id.nav_logout:
                showAreYouSureDialog();
                break;

            case R.id.nav_req:
                this.switchHomeFragment(RequestsFrag.newInstance(null));
                break;

            case R.id.nav_guide:
                this.switchHomeFragment(GuideViewFrag.newInstance(null));
                break;
        }
        return true;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_home, container, false);
    }

    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        if(this.onCreateMenuCallback != null){
            onCreateMenuCallback.consume(menu, inflater);
            onCreateMenuCallback = null; // Prevents to be used again
        }
    }

    protected static void setOptionsMenu(BiConsumer<Menu,MenuInflater> onCreateMenuCallback){
        if(HomeFragment.thisFrag != null) {
            HomeFragment.thisFrag.setHasOptionsMenu(true);
            HomeFragment.thisFrag.onCreateMenuCallback = onCreateMenuCallback;
            HomeFragment.thisFrag.requireActivity().invalidateOptionsMenu();
        }
    }

    private void showAreYouSureDialog(){
        new AlertDialog.Builder(this.requireContext())
                .setTitle(R.string.logout)
                .setMessage(R.string.sure_to_logout)
                .setIcon(android.R.drawable.ic_dialog_alert)
                .setPositiveButton(R.string.yes, this::onLogoutYesClick)
                .setNegativeButton(R.string.no, null).show();
    }

    private void onLogoutYesClick(DialogInterface dialog, int whichButton) {
        MainActivity.logoutUser(this.requireActivity(),getString(R.string.logout_success));
    }
}