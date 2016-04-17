package com.example.kirill.techpark16.Fragments;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.v4.app.ListFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import com.example.kirill.techpark16.R;
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
public class FragmentFriendList extends ListFragment {

    public static String DIALOG_NO = "dialog_no";
    public static String FRIEND_LIST = "friends";

    private onFriendSelectedListener mCallback;
    private VKList list;


    public static FragmentFriendList getInstance(int dialog_no, VKList friends){
       FragmentFriendList fragmentFriendList = new FragmentFriendList();
//        Log.i("inList2", String.valueOf((inList.get(2))));
        Bundle bundle = new Bundle();
        bundle.putInt(DIALOG_NO, dialog_no);
        Parcelable p = friends;
        bundle.putParcelable(FRIEND_LIST, p);
//        bundle.putParcelableArrayList(FRIEND_LIST, friends);
//        bundle.putStringArrayList(FRIEND_LIST, friends);



        fragmentFriendList.setArguments(bundle);
        return fragmentFriendList;
    }

    @Override
    public void onListItemClick(ListView l, View v, int position, long id) {
        mCallback.onFriendSelected(position);
    }




    @Override
    public View onCreateView(final LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_friends_list, null);

        VKRequest request_list_friend = VKApi.friends().get(VKParameters.from(VKApiConst.FIELDS, "first_name, last_name", "order", "hints"));

        request_list_friend.executeWithListener(new VKRequest.VKRequestListener() {
            @Override
            public void onComplete(VKResponse response) {

                super.onComplete(response);

                ArrayList<String> friends;

                //ArrayList<String> messages = new ArrayList<>();


//                friends = (ArrayList) response.parsedModel;

                list = (VKList) response.parsedModel;



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
                mCallback = (onFriendSelectedListener) a;
            } catch (ClassCastException e) {
                throw new ClassCastException(a.toString()
                        + " must implement OnItemSelectedListener");

            }
        }
    }

    public interface onFriendSelectedListener {
        public void onFriendSelected(int position);
    }


}
