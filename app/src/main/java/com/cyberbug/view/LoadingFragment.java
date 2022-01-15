package com.cyberbug.view;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.cyberbug.R;

/**
 * A fragment that shows just a loading bar, used by login and registration
 */
public class LoadingFragment extends Fragment {

    private static LoadingFragment thisFrag;

    public static LoadingFragment newInstance() {
        if(thisFrag == null)
            thisFrag = new LoadingFragment();
        return thisFrag;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_loading, container, false);
    }
}