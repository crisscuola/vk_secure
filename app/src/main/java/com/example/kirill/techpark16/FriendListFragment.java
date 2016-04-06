package com.example.kirill.techpark16;

import android.app.Activity;
import android.app.ListFragment;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import com.vk.sdk.api.VKApi;
import com.vk.sdk.api.VKApiConst;
import com.vk.sdk.api.VKParameters;
import com.vk.sdk.api.VKRequest;
import com.vk.sdk.api.VKResponse;
import com.vk.sdk.api.model.VKList;

import java.util.ArrayList;

/**
 * Created by konstantin on 06.04.16.
 */
public class FriendListFragment extends ListFragment {
    private onItemSelectedListener mCallback;
    private VKList list;


    @Override
    public void onListItemClick(ListView l, View v, int position, long id) {
        mCallback.onFriendSelected(position);
    }



    @Override
    public View onCreateView(final LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        VKRequest request_list_friend = VKApi.friends().get(VKParameters.from(VKApiConst.FIELDS, "first_name, last_name", "order", "hints"));

        request_list_friend.executeWithListener(new VKRequest.VKRequestListener() {
            @Override
            public void onComplete(VKResponse response) {

                super.onComplete(response);

                ArrayList<String> friends;

                //ArrayList<String> messages = new ArrayList<>();


                friends = (ArrayList) response.parsedModel;

               // list = (VKList) response.parsedModel;



                setListAdapter(new FriendListAdapter(inflater.getContext(), friends));


            }
        });


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
        public void onFriendSelected(int position);
    }


}
