package com.cyberbug.view;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.viewpager2.widget.ViewPager2;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.cyberbug.R;
import com.google.android.material.tabs.TabItem;
import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;


public class GroupPageFrag extends Fragment {

    private static final String ARG_TOOLBAR_TITLE = "Toolbar title";
    private String toolTitle = null;
    private TabItem info, myObjs, board, members;


    public static GroupPageFrag newInstance(String groupName) {
        GroupPageFrag thisFrag = new GroupPageFrag();

        if(groupName != null) {
            Bundle args = new Bundle();
            args.putString(ARG_TOOLBAR_TITLE, groupName);
            thisFrag.setArguments(args);
        }
        return thisFrag;
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
            toolTitle = getArguments().getString(ARG_TOOLBAR_TITLE);
        }

        Fragment parent = this.getParentFragment();

        if (parent != null) {
            // Check to avoid crash if the parent fragment does not exists (should not happen)

            Toolbar tBar = parent.requireView().findViewById(R.id.toolbar);
            tBar.setTitle(toolTitle);
            parent.setHasOptionsMenu(false);
        }

        View v = inflater.inflate(R.layout.fragment_group_page, container, false);

        ViewPager2 viewPager2 = v.findViewById(R.id.group_viewPager2);
        TabLayout tabLayout = v.findViewById(R.id.group_tab_menu);

        GroupPageAdapter adt = new GroupPageAdapter(this.requireActivity(), this);
        viewPager2.setAdapter(adt);


        TabLayoutMediator tabLayoutMediator = new TabLayoutMediator(tabLayout, viewPager2, new TabLayoutMediator.TabConfigurationStrategy() {
            @Override
            public void onConfigureTab(@NonNull TabLayout.Tab tab, int position) {
                switch (position){
                    case 0:
                        tab.setText(R.string.group_info);
                        break;
                    case 1:
                        tab.setText(R.string.my_objs);
                        break;
                    case 2:
                        tab.setText(R.string.group_board);
                        break;
                    case 3:
                        tab.setText(R.string.group_members);
                        break;
                }
            }
        });
        tabLayoutMediator.attach();


        return v;
    }
}