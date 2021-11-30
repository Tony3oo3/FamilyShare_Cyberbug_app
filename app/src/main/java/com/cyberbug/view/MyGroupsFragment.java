package com.cyberbug.view;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.example.grafica.R;

public class MyGroupsFragment extends Fragment {

    public static MyGroupsFragment newInstance() {
        return new MyGroupsFragment();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_my_groups, container, false);

        // Set toolbar title
        Fragment parent = this.getParentFragment();
        if(parent != null) {
            // Check to avoid crash if the parent fragment does not exists (should not happen)
            Toolbar tBar = parent.requireView().findViewById(R.id.toolbar);
            tBar.setTitle(getString(R.string.my_groups));
        }

        ListView groupList = v.findViewById(R.id.my_groups_listview);
        ArrayAdapter<String> groups = new ArrayAdapter<>(
                this.requireContext(),
                R.layout.textview_group,
                new String[]{"Gruppo 1", "Gruppo 2", "Gruppo 3"});
        groupList.setAdapter(groups);


        return v;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

    }

}