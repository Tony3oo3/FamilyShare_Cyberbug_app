package com.cyberbug.view;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.viewpager2.adapter.FragmentStateAdapter;

/**
 * A fragment state adapter to manage the group page sections
 */
public class GroupPageAdapter extends FragmentStateAdapter {
    private final GroupPageFrag parent;

    public GroupPageAdapter(@NonNull FragmentActivity fragmentActivity, GroupPageFrag parent) {
        super(fragmentActivity);
        this.parent = parent;
    }

    @NonNull
    @Override
    public Fragment createFragment(int position) {
        switch (position){
            //my group objects
            case 1:
                return MyGroupObjsFrag.newInstance(parent);
            //group board
            case 2:
                return GroupBoardFrag.newInstance(parent);
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
