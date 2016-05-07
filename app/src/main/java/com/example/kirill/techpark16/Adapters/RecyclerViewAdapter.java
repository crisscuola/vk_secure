package com.example.kirill.techpark16.Adapters;

import android.content.Context;
import android.support.v4.app.FragmentManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.kirill.techpark16.ChatMessage;
import com.example.kirill.techpark16.Fragments.FragmentSingleFriend;
import com.example.kirill.techpark16.R;
import com.vk.sdk.api.VKApi;
import com.vk.sdk.api.VKApiConst;
import com.vk.sdk.api.VKParameters;
import com.vk.sdk.api.VKRequest;
import com.vk.sdk.api.VKResponse;
import com.vk.sdk.api.model.VKApiModel;
import com.vk.sdk.api.model.VKList;

import org.json.JSONException;

import java.util.ArrayList;

/**
 * Created by kirill on 07.05.16.
 */
public class RecyclerViewAdapter extends RecyclerView.Adapter<RecyclerViewAdapter.ViewHolder> {
    private VKList friendList = new VKList();
    private FragmentManager fragmentManager;

    public  RecyclerViewAdapter(FragmentManager fragmentManager, VKList friendList){
        this.fragmentManager = fragmentManager;
        this.friendList = friendList;
    }

    public static class MyViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        public TextView txtView;
        public MyViewHolderClicks mListener;


        public MyViewHolder(View itemLayoutView, MyViewHolderClicks listener) {
            super(itemLayoutView);
            mListener = listener;
            txtView = (TextView) itemLayoutView.findViewById(R.id.friend_name);
            itemLayoutView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            mListener.onPotato(v);
        }

        public static interface MyViewHolderClicks {
            public void onPotato(View caller);
        }
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemLayoutView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.friendlist_item, null);
        ViewHolder viewHolder = new ViewHolder(itemLayoutView);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, final int position) {
        holder.textView.setText(String .valueOf(friendList.get(position)));

        holder.textView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d("click", String.valueOf(holder.textView.getText()));

                VKRequest request_list_friend = VKApi.friends().get(VKParameters.from(VKApiConst.FIELDS, "first_name, last_name", "order", "hints"));

                request_list_friend.executeWithListener(new VKRequest.VKRequestListener() {
                    @Override
                    public void onComplete(VKResponse response) {

                        super.onComplete(response);

                        VKList list = new VKList();

                        list = (VKList) response.parsedModel;

                        int id = 0;

                        VKApiModel model = list.get(position);
                        try {
                            id = model.fields.getInt("id");
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                        String firstname = "";
                        String lastname = "";


                        try {
                            firstname = model.fields.getString("first_name");
                            lastname = model.fields.getString("last_name");

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                        FragmentSingleFriend newFragment = FragmentSingleFriend.getInstance(id, firstname, lastname);
                        fragmentManager.beginTransaction()
                        .replace(R.id.fragmentPlace, newFragment)
                        .addToBackStack(null)
                        .commit();
                    }
                });

            }
        });
    }

    @Override
    public int getItemCount() {
        return friendList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder{
        public TextView textView;

        public ViewHolder(View itemLayoutView) {
            super(itemLayoutView);
            textView = (TextView) itemLayoutView.findViewById(R.id.friend_name);
        }
    }
}
