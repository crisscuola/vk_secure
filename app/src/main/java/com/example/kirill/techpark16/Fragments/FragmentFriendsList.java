package com.example.kirill.techpark16.Fragments;

import android.app.Activity;
import android.app.FragmentTransaction;
import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.example.kirill.techpark16.Adapters.RecyclerViewAdapter;
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
import java.util.zip.Inflater;

/**
 * Created by konstantin on 09.04.16.
 */
public class FragmentFriendsList extends Fragment {

    RecyclerView recyclerView;
    RecyclerViewAdapter recyclerViewAdapter;
    VKList list = new VKList();
    FragmentTransaction fragmentTransaction;



    public View onCreateView(final LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_friends_list, null);
        recyclerView = (RecyclerView) view.findViewById(R.id.recyclerView);

        VKRequest request_list_friend = VKApi.friends().get(VKParameters.from(VKApiConst.FIELDS,
                "first_name, last_name", "order", "hints"));

        request_list_friend.executeWithListener(new VKRequest.VKRequestListener() {
            @Override
            public void onComplete(VKResponse response) {

                super.onComplete(response);

                list = (VKList) response.parsedModel;

                LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getActivity());
                linearLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
                recyclerView.setLayoutManager(linearLayoutManager);
                recyclerView.setItemAnimator(new DefaultItemAnimator());
                recyclerViewAdapter = new RecyclerViewAdapter(getActivity().getSupportFragmentManager(),list);
                recyclerView.setAdapter(recyclerViewAdapter);
            }
        });

        return view;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

    }

    @Override
    public void onResume() {
        super.onResume();
        getActivity().setTitle(R.string.friends_title);
        getActivity().findViewById(R.id.toolbar).findViewById(R.id.toolbar_button).setVisibility(View.INVISIBLE);
    }



}
