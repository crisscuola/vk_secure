package com.example.kirill.techpark16.Fragments;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.example.kirill.techpark16.Adapters.RecyclerViewAdapter;
import com.example.kirill.techpark16.R;
import com.vk.sdk.api.VKApi;
import com.vk.sdk.api.VKApiConst;
import com.vk.sdk.api.VKParameters;
import com.vk.sdk.api.VKRequest;
import com.vk.sdk.api.VKResponse;
import com.vk.sdk.api.model.VKList;

import java.util.ArrayList;
import java.util.zip.Inflater;

/**
 * Created by konstantin on 09.04.16.
 */
public class FragmentFriendsList extends android.support.v4.app.ListFragment {

    RecyclerView recyclerView;
    RecyclerViewAdapter recyclerViewAdapter;
    VKList list = new VKList();
    private onItemSelectedListener mCallback;


    @Override
    public void onListItemClick(ListView l, View v, int position, long id) {
        mCallback.onFriendSelected(position);
    }

    public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
    }

    public View onCreateView(final LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_friends_list, null);
        recyclerView = (RecyclerView) view.findViewById(R.id.recyclerView);


        ArrayList<String> arrayList = new ArrayList<String>();
        for (int i = 0; i < 10; i++){
            arrayList.add(String.valueOf(i));
        }

        recyclerView.setHasFixedSize(true);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getActivity());
        linearLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        recyclerView.setLayoutManager(linearLayoutManager);
        recyclerViewAdapter = new RecyclerViewAdapter(getActivity(), arrayList);
        recyclerView.setAdapter(recyclerViewAdapter);
        recyclerView.setItemAnimator(new DefaultItemAnimator());


        VKRequest request_list_friend = VKApi.friends().get(VKParameters.from(VKApiConst.FIELDS,
                "first_name, last_name", "order", "hints"));

        request_list_friend.executeWithListener(new VKRequest.VKRequestListener() {
            @Override
            public void onComplete(VKResponse response) {

                super.onComplete(response);

                list = (VKList) response.parsedModel;


            }
        });

        return super.onCreateView(inflater, container, savedInstanceState);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        VKRequest request_list_friend = VKApi.friends().get(VKParameters.from(VKApiConst.FIELDS, "first_name, last_name", "order", "hints"));

        request_list_friend.executeWithListener(new VKRequest.VKRequestListener() {
            @Override
            public void onComplete(VKResponse response) {

                super.onComplete(response);

                list = (VKList) response.parsedModel;

                ArrayAdapter<String> adapter = new ArrayAdapter<String>(getActivity(), android.R.layout.simple_list_item_1, list);
                setListAdapter(adapter);

            }
        });

    }

    @Override
    public void onResume() {
        super.onResume();
        getActivity().setTitle(R.string.friends_title);
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
        public void onFriendSelected(int position);
    }

}
