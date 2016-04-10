package com.example.kirill.techpark16;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.vk.sdk.api.model.VKList;

/**
 * Created by konstantin on 06.04.16.
 */
public class FriendListAdapter extends BaseAdapter {
    private VKList friends;
    private Context context;

    public FriendListAdapter(Context context,VKList friends) {
        this.friends = friends;
        this.context = context;
    }


    @Override
    public int getCount() {
        return friends.size();
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
    public View getView(int position, View convertView, ViewGroup parent) {


        SetData setData = new SetData();
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View view = inflater.inflate(R.layout.test_friend_list, null);

        setData.friends = (TextView) view.findViewById(R.id.user_name);
       // setData.friends.setText(friends.get(position));

        return view;
    }

    public class SetData {
        TextView friends;
    }
}

