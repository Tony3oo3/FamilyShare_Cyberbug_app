package com.cyberbug.view;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import android.os.Bundle;

import com.cyberbug.api.FSAPIWrapper;
import com.cyberbug.model.SharedData;
import com.example.grafica.R;

public class MainActivity extends AppCompatActivity {

    public final static FSAPIWrapper fsapi = new FSAPIWrapper("http://192.168.1.9");
    public static SharedData sData = new SharedData();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }
}