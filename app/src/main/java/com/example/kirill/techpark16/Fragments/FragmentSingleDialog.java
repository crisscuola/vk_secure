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

import com.example.kirill.techpark16.Adapters.SingleDialogAdapter;
import com.example.kirill.techpark16.ChatMessage;
import com.example.kirill.techpark16.MyMessagesHistory;
import com.example.kirill.techpark16.PublicKeyHandler;
import com.example.kirill.techpark16.R;
import com.vk.sdk.api.VKApi;
import com.vk.sdk.api.VKApiConst;
import com.vk.sdk.api.VKParameters;
import com.vk.sdk.api.VKRequest;
import com.vk.sdk.api.VKResponse;
import com.vk.sdk.api.model.VKApiMessage;
import com.vk.sdk.api.model.VKList;

import org.json.JSONException;

import java.io.IOException;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;

/**
 * Created by kirill on 02.04.16
 */
public class FragmentSingleDialog extends ListFragment implements SwipeRefreshLayout.OnRefreshListener {


    public static final String USER_ID = "user_id";
    public static final String IN_LIST = "inList";
    public static final String OUT_LIST = "outList";
    public static final String MESSAGES = "messages";
    private final String MEDIA_MSG = "[MEDIA MESSAGE]";
    private final String PREFIX = "cpslbs_";

    ArrayList<String> inList = new ArrayList<>();
    ArrayList<String> outList = new ArrayList<>();
    ArrayList<VKApiMessage> vkMessages = new ArrayList<>();
    SingleDialogAdapter singleDialogAdapter;
    int id;
    boolean sendFlag = false;

    EditText text;
    ListView listView;
    Button send;
    static Integer count = 0;
    String title;

    static int title_id;
    VKList list_s;

    String friendKey;

    private static SwipeRefreshLayout mswipeRefreshLayout;


    public static FragmentSingleDialog getInstance(int user_id, ArrayList<String> inList,
                                                   ArrayList<String> outList, ArrayList<VKApiMessage> vkMsgs) {
        FragmentSingleDialog fragmentSingleDialog = new FragmentSingleDialog();
        Bundle bundle = new Bundle();
        bundle.putInt(USER_ID, user_id);
        bundle.putStringArrayList(IN_LIST, inList);
        bundle.putStringArrayList(OUT_LIST, outList);
        bundle.putParcelableArrayList(MESSAGES, vkMsgs);

        title_id = user_id;

        fragmentSingleDialog.setArguments(bundle);
        return fragmentSingleDialog;
    }

    @Override
    public void onRefresh() {
       Log.i("REFRESH", "REFRESH");
        title = title.substring(0, title.length() -1);
        count = 0;

        VKRequest update = new VKRequest("messages.getLongPollHistory",  VKParameters.from("pts", ActivityBase.pts));

        update.executeWithListener(new VKRequest.VKRequestListener() {
            @Override
            public void onComplete(VKResponse response) {
                try {
                    count = response.json.getJSONObject("response").getJSONObject("messages").getInt("count");
                    Log.i("POOL", String.valueOf(count));
                    Log.i("POOL", String.valueOf(ActivityBase.pts));
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                super.onComplete(response);
            }
        });
        getActivity().setTitle(title);

        FragmentTransaction ft = getFragmentManager().beginTransaction();
        ft.detach(this).attach(this).commit();
    }

    private class LongOperation extends AsyncTask<String, Void, String> {

        @Override
        protected String doInBackground(String... params) {
            try {
                friendKey = PublicKeyHandler.downloadFriendPublicKey(title_id);
            } catch (InvalidKeySpecException | NoSuchAlgorithmException | JSONException | IOException e) {
                e.printStackTrace();
            }
            String tmp;
            for (VKApiMessage msg : vkMessages) {
                ChatMessage chatMessage = new ChatMessage(msg.body, msg.out, msg.date);
                if(msg.out && msg.body.length() == 174 && msg.body.charAt(msg.body.length() - 1) == '=') {
                    Log.d("isEnc", String.valueOf(msg.body.length()));
                    List<MyMessagesHistory> outMsg = MyMessagesHistory.find(MyMessagesHistory.class,
                            "msg_id = ?", String.valueOf(msg.id));
                    if (outMsg.size() != 0){
                        chatMessage.setMsg(outMsg.get(0).getMsg());
                    }
                }

                if(msg.attachments.size() != 0 || msg.body.isEmpty() || !msg.fwd_messages.isEmpty()){
                    chatMessage.setMsg(MEDIA_MSG);
                    singleDialogAdapter.add(chatMessage);
                    continue;
                }

                try {
                    tmp = ActivityBase.encryptor.decode(msg.body);
                } catch (Exception e) {
                    tmp = msg.body;
                    e.printStackTrace();
                }
                if (tmp.startsWith(PREFIX)) {
                    chatMessage.setMsg(tmp.substring(PREFIX.length()));
                }

                singleDialogAdapter.add(chatMessage);
            }
            return friendKey;
        }

        @Override
        protected void onPostExecute(String result) {
            // might want to change "executed" for the returned string passed
            // into onPostExecute() but that is upto you
            send.setText("Send");
            send.setClickable(true);
            singleDialogAdapter.notifyDataSetChanged();
        }

        @Override
        protected void onPreExecute() {
            send.setClickable(false);
            send.setText("loading");
            listView.setAdapter(singleDialogAdapter);
        }

        @Override
        protected void onProgressUpdate(Void... values) {
        }
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState){

        View view = inflater.inflate(R.layout.fragment_single_dialog, null);

        mswipeRefreshLayout = (SwipeRefreshLayout) view.findViewById(R.id.refresh);
        mswipeRefreshLayout.setOnRefreshListener(this);

        count = 0;

        VKRequest update = new VKRequest("messages.getLongPollHistory",  VKParameters.from("pts", ActivityBase.pts));

        update.executeWithListener(new VKRequest.VKRequestListener() {
            @Override
            public void onComplete(VKResponse response) {
                try {
                    count = response.json.getJSONObject("response").getJSONObject("messages").getInt("count");
                    Log.i("POOL", String.valueOf(count));
                    Log.i("POOL", String.valueOf(ActivityBase.pts));
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                super.onComplete(response);
            }
        });

        inList = getArguments().getStringArrayList(IN_LIST);
        outList = getArguments().getStringArrayList(OUT_LIST);
        vkMessages = getArguments().getParcelableArrayList(MESSAGES);
        Collections.reverse(vkMessages);
        singleDialogAdapter = new SingleDialogAdapter(view.getContext(), inList, outList);

        id = getArguments().getInt(USER_ID);
        

        text = (EditText) view.findViewById(R.id.textmsg);
        listView = (ListView) view.findViewById(R.id.listmsg);
        send = (Button) view.findViewById(R.id.sendmsg);

        new LongOperation().execute();
        singleDialogAdapter.notifyDataSetChanged();

//        if (id == Integer.parseInt(VKSdk.getAccessToken().userId)) {
//            listView.setAdapter(new MyselfSingleDialogAdapter(view.getContext(), inList));
//        } else {
//            listView.setAdapter(singleDialogAdapter);
//        }

        send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (!sendFlag)
                    sendFlag = true;

                VKRequest request;

                final String msg = text.getText().toString();
                String messageToSend = PREFIX + msg;

                try {
                    if (!friendKey.equals("none")) {

                        ActivityBase.encryptor.setPublicKey(friendKey);
                        messageToSend = ActivityBase.encryptor.encode(messageToSend);

                        text.setText("");

                        request = new VKRequest("messages.send", VKParameters.from(VKApiConst.USER_ID, id,
                                VKApiConst.MESSAGE, messageToSend));

                        request.executeWithListener(new VKRequest.VKRequestListener() {
                            @Override
                            public void onComplete(VKResponse response) {
                                super.onComplete(response);
                                int msgId;
                                try {
                                    msgId = (int) response.json.get("response");
                                    MyMessagesHistory myMessage = new MyMessagesHistory(id, msg, msgId);
                                    myMessage.save();
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                                ChatMessage chatMessage = new ChatMessage(msg, true, new Date().getTime());
                                singleDialogAdapter.add(chatMessage);
                                singleDialogAdapter.notifyDataSetChanged();
                            }
                        });

                    } else {
                        Toast.makeText(getContext(), "The friend hasn't started the dialog.",
                                Toast.LENGTH_SHORT).show();
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }

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

        count = 0;

        VKRequest update = new VKRequest("messages.getLongPollHistory",  VKParameters.from("pts", ActivityBase.pts));

        update.executeWithListener(new VKRequest.VKRequestListener() {
            @Override
            public void onComplete(VKResponse response) {
                try {
                    count = response.json.getJSONObject("response").getJSONObject("messages").getInt("count");
                    Log.i("POOL", String.valueOf(count));
                    Log.i("POOL", String.valueOf(ActivityBase.pts));
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                super.onComplete(response);
            }
        });

        final String[] name_id = {""};

        VKRequest my_request = VKApi.users().get(VKParameters.from(VKApiConst.USER_IDS, title_id,
                VKApiConst.FIELDS, "first_name, last_name"));
        my_request.executeWithListener(new VKRequest.VKRequestListener() {
            @Override
            public void onComplete(VKResponse response) {
                super.onComplete(response);
                list_s = (VKList) response.parsedModel;

                name_id[0] = String.valueOf(FragmentSingleDialog.this.list_s.getById(title_id));
                title = name_id[0]+ " +" + String.valueOf(count);
                getActivity().setTitle(title);
            }
        });

        getActivity().findViewById(R.id.toolbar).findViewById(R.id.toolbar_button).setVisibility(View.VISIBLE);
    }

    @Override
    public void onStop() {
        super.onStop();

        if (sendFlag) {
            String[] queryId = {String.valueOf(id)};
            List<MyMessagesHistory> list = MyMessagesHistory.find(MyMessagesHistory.class,
                    "user_id = ?", queryId, "", "id DESC", "10");
            MyMessagesHistory.deleteAll(MyMessagesHistory.class, "user_id = ?", String.valueOf(id));

            for (MyMessagesHistory item : list) {
                item.save();
            }
        }
    }
}
