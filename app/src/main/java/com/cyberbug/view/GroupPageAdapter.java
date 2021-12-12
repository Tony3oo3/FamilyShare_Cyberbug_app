package com.cyberbug.view;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.viewpager2.adapter.FragmentStateAdapter;


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
                return MyGroupObjsFrag.newInstance();
            //group board
            case 2:
                return GroupBoardFrag.newInstance();
            //group members
            case 3:
                return GroupMembersFrag.newInstance();
            //group info
            default:
                return GroupInfoFrag.newInstance();
        }
    }

    @Override
    public int getItemCount() {
        return 4;
    }
}
