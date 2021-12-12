package com.cyberbug.controller;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.cyberbug.R;

import java.util.Stack;

public class HomeBackStack {
    private final FragmentManager fm;
    private final Stack<Fragment> fragStack;

    public HomeBackStack(FragmentManager fm) {
        this.fm = fm;
        this.fragStack = new Stack<>();
    }

    public void popAndShowFragment(){
        if(!fragStack.empty()){
            Fragment f = fragStack.pop();

            FragmentTransaction ft = fm.beginTransaction();
            ft.replace(R.id.home_fragment_container, f).commit();
        }
    }

    public void add(Fragment f){
        fragStack.add(f);
    }

    public void clear(){
        fragStack.clear();
    }

    public boolean isEmpty(){
        return fragStack.isEmpty();
    }
}
