package com.example.kirill.techpark16;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;

/**
 * Created by konstantin on 08.04.16.
 */
public class SettingsFragment extends Fragment{
//    private onFriendSelectedListener mCallback;

    public static SettingsFragment getInstance(int dialog_no, ArrayList<String> inList, ArrayList<String> outList){
        SettingsFragment settingsFragment = new SettingsFragment();
//        Log.i("inList2", String.valueOf((inList.get(2))));
        Bundle bundle = new Bundle();


       settingsFragment.setArguments(bundle);
        return settingsFragment;
    }

    @Override
    public View onCreateView(final LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.settings, null);

//        setListAdapter(new FriendListAdapter(inflater.getContext(), list));




        return super.onCreateView(inflater, container, savedInstanceState);
    }



}
