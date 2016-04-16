package com.example.kirill.techpark16;


import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;

public class MyDetailDialogAdapter extends BaseAdapter {
    private ArrayList<String> inList;
    private Context context;




    public MyDetailDialogAdapter(Context context, ArrayList<String> inList) {
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
        View view = inflater.inflate(R.layout.myself_dialog, null);

        setData.inList = (TextView) view.findViewById(R.id.textView5);
        //setData.outList = (TextView) view.findViewById(R.id.textView5);

        setData.inList.setText(inList.get(position));


        return view;
    }

    public class SetData {
        TextView inList,outList;
    }
}