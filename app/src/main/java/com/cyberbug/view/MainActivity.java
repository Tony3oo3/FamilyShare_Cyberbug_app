package com.cyberbug.view;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentManager;

import android.os.Bundle;

import com.cyberbug.api.FSAPIWrapper;
import com.cyberbug.model.SharedData;
import com.example.grafica.R;

public class MainActivity extends AppCompatActivity {

    public final static FSAPIWrapper fsapi = new FSAPIWrapper("http://192.168.1.122");
    public static SharedData sData = new SharedData();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    /**
     * Logs out the user, deletes session information and redirects to the login page
     * @param fragMan the FragmentManager used to swap the current fragment with the login fragment
     * @param message the message to show in the snackBar when redirected to the login page
     */
    protected static void logoutUser(FragmentManager fragMan, String message){
        MainActivity.sData = new SharedData();
        fragMan.popBackStack();
        fragMan.beginTransaction().replace(R.id.main_fragment_container, LoginFragment.newInstance(message)).commit();
    }
}