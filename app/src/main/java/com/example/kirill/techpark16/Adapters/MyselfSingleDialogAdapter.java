package com.example.kirill.techpark16.Adapters;


import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.example.kirill.techpark16.R;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;

public class MyselfSingleDialogAdapter extends BaseAdapter {
    private ArrayList<String> inList;
    private Context context;




    public MyselfSingleDialogAdapter(Context context, ArrayList<String> inList) {
        this.inList = inList;
        this.context = context;
    }

    @Override
    public int getCount() {
        return inList.size();
    }

    @Override
    public Object getItem(int position) {
        return position;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {

        Arrays.sort(inList.toArray(), Collections.reverseOrder());

        SetData setData = new SetData();
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        Log.i("inList3", String.valueOf((inList.get(2))));
        View view = inflater.inflate(R.layout.fragment_dialog_myself, null);

        setData.inList = (TextView) view.findViewById(R.id.textView5);

        setData.inList.setText(inList.get(position));


        return view;
    }

    public class SetData {
        TextView inList;
    }
}