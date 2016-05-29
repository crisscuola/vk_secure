package com.example.kirill.techpark16.Fragments;

import android.app.Activity;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.ListFragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.example.kirill.techpark16.Friend;
import com.example.kirill.techpark16.R;
import com.vk.sdk.api.VKApi;
import com.vk.sdk.api.VKApiConst;
import com.vk.sdk.api.VKParameters;
import com.vk.sdk.api.VKRequest;
import com.vk.sdk.api.VKResponse;
import com.vk.sdk.api.model.VKList;

import java.util.List;
import java.util.concurrent.ExecutionException;

/**
 * Created by konstantin on 10.04.16
 */
public class FragmentFriendsSend extends ListFragment {

    VKList list = new VKList();
    private onItemSelectedListener mCallback;
    ArrayAdapter<String> adapter;
    TextView loading;
    ListView listView;


    @Override
    public void onListItemClick(ListView l, View v, int position, long id) {
        mCallback.onFriendSendSelected(position);
    }

    public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
    }

    public View onCreateView(final LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        final View view = inflater.inflate(R.layout.fragment_friend_send, null);
        FragmentSettingsDialog.flag = false;
        loading = (TextView) view.findViewById(R.id.send_loading);
        //listView = (ListView) view.findViewById(R.id.list);

        new DownloadingFriends().execute();

        return view;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);


//        VKRequest request_list_friend = VKApi.friends().get(VKParameters.from(VKApiConst.FIELDS, "first_name, last_name", "order", "hints"));
//
//        request_list_friend.executeWithListener(new VKRequest.VKRequestListener() {
//            @Override
//            public void onComplete(VKResponse response) {
//
//                super.onComplete(response);
//
//                list = (VKList) response.parsedModel;
//
//                ArrayAdapter<String> adapter = new ArrayAdapter<String>(getActivity(), R.layout.friend_send_list, list);
//                setListAdapter(adapter);
//
//            }
//        });

    }

    @Override
    public void onResume() {
        super.onResume();
        getActivity().setTitle(R.string.send);
        getActivity().findViewById(R.id.toolbar).findViewById(R.id.toolbar_button).setVisibility(View.INVISIBLE);
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

        Activity activity;

        if (context instanceof Activity){
            activity=(Activity) context;
            try {
                mCallback = (onItemSelectedListener) activity;
            } catch (ClassCastException e) {
                throw new ClassCastException(activity.toString()
                        + " must implement OnItemSelectedListener");
            }
        }
    }

    public interface onItemSelectedListener {
        void onFriendSendSelected(int position);
    }

    private class DownloadingFriends extends AsyncTask<String, Void, String> {
        @Override
        protected String doInBackground(String... params) {
            VKRequest request_list_friend = VKApi.friends().get(VKParameters.from(VKApiConst.FIELDS,
                    "first_name, last_name", "order", "hints"));

            request_list_friend.executeWithListener(new VKRequest.VKRequestListener() {
                @Override
                public void onComplete(VKResponse response) {
                super.onComplete(response);

                list = (VKList) response.parsedModel;
                Log.d("friends", String.valueOf(list.size()));
                adapter = new ArrayAdapter<String>(getActivity(), R.layout.friend_send_list, list);
                setListAdapter(adapter);

                }
            });
            return null;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            loading.setVisibility(View.VISIBLE);
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            loading.setVisibility(View.GONE);
            //listView.setAdapter(adapter);
//            adapter = new ArrayAdapter<String>(getActivity(), R.layout.friend_send_list, list);
//            setListAdapter(adapter);
        }
    }

}

