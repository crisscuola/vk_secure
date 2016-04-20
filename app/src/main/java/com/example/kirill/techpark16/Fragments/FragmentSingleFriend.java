package com.example.kirill.techpark16.Fragments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.example.kirill.techpark16.R;
import com.vk.sdk.api.VKApi;
import com.vk.sdk.api.VKApiConst;
import com.vk.sdk.api.VKParameters;
import com.vk.sdk.api.VKRequest;
import com.vk.sdk.api.VKResponse;
import com.vk.sdk.api.model.VKApiMessage;
import com.vk.sdk.api.model.VKList;

import org.json.JSONArray;
import org.json.JSONException;

import java.util.ArrayList;

/**
 * Created by konstantin on 10.04.16.
 */
public class FragmentSingleFriend extends Fragment {

    public static String USER_ID = "user_id";
    public static String FIRST_NAME = "first_name";
    public static String LAST_NAME = "last_name";


    int id = 0;
    String first_name;
    String last_name;
    Button send;

    static int title_id;
    VKList list_s;

    public static FragmentSingleFriend getInstance(int user_id, String first_name, String last_name){
        FragmentSingleFriend detailDialogFragment = new FragmentSingleFriend();

        Bundle bundle = new Bundle();
        bundle.putInt(USER_ID, user_id);
        bundle.putString(FIRST_NAME, first_name);
        bundle.putString(LAST_NAME, last_name);

        title_id = user_id;

        detailDialogFragment.setArguments(bundle);
        return detailDialogFragment;
    }
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_single_friend, null);

        TextView friend_name = (TextView) view.findViewById(R.id.friends_name);

        id = getArguments().getInt(USER_ID);
        first_name = getArguments().getString(FIRST_NAME);
        last_name = getArguments().getString(LAST_NAME);

        friend_name.setText(first_name + " " + last_name);


        send = (Button) view.findViewById(R.id.button_write);
        send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                VKRequest request = new VKRequest("messages.getHistory", VKParameters.from(VKApiConst.USER_ID, id));
                request.executeWithListener(new VKRequest.VKRequestListener() {
                    @Override
                    public void onComplete(VKResponse response) {
                        super.onComplete(response);

                        final ArrayList<String> inList = new ArrayList<>();
                        final ArrayList<String> outList = new ArrayList<>();
                        try {
                            JSONArray array = response.json.getJSONObject("response").getJSONArray("items");

                            VKApiMessage[] msg = new VKApiMessage[array.length()];

                            for (int i = 0; i < array.length(); i++) {
                                VKApiMessage mes = new VKApiMessage(array.getJSONObject(i));
                                msg[i] = mes;
                            }


                            for (VKApiMessage mess : msg) {
                                if (mess.out) {
                                    outList.add(mess.body);
                                } else {
                                    inList.add(mess.body);
                                }

                            }
                            Log.i("inList", String.valueOf((inList.get(2))));
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }


                        FragmentSingleDialog newFragment = FragmentSingleDialog.getInstance(id, inList, outList);
                        FragmentTransaction transaction = getActivity().getSupportFragmentManager().beginTransaction();
                        transaction.replace(R.id.fragmentPlace, newFragment);
                        transaction.addToBackStack(null);
                        transaction.commit();
                    }

                });


            }
        });


        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        final String[] name_id = {""};

        VKRequest my_request = VKApi.users().get(VKParameters.from(VKApiConst.USER_IDS, title_id, VKApiConst.FIELDS, "first_name, last_name"));
        my_request.executeWithListener(new VKRequest.VKRequestListener() {
            @Override
            public void onComplete(VKResponse response) {
                super.onComplete(response);
                list_s = (VKList) response.parsedModel;

                name_id[0] = String.valueOf(FragmentSingleFriend.this.list_s.getById(title_id));
                getActivity().setTitle(name_id[0]);
            }
        });

        getActivity().findViewById(R.id.toolbar).findViewById(R.id.toolbar_button).setVisibility(View.INVISIBLE);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {


    }


}
