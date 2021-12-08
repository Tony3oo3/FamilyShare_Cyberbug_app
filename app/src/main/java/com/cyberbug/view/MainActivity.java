package com.cyberbug.view;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentManager;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;

import com.cyberbug.api.FSAPIWrapper;
import com.cyberbug.model.SharedData;
import com.example.grafica.R;

public class MainActivity extends AppCompatActivity {

    public final static FSAPIWrapper fsAPI = new FSAPIWrapper("http://192.168.1.9");
    public static SharedData sData = new SharedData();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        if(this.retrieveSavedAuthData()){
            HomeFragment homeFrag = HomeFragment.newInstance();
            getSupportFragmentManager().beginTransaction().replace(R.id.main_fragment_container, homeFrag).commit();
        }
    }

    /**
     * Gets the authToken and userId from SharedPreferences and if they are not null initializes the
     * static sData object in MainActivity with them
     * @return true if the data is valid, false if al least one is null
     */
    private boolean retrieveSavedAuthData(){
        SharedPreferences sharedPref = this.getPreferences(Context.MODE_PRIVATE);
        String authToken = sharedPref.getString("authToken", null);
        String userId = sharedPref.getString("userId", null);
        if(authToken == null || userId == null)
            return false;
        else{
            MainActivity.sData = new SharedData(authToken, userId);
            return true;
        }

    }

    /**
     * Logs out the user, deletes session information and redirects to the login page
     * @param message the message to show in the snackBar when redirected to the login page
     */
    protected static void logoutUser(FragmentActivity act, String message){
        // Erase saved data
        MainActivity.sData = new SharedData();
        SharedPreferences.Editor editor = act.getPreferences(Context.MODE_PRIVATE).edit();
        editor.remove("authToken");
        editor.remove("userId");
        editor.apply();

        FragmentManager fm = act.getSupportFragmentManager();
        fm.popBackStack();
        fm.beginTransaction().replace(R.id.main_fragment_container, LoginFragment.newInstance(message)).commit();
    }
}