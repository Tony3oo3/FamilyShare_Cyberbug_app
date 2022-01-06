package com.cyberbug.view;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.cyberbug.R;
import com.github.barteksc.pdfviewer.PDFView;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link GuideViewFrag#newInstance} factory method to
 * create an instance of this fragment.
 */
public class GuideViewFrag extends Fragment {

    private static final String ARG_ERROR_MESSAGE = "errorMessage";
    private String errorMessage = null;


    public GuideViewFrag(){
        // Empty constructor
    }

    public static GuideViewFrag newInstance(String errorMessage) {
        GuideViewFrag gf = new GuideViewFrag();
        Bundle args = new Bundle();
        args.putString(ARG_ERROR_MESSAGE, errorMessage);
        gf.setArguments(args);
        return gf;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) { super.onCreate(savedInstanceState); }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        // Create fragment view
        View v = inflater.inflate(R.layout.fragment_guide_view, container, false);

        // Initialize fragment components
        PDFView pdfView = v.findViewById(R.id.pdfView);
        pdfView.fromAsset("Families_Share_Walkthrough.pdf").load();

        // Set fragment title
        Fragment parent = this.getParentFragment();
        if (parent != null) {
            // Check to avoid crash if the parent fragment does not exists (should not happen)
            Toolbar tBar = parent.requireView().findViewById(R.id.toolbar);
            tBar.setTitle(getString(R.string.user_guide));
            parent.setHasOptionsMenu(false);
        }
        return v;
    }
}
