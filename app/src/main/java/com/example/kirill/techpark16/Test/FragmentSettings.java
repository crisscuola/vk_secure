package com.example.kirill.techpark16.Test;

import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.kirill.techpark16.R;

/**
 * Created by konstantin on 09.04.16.
 */
public class FragmentSettings extends Fragment {

    public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
    }

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.test_settings, container, false);

        return view;
    }

}
