package com.example.kirill.techpark16.Adapters;

import android.os.AsyncTask;
import android.support.v4.app.FragmentManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

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

/**
 * Created by kirill on 07.05.16
 */
public class RecyclerViewAdapter extends RecyclerView.Adapter<RecyclerViewAdapter.ViewHolder>{
    private VKList friendList = new VKList();
    private FragmentManager fragmentManager;

    public  RecyclerViewAdapter(FragmentManager fragmentManager, VKList friendList){
        this.fragmentManager = fragmentManager;
        this.friendList = friendList;
    }

    private class FriendNameLoader extends AsyncTask {
        int position;
        int id;
        String firstname,lastname;
        VKList list;

        @Override
        protected VKList  doInBackground(Object[] params) {
            final VKRequest request_list_friend = VKApi.friends().get(VKParameters.from(VKApiConst.FIELDS,
                    "first_name, last_name", "order", "hints"));
            position = (int)params[0];
            request_list_friend.executeWithListener(new VKRequest.VKRequestListener() {
                @Override
                public void onComplete(VKResponse response) {

                    super.onComplete(response);

                    list = (VKList) response.parsedModel;

                    VKApiModel model = list.get(position);
                    try {
                        id = model.fields.getInt("id");
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }


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
            return list;
        }
    }


    @Override
    public RecyclerViewAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemLayoutView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.friendlist_item, null);
        return new ViewHolder(itemLayoutView);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, final int position) {
        holder.textView.setText(String.valueOf(friendList.get(position)));

        holder.textView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new FriendNameLoader().execute(position);
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
