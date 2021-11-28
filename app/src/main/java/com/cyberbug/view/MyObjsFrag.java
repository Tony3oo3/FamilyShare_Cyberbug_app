package com.cyberbug.view;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.grafica.R;

public class MyObjsFrag extends Fragment {

    public MyObjsFrag() {
        // Required empty public constructor
    }

    public static MyObjsFrag newInstance() {
        return new MyObjsFrag();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View v = inflater.inflate(R.layout.fragment_my_objs, container, false);
        /* Codice per cambiare il titolo della toolbar
        // TODO mettere come paraemtro la toolbar in newInstance (quando il fragment padre crea this
            allora deve passare la toolbar sulla quale cambiare il titolo)
        Fragment parent = this.requireParentFragment();
        Toolbar tb = parent.requireView().findViewById(R.id.toolbar);
        tb.setTitle("Prova");
        */

        return v;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
    }
}