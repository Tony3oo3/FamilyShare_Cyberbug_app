package com.cyberbug.view;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.lifecycle.Lifecycle;
import androidx.viewpager2.adapter.FragmentStateAdapter;

import com.example.grafica.GroupBoardFrag;
import com.example.grafica.GroupMembersFrag;

public class GroupPageAdapter extends FragmentStateAdapter {

    public GroupPageAdapter(@NonNull FragmentActivity fragmentActivity) {
        super(fragmentActivity);
    }

    @NonNull
    @Override
    public Fragment createFragment(int position) {
        switch (position){
            //my group objects
            case 1:
                return new MyGroupObjsFrag();
            //group board
            case 2:
                return new GroupBoardFrag();
            //group members
            case 3:
                return new GroupMembersFrag();
            //group info
            default:
                return new GroupInfoFrag();
        }
    }

    @Override
    public int getItemCount() {
        return 4;
    }
}
