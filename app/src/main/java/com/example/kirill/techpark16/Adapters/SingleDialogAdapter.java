package com.example.kirill.techpark16.Adapters;


import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.example.kirill.techpark16.R;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;

public class SingleDialogAdapter extends BaseAdapter {
    private ArrayList<String> inList, outList;
    private Context context;

    String EMPTY = "empty";



    public SingleDialogAdapter(Context context, ArrayList<String> inList, ArrayList<String> outList) {
        this.inList = inList;
        this.outList = outList;
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
        Arrays.sort(outList.toArray(), Collections.reverseOrder());

        SetData setData = new SetData();
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        View view = inflater.inflate(R.layout.list_dialog_adapter, null);

        setData.inList = (TextView) view.findViewById(R.id.textView4);
        setData.outList = (TextView) view.findViewById(R.id.textView5);

        setData.inList.setText(inList.get(position));
        try {
        setData.outList.setText(outList.get(position));
        } catch (IndexOutOfBoundsException e){
            setData.outList.setText(EMPTY);
        }


        return view;
    }

    public class SetData {
        TextView inList,outList;
    }
}