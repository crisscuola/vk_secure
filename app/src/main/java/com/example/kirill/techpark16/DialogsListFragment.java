package com.example.kirill.techpark16;

import android.app.ListFragment;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.view.View;

/**
 * Created by kirill on 17.03.16
 */
public class DialogsListFragment extends ListFragment {
    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
    }

    public interface onItemSelectedListener {
        public void onDialogSelected(int position);
    }
}
