package com.example.kirill.techpark16.Fragments;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.kirill.techpark16.CircleTransform;
import com.example.kirill.techpark16.Friend;
import com.example.kirill.techpark16.R;
import com.squareup.picasso.Picasso;
import com.vk.sdk.api.VKApiConst;
import com.vk.sdk.api.VKError;
import com.vk.sdk.api.VKParameters;
import com.vk.sdk.api.VKRequest;
import com.vk.sdk.api.VKResponse;
import com.vk.sdk.api.model.VKApiMessage;
import com.vk.sdk.api.model.VKList;

import org.json.JSONArray;
import org.json.JSONException;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Created by konstantin on 10.04.16
 */
public class FragmentSingleFriend extends Fragment {

    public static String USER_ID = "user_id";
    public static String FIRST_NAME = "first_name";
    public static String LAST_NAME = "last_name";

    HashMap<String, String> months = new HashMap<>();

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

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

        months.put("1", " января");
        months.put("2", " февраля");
        months.put("3", " марта");
        months.put("4", " апреля");
        months.put("5", " мая");
        months.put("6", " июня");
        months.put("7", " июля");
        months.put("8", " августа");
        months.put("9", " сентября");
        months.put("10", " октября");
        months.put("11", " ноября");
        months.put("12"," декабря");
    }


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        final View view = inflater.inflate(R.layout.fragment_single_friend, null);

        FragmentSettingsDialog.flag = false;

        TextView friend_name = (TextView) view.findViewById(R.id.friends_name);
        final TextView friend_status = (TextView) view.findViewById(R.id.friends_status);

        id = getArguments().getInt(USER_ID);
        first_name = getArguments().getString(FIRST_NAME);
        last_name = getArguments().getString(LAST_NAME);
        String name = first_name + " " + last_name;

        friend_name.setText(name);

        send = (Button) view.findViewById(R.id.button_write);
        send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                VKRequest request = new VKRequest("messages.getHistory", VKParameters.from(VKApiConst.USER_ID, id));
                request.executeWithListener(new VKRequest.VKRequestListener() {
                    @Override
                    public void onComplete(VKResponse response) {
                    super.onComplete(response);

                    final ArrayList<VKApiMessage> msg = new ArrayList<>();
                    final ArrayList<Integer> ids = new ArrayList<>();

                    try {
                        JSONArray array = response.json.getJSONObject("response").getJSONArray("items");

                        for (int i = 0; i < array.length(); i++) {
                            VKApiMessage mes = new VKApiMessage(array.getJSONObject(i));
                            msg.add(mes);
                            if (!mes.out)
                                ids.add(mes.id);
                        }

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }


                    FragmentSingleDialog newFragment = FragmentSingleDialog.getInstance(id, msg, ids);
                    FragmentTransaction transaction = getActivity().getSupportFragmentManager().beginTransaction();
                    transaction.replace(R.id.fragmentPlace, newFragment);
                    transaction.addToBackStack(null);
                    transaction.commit();
                    }

                });
            }
        });


        VKRequest request_info = new VKRequest("users.get", VKParameters.from(VKApiConst.USER_IDS,id,
                VKApiConst.FIELDS, "photo_200, bdate, city", "online"));

        request_info.executeWithListener(new VKRequest.VKRequestListener() {
            @Override
            public void onComplete(VKResponse response) {

            String photoUrl = " ";
            String city = "Не указан";
            String bdate = "Не указан";
            String online;

            try {
                JSONArray array = response.json.getJSONArray("response");
                Log.d("online", String.valueOf(array.getJSONObject(0)));

                List<Friend> avatar = Friend.find(Friend.class, "friend_id = ?", String.valueOf(id));
                if (avatar.size() != 0)
                    photoUrl = avatar.get(0).getPhotoUrl();
                else
                    photoUrl = array.getJSONObject(0).getString("photo_200");
                city = array.getJSONObject(0).getJSONObject("city").getString("title");
                bdate = array.getJSONObject(0).getString("bdate");

                String[] arraybdate = bdate.split("\\.");

                bdate = arraybdate[0];

                bdate += months.get(arraybdate[1]);

                if (arraybdate.length == 3) {
                    bdate += " ";
                    bdate += arraybdate[2];
                }

            } catch (JSONException e) {
                e.printStackTrace();
            }

            TextView name = (TextView) view.findViewById(R.id.city);
            name.setText(city);

            TextView b_date = (TextView)  view.findViewById(R.id.b_date);
            b_date.setText(bdate);

            ImageView avatar = (ImageView) view.findViewById(R.id.avatar);

            Picasso.with(getContext()).load(photoUrl).transform(new CircleTransform())
                        .placeholder(R.drawable.placeholder_dark)
                        .into(avatar);

            super.onComplete(response);
            }
        });

        VKRequest request = new VKRequest("status.get", VKParameters.from(VKApiConst.USER_ID, id));

        request.executeWithListener(new VKRequest.VKRequestListener() {
            @Override
            public void onComplete(VKResponse response) {
                super.onComplete(response);

                String status;
                try {

                    status = (String) response.json.getJSONObject("response").get("text");
                    friend_status.setText(status);

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onError(VKError error) {
                Log.i("len", String.valueOf(error.errorCode));
            }
        });

        return view;
    }

    @Override
    public void onResume() {
        super.onResume();

        getActivity().setTitle(first_name);
        getActivity().findViewById(R.id.toolbar).findViewById(R.id.toolbar_button).setVisibility(View.INVISIBLE);
    }

}
