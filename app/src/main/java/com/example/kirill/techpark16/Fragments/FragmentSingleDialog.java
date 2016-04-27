package com.example.kirill.techpark16.Fragments;


import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.app.ListFragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import com.example.kirill.techpark16.Adapters.MyselfSingleDialogAdapter;
import com.example.kirill.techpark16.Adapters.SingleDialogAdapter;
import com.example.kirill.techpark16.PublicKeyHandler;
import com.example.kirill.techpark16.PublicKeysTable;
import com.example.kirill.techpark16.R;
import com.vk.sdk.VKSdk;
import com.vk.sdk.api.VKApi;
import com.vk.sdk.api.VKApiConst;
import com.vk.sdk.api.VKParameters;
import com.vk.sdk.api.VKRequest;
import com.vk.sdk.api.VKResponse;
import com.vk.sdk.api.model.VKList;

import org.json.JSONObject;

import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by kirill on 02.04.16
 */
public class FragmentSingleDialog extends ListFragment implements SwipeRefreshLayout.OnRefreshListener {


    public static String USER_ID = "user_id";
    public static String IN_LIST = "inList";
    public static String OUT_LIST = "outList";

    ArrayList<String> inList = new ArrayList<>();
    ArrayList<String> outList = new ArrayList<>();
    int id;


    EditText text;
    ListView listView;
    Button send;

    static int title_id;
    VKList list_s;

    String friendKey;

    private static SwipeRefreshLayout mswipeRefreshLayout;

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

    @Override
    public void onRefresh() {
       Log.i("REFRESH", "REFRESH");

        FragmentTransaction ft = getFragmentManager().beginTransaction();
        ft.detach(this).attach(this).commit();
    }

    private class LongOperation extends AsyncTask<String, Void, String> {
        String response;
        JSONObject json;
        @Override
        protected String doInBackground(String... params) {

            try {
                friendKey = PublicKeyHandler.downloadFriendPublicKey(id);
                Log.d("resp_id", String.valueOf(id));
                Log.d("resp_db_len", String.valueOf(PublicKeysTable.listAll(PublicKeysTable.class).size()));
            } catch ( NoSuchAlgorithmException | InvalidKeySpecException e) {
                e.printStackTrace();
            }
            return friendKey;
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

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState){
        View view = inflater.inflate(R.layout.fragment_single_dialog, null);

        mswipeRefreshLayout = (SwipeRefreshLayout) view.findViewById(R.id.refresh);
        mswipeRefreshLayout.setOnRefreshListener(this);

        inList = getArguments().getStringArrayList(IN_LIST);
        outList = getArguments().getStringArrayList(OUT_LIST);
        id = getArguments().getInt(USER_ID);

        Log.d("inList_size", String.valueOf(inList.size()));
        for (String msg : inList){
            try {
                Log.d("inList_decr", ActivityBase.encryptor.decode(msg));
            } catch (Exception e) {
                e.printStackTrace();
            }
        }


        new LongOperation().execute();

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
                    if (!friendKey.equals("none")) {
                        Log.d("resp_friend_pk_fragm", friendKey);
                        ActivityBase.encryptor.setPublicKey(friendKey);
                        messageToSend = ActivityBase.encryptor.encode(messageToSend);
                    } else {
                        Toast.makeText(getContext(),"The friend hasn't started the dialog.",Toast.LENGTH_SHORT).show();
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
                text.setText("");
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
