package com.example.kirill.techpark16.Fragments;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
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

import com.example.kirill.techpark16.R;
import com.vk.sdk.api.VKApi;
import com.vk.sdk.api.VKApiConst;
import com.vk.sdk.api.VKError;
import com.vk.sdk.api.VKParameters;
import com.vk.sdk.api.VKRequest;
import com.vk.sdk.api.VKResponse;
import com.vk.sdk.api.model.VKApiMessage;
import com.vk.sdk.api.model.VKList;

import org.json.JSONArray;
import org.json.JSONException;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by konstantin on 10.04.16
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


    private class DownloadImageTask extends AsyncTask<String, Void, Bitmap> {
        ImageView bmImage;

        public DownloadImageTask(ImageView bmImage) {
            this.bmImage = bmImage;
        }

        protected Bitmap doInBackground(String... urls) {
            String urldisplay = urls[0];
            Bitmap mIcon11 = null;
            try {
                InputStream in = new java.net.URL(urldisplay).openStream();
                mIcon11 = BitmapFactory.decodeStream(in);
            } catch (Exception e) {
                e.printStackTrace();
            }
            return mIcon11;
        }

        protected void onPostExecute(Bitmap result) {
            bmImage.setImageBitmap(result);
        }
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

        final VKRequest request = new VKRequest("status.get", VKParameters.from(VKApiConst.USER_ID, id));

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

        VKRequest request_info = new VKRequest("users.get", VKParameters.from(VKApiConst.USER_IDS,id,
                VKApiConst.FIELDS, "photo_200,bdate,city"));

        request_info.executeWithListener(new VKRequest.VKRequestListener() {
            @Override
            public void onComplete(VKResponse response) {

            String photo_url = " ";
            String city = " ";
            String bdate = "";

            try {
                JSONArray array = response.json.getJSONArray("response");

                photo_url = array.getJSONObject(0).getString("photo_200");
                city = array.getJSONObject(0).getJSONObject("city").getString("title");
                bdate = array.getJSONObject(0).getString("bdate");

                String[] arraybdate = bdate.split("\\.");

                bdate = arraybdate[0];

                HashMap<String, String> months = new HashMap<>();

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


            new DownloadImageTask((ImageView) view.findViewById(R.id.avatar)).execute(photo_url);

            super.onComplete(response);
            }
        });


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
