package com.example.kirill.techpark16;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.ListFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;

/**
 * Created by kirill on 17.03.16
 */
public class DialogsListFragment extends ListFragment {
    String[] numbers_text = new String[]{"one", "two", "three", "four",
            "five", "six", "seven", "eight", "nine", "ten", "eleven",
            "twelve", "thirteen", "fourteen", "fifteen"};
    private onItemSelectedListener mCallback;

    @Override
    public void onListItemClick(ListView l, View v, int position, long id) {
        mCallback.onDialogSelected(position);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(inflater.getContext(), android.R.layout.simple_list_item_1,
                numbers_text);
        setListAdapter(adapter);
        return super.onCreateView(inflater, container, savedInstanceState);
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

        Activity a;

        if (context instanceof Activity){
            a=(Activity) context;
            try {
                mCallback = (onItemSelectedListener) a;
            } catch (ClassCastException e) {
                throw new ClassCastException(a.toString()
                        + " must implement OnItemSelectedListener");
            }
        }
    }

    public interface onItemSelectedListener {
        public void onDialogSelected(int position);
    }
}
