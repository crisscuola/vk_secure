package com.example.kirill.techpark16;


import android.content.Context;
import android.content.Intent;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.vk.sdk.api.VKApiConst;
import com.vk.sdk.api.VKParameters;
import com.vk.sdk.api.VKRequest;
import com.vk.sdk.api.VKResponse;
import com.vk.sdk.api.model.VKApiDialog;
import com.vk.sdk.api.model.VKApiMessage;
import com.vk.sdk.api.model.VKList;

import org.json.JSONArray;
import org.json.JSONException;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;

public class DetailDialogAdapter extends BaseAdapter {
    private ArrayList<String> inList, outList;
    private Context context;




    public DetailDialogAdapter(Context context, ArrayList<String> inList, ArrayList<String> outList) {
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
        SetData setData = new SetData();
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        View view = inflater.inflate(R.layout.dialogs_fragment, null);

        setData.inList = (TextView) view.findViewById(R.id.user_name);
        setData.outList = (TextView) view.findViewById(R.id.msg);

//        setData.user_name.setText(inList.get(position));
//        setData.msg.setText(messages.get(position));


        return view;
    }

    public class SetData {
        TextView inList,outList;
    }
}