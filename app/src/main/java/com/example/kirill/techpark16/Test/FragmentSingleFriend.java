package com.example.kirill.techpark16.Test;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.kirill.techpark16.DetailDialogFragment;
import com.example.kirill.techpark16.R;
import com.vk.sdk.api.VKApiConst;
import com.vk.sdk.api.VKParameters;
import com.vk.sdk.api.VKRequest;
import com.vk.sdk.api.VKResponse;
import com.vk.sdk.api.model.VKApiMessage;

import org.json.JSONArray;
import org.json.JSONException;

import java.util.ArrayList;

/**
 * Created by konstantin on 10.04.16.
 */
public class FragmentSingleFriend extends Fragment {

    public static String USER_ID = "user_id";

    int id = 0;
    Button send;


    public static FragmentSingleFriend getInstance(int user_id){
        FragmentSingleFriend detailDialogFragment = new FragmentSingleFriend();

        Bundle bundle = new Bundle();
        bundle.putInt(USER_ID, user_id);
        detailDialogFragment.setArguments(bundle);
        return detailDialogFragment;
    }
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.single_friend_fragment, null);

        EditText friend_id = (EditText) view.findViewById(R.id.friend_id);
        id = getArguments().getInt(USER_ID);
        Log.i("id", String.valueOf(id));
        friend_id.setText(String.valueOf(id));

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


                        Toast.makeText(getActivity(), "Clicked WRITE MESSAGE", Toast.LENGTH_SHORT).show();
                        DetailDialogFragment newFragment = DetailDialogFragment.getInstance(id, inList, outList);
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
    public void onViewCreated(View view, Bundle savedInstanceState) {


    }


}
