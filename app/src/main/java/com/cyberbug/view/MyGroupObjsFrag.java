package com.cyberbug.view;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import com.example.grafica.R;

import com.cyberbug.view.MyObjectsFragment;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link MyGroupObjsFrag#newInstance} factory method to
 * create an instance of this fragment.
 */
public class MyGroupObjsFrag extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public MyGroupObjsFrag() {
        // Required empty public constructor
    }
    public static MyGroupObjsFrag newInstance(String errorMessage) {
        MyGroupObjsFrag groupObjsFrag = new MyGroupObjsFrag();
        Bundle args = new Bundle();
        groupObjsFrag.setArguments(args);
        return groupObjsFrag;
    }

    // TODO: Rename and change types and number of parameters
    public static MyGroupObjsFrag newInstance(String param1, String param2) {
        MyGroupObjsFrag fragment = new MyGroupObjsFrag();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_my_group_objs, container, false);
    }
}