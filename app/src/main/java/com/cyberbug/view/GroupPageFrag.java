package com.cyberbug.view;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.viewpager2.widget.ViewPager2;

import com.cyberbug.R;
import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;

/**
 * The group page fragment, it contains:
 * {@link com.cyberbug.view.GroupInfoFrag}GroupInfoFrag,
 * {@link com.cyberbug.view.MyGroupObjsFrag}MyGroupObjsFrag
 * {@link com.cyberbug.view.GroupBoardFrag}GroupBoardFrag
 * {@link com.cyberbug.view.GroupMembersFrag}GroupMembersFrag
 */
public class GroupPageFrag extends Fragment {

    private static final String ARG_TOOLBAR_TITLE = "Toolbar title";
    private String toolTitle = null;


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
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
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


        TabLayoutMediator tabLayoutMediator = new TabLayoutMediator(tabLayout, viewPager2, (tab, position) -> {
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
        });
        tabLayoutMediator.attach();


        return v;
    }
}