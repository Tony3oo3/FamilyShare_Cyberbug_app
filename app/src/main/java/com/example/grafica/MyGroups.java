package com.example.grafica;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;

public class MyGroups extends Fragment {

    public static MyGroups newInstance() {
        return new MyGroups();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_my_groups, container, false);
        ListView groupList = v.findViewById(R.id.my_groups_listview);
        ArrayAdapter<String> groups = new ArrayAdapter<>(
                this.requireContext(),
                R.layout.textview_group,
                new String[]{"Gruppo 1", "Gruppo 2", "Gruppo 3"});
        groupList.setAdapter(groups);
        return v;
    }
}