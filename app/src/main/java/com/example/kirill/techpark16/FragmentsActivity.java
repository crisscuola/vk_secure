package com.example.kirill.techpark16;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentTransaction;

/**
 * Created by kirill on 02.04.16
 */
public class FragmentsActivity extends FragmentActivity implements DialogsListFragment.onItemSelectedListener{
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragments_activity);
    }

    @Override
    public void onDialogSelected(int position) {
        DetailDialogFragment newFragment = DetailDialogFragment.getInstance(position);
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.add(R.id.fragment_container, newFragment);
        //transaction.addToBackStack(null);
        transaction.commit();
    }
}
