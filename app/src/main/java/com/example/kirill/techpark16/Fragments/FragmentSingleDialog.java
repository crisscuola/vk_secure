package com.example.kirill.techpark16.Fragments;


import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.ListFragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;

import com.example.kirill.techpark16.Adapters.MyselfSingleDialogAdapter;
import com.example.kirill.techpark16.Adapters.SingleDialogAdapter;
import com.example.kirill.techpark16.HttpConnectionHandler;
import com.example.kirill.techpark16.PublicKeysTable;
import com.example.kirill.techpark16.R;
import com.vk.sdk.VKSdk;
import com.vk.sdk.api.VKApi;
import com.vk.sdk.api.VKApiConst;
import com.vk.sdk.api.VKParameters;
import com.vk.sdk.api.VKRequest;
import com.vk.sdk.api.VKResponse;
import com.vk.sdk.api.model.VKList;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by kirill on 02.04.16
 */
public class FragmentSingleDialog extends ListFragment {


    public static String USER_ID = "user_id";
    public static String IN_LIST = "inList";
    public static String OUT_LIST = "outList";

    ArrayList<String> inList = new ArrayList<>();
    ArrayList<String> outList = new ArrayList<>();
    int id = 0;


    EditText text;
    ListView listView;
    Button send;

    static int title_id;
    VKList list_s;

    public static FragmentSingleDialog getInstance(int user_id, ArrayList<String> inList, ArrayList<String> outList) {
        FragmentSingleDialog fragmentSingleDialog = new FragmentSingleDialog();
        Bundle bundle = new Bundle();
        bundle.putInt(USER_ID, user_id);
        bundle.putStringArrayList(IN_LIST, inList);
        bundle.putStringArrayList(OUT_LIST, outList);

        title_id = user_id;

        fragmentSingleDialog.setArguments(bundle);
        return fragmentSingleDialog;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState){
        View view = inflater.inflate(R.layout.fragment_single_dialog, null);


        class LongOperation extends AsyncTask<String, Void, String> {
            String response;
            JSONObject json;
            @Override
            protected String doInBackground(String... params) {

                HttpConnectionHandler client = new HttpConnectionHandler();
                try {
                    response = client.doGetRequest(String.valueOf(6759461));
                    Log.d("resp_get1", response);
                    response = client.doPostRequest(String.valueOf(6759461), ActivityBase.publicKey);
                    json = new JSONObject(response);
                    Log.d("resp_post", response);
                    response = client.doGetRequest(String.valueOf(6759461));
                    Log.d("resp_get2", response);
                } catch (IOException e) {
                    e.printStackTrace();
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                return "Executed";
            }

            @Override
            protected void onPostExecute(String result) {
                // might want to change "executed" for the returned string passed
                // into onPostExecute() but that is upto you
            }

            @Override
            protected void onPreExecute() {}

            @Override
            protected void onProgressUpdate(Void... values) {}
        }

        new LongOperation().execute();


        inList = getArguments().getStringArrayList(IN_LIST);
        outList = getArguments().getStringArrayList(OUT_LIST);
        id = getArguments().getInt(USER_ID);

        text = (EditText) view.findViewById(R.id.textmsg);
        listView = (ListView) view.findViewById(R.id.listmsg);

        if (id == Integer.parseInt(VKSdk.getAccessToken().userId)) {
            listView.setAdapter(new MyselfSingleDialogAdapter(view.getContext(), inList));
        } else {

            listView.setAdapter(new SingleDialogAdapter(view.getContext(), inList, outList));
        }
        send = (Button) view.findViewById(R.id.sendmsg);
        send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v){

                VKRequest request;
                String messageToSend = text.getText().toString();

                try {
                    messageToSend = ActivityBase.encryptionFriend.encode(messageToSend);
                } catch (Exception e) {
                    e.printStackTrace();
                }



                request = new VKRequest("messages.send", VKParameters.from(VKApiConst.USER_ID, id,
                        VKApiConst.MESSAGE, messageToSend));

                request.executeWithListener(new VKRequest.VKRequestListener() {
                    @Override
                    public void onComplete(VKResponse response) {
                        super.onComplete(response);
                    }
                });
            }
        });

        return view;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {

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

                name_id[0] = String.valueOf(FragmentSingleDialog.this.list_s.getById(title_id));
                getActivity().setTitle(name_id[0]);
            }
        });

        getActivity().findViewById(R.id.toolbar).findViewById(R.id.toolbar_button).setVisibility(View.VISIBLE);
    }
}
