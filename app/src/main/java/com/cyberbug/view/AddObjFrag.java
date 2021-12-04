package com.cyberbug.view;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

import com.example.grafica.R;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link AddObjFrag#newInstance} factory method to
 * create an instance of this fragment.
 */
public class AddObjFrag extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_ERROR_MESSAGE = "errorMessage";

    // TODO: Rename and change types of parameters
    private EditText objName;
    private EditText objDesc;

    public AddObjFrag() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @return A new instance of fragment AddObjFrag.
     */
    // TODO: Rename and change types and number of parameters
    public static AddObjFrag newInstance(String param1) {
        AddObjFrag aof = new AddObjFrag();
        Bundle args = new Bundle();
        args.putString(ARG_ERROR_MESSAGE, param1);
        aof.setArguments(args);
        return aof;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        /*if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_ERROR_MESSAGE);
        }*/
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_add_obj, container, false);
    }
}