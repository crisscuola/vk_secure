package com.example.kirill.techpark16.Fragments;

import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.kirill.techpark16.Adapters.Person;
import com.example.kirill.techpark16.Adapters.RVAdapter;
import com.example.kirill.techpark16.Friend;
import com.example.kirill.techpark16.R;
import com.vk.sdk.api.VKApi;
import com.vk.sdk.api.VKApiConst;
import com.vk.sdk.api.VKParameters;
import com.vk.sdk.api.VKRequest;
import com.vk.sdk.api.VKResponse;
import com.vk.sdk.api.model.VKApiUser;
import com.vk.sdk.api.model.VKList;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by konstantin on 09.04.16
 */
public class FragmentFriendsList extends Fragment {

    RecyclerView recyclerView;
    VKList<VKApiUser> list = new VKList();
    RVAdapter adapter;
    TextView loading;

    private class DownloadingFriendList extends AsyncTask<Void, Void, Void> {

        @Override
        protected Void doInBackground(Void... params) {


            List<Friend> friends = Friend.listAll(Friend.class);
            Log.d("friends", String.valueOf(friends.size()));

            adapter = new RVAdapter(getContext(), getActivity().getSupportFragmentManager(), friends);
//            VKRequest request_list_friend = VKApi.friends().get(VKParameters.from(VKApiConst.FIELDS,
//                    "first_name, last_name, photo_100", "order", "hints"));
//
//            request_list_friend.executeWithListener(new VKRequest.VKRequestListener() {
//                @Override
//                public void onComplete(VKResponse response) {
//
//                super.onComplete(response);
//
//                list = (VKList<VKApiUser>) response.parsedModel;
//
//                LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getActivity());
//                linearLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
//                recyclerView.setLayoutManager(linearLayoutManager);
//                List<Person> persons = new ArrayList<>();
//                List<Friend> friends = Friend.listAll(Friend.class);
////                for (final VKApiUser user: list) {
////                    persons.add(new Person(user.first_name, user.last_name, user.photo_100, user.id));
////                }
//                adapter = new RVAdapter(getContext(), getActivity().getSupportFragmentManager(), friends);
//                recyclerView.setAdapter(adapter);
//                }
//            });
            return null;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();

            recyclerView.setVisibility(View.INVISIBLE);
            loading.setVisibility(View.VISIBLE);
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getActivity());
            linearLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
            recyclerView.setLayoutManager(linearLayoutManager);
            recyclerView.setAdapter(adapter);
            recyclerView.setVisibility(View.VISIBLE);
            loading.setVisibility(View.GONE);
        }
    }


    public View onCreateView(final LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_friends_list, null);
        recyclerView = (RecyclerView) view.findViewById(R.id.recyclerView);
        loading = (TextView) view.findViewById(R.id.loading);

        FragmentSettingsDialog.flag = true;

        new DownloadingFriendList().execute();

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
