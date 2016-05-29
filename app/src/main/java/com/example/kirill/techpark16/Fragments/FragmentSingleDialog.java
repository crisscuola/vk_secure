package com.example.kirill.techpark16.Fragments;


import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.ListFragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import com.example.kirill.techpark16.Adapters.SingleDialogAdapter;
import com.example.kirill.techpark16.ChatMessage;
import com.example.kirill.techpark16.MyMessagesHistory;
import com.example.kirill.techpark16.PublicKeyHandler;
import com.example.kirill.techpark16.R;
import com.orangegangsters.github.swipyrefreshlayout.library.SwipyRefreshLayout;
import com.orangegangsters.github.swipyrefreshlayout.library.SwipyRefreshLayoutDirection;
import com.vk.sdk.api.VKApi;
import com.vk.sdk.api.VKApiConst;
import com.vk.sdk.api.VKParameters;
import com.vk.sdk.api.VKRequest;
import com.vk.sdk.api.VKResponse;
import com.vk.sdk.api.model.VKApiMessage;
import com.vk.sdk.api.model.VKList;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Created by kirill on 02.04.16
 */
public class FragmentSingleDialog extends ListFragment {


    public static final String USER_ID = "user_id";
    public static final String MESSAGES = "messages";
    public static final String IDS = "ids";
    private final String PREFIX = "cpslbs_";

    static ArrayList<VKApiMessage> vkMessages = new ArrayList<>();
    ArrayList<Integer> vkMessagesIds = new ArrayList<>();
    SingleDialogAdapter singleDialogAdapter;
    ArrayList<ChatMessage> chatMessagesList= new ArrayList<>();
    int id;
    boolean sendFlag = false;
    boolean encryptionMode = false;
    static boolean[] wasResfesh = {false};
    int buf = 0;


    EditText text;
    ListView listView;
    Button send;
    static String title;

    static int title_id;
    static int outCounter = 0;
    VKList list_s;
    VKRequest update = new VKRequest("messages.getLongPollHistory", VKParameters.from("pts", ActivityBase.pts));;
    int y;

    String friendKey;
    private static SwipyRefreshLayout mswipeRefreshLayout;

    public static FragmentSingleDialog getInstance(int user_id, ArrayList<VKApiMessage> vkMsgs, ArrayList<Integer> ids) {
        FragmentSingleDialog fragmentSingleDialog = new FragmentSingleDialog();
        Bundle bundle = new Bundle();
        bundle.putInt(USER_ID, user_id);
        bundle.putParcelableArrayList(MESSAGES, vkMsgs);
        bundle.putIntegerArrayList(IDS, ids);

        title_id = user_id;

        fragmentSingleDialog.setArguments(bundle);
        return fragmentSingleDialog;
    }

    private class DownloadingKey extends AsyncTask<String, Void, String> {

        @Override
        protected String doInBackground(String... params) {
            try {
                friendKey = PublicKeyHandler.requestPublicKeyFromServer(title_id);

            } catch (IOException | JSONException e) {
                e.printStackTrace();
            }
            return friendKey;
        }
    }

    private class DownloadingMessages extends AsyncTask<String, Void, String> {

        @Override
        protected String doInBackground(String... params) {
            String tmp;
            boolean keyIsDownloaded = false;
            String mediaMessage = "[MEDIA MESSAGE]";


            try {
                friendKey = PublicKeyHandler.requestPublicKeyFromServer(title_id);

            } catch (IOException | JSONException e) {
                e.printStackTrace();
            }

            for (VKApiMessage msg : vkMessages) {
//                if (msg.body.equals("I write from new Device!") && !msg.out && !keyIsDownloaded) {
//                    keyIsDownloaded = true;
//                    try {
//                        friendKey = PublicKeyHandler.downloadFriendPublicKey(title_id, true);
//                        ActivityBase.encryptor.setPublicKey(friendKey);
//                    } catch (InvalidKeySpecException | NoSuchAlgorithmException | JSONException | IOException e) {
//                        e.printStackTrace();
//                    }
//                }

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

                    chatMessage.setMsg(mediaMessage);
                    chatMessagesList.add(chatMessage);
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
                chatMessagesList.add(chatMessage);
            }
            return friendKey;
        }

        @Override
        protected void onPostExecute(String result) {
            // might want to change "executed" for the returned string passed
            // into onPostExecute() but that is upto you
            send.setText("Отправить");
            send.setClickable(true);
            Collections.reverse(chatMessagesList);
            singleDialogAdapter.copyArrayList(chatMessagesList);
            singleDialogAdapter.notifyDataSetChanged();
            chatMessagesList.clear();
        }

        @Override
        protected void onPreExecute() {
            send.setClickable(false);
            send.setText("Загрузка");
            listView.setAdapter(singleDialogAdapter);
        }
    }


    public boolean onTouchEvent(MotionEvent event) {
        y = (int)event.getY();
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
            case MotionEvent.ACTION_MOVE:
            case MotionEvent.ACTION_UP:
        }
        return false;
    }

    public void setupUI(final View view) {
        if(!(view instanceof EditText) && !(view instanceof Button)) {

            view.setOnTouchListener(new View.OnTouchListener() {

                public boolean onTouch(View v, MotionEvent event) {
                    onTouchEvent(event);

                    if (y < 265) {
                        InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
                        imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
                        text.clearFocus();
                    }
                    return false;
                }

            });
        }

        //If a layout container, iterate over children and seed recursion.
        if (view instanceof ViewGroup) {

            for (int i = 0; i < ((ViewGroup) view).getChildCount(); i++) {

                View innerView = ((ViewGroup) view).getChildAt(i);

                setupUI(innerView);
            }
        }
    }


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState){
        final View view = inflater.inflate(R.layout.fragment_single_dialog, null);
        setupUI(view);
        wasResfesh[0] = false;
        update = new VKRequest("messages.getLongPollHistory", VKParameters.from("pts", ActivityBase.pts));

        VKRequest request_long_poll =  new VKRequest("messages.getLongPollServer", VKParameters.from("need_pts", 1));

        request_long_poll.executeWithListener(new VKRequest.VKRequestListener() {
            @Override
            public void onComplete(VKResponse response) {
            super.onComplete(response);
            try {
                ActivityBase.pts = response.json.getJSONObject("response").getInt("pts");

                Log.i("PTS", String.valueOf(ActivityBase.pts));
            } catch (JSONException e) {
                e.printStackTrace();
            }
            }
        });


//        VKRequest request = new VKRequest("messages.getHistory", VKParameters.from(VKApiConst.USER_ID, id));
//
//        request.executeWithListener(new VKRequest.VKRequestListener() {
//            @Override
//            public void onComplete(VKResponse response) {
//            super.onComplete(response);
//
//            try {
//                JSONArray array = response.json.getJSONObject("response").getJSONArray("items");
//
//                for (int i = 0; i < array.length(); i++) {
//                    VKApiMessage mes = new VKApiMessage(array.getJSONObject(i));
//                    vkMessages.add(mes);
//                    if (!mes.out)
//                        vkMessagesIds.add(mes.id);
//                }
//
//            } catch (JSONException e) {
//                e.printStackTrace();
//            }
//            }
//        });

        vkMessages = getArguments().getParcelableArrayList(MESSAGES);
        vkMessagesIds = getArguments().getIntegerArrayList(IDS);

        VKRequest markAsRead = new VKRequest("messages.markAsRead", VKParameters
                .from("message_ids", vkMessagesIds));
        markAsRead.executeWithListener(new VKRequest.VKRequestListener() {
            @Override
            public void onComplete(VKResponse response) {
                super.onComplete(response);
            }
        });


        if (!FragmentSettingsDialog.flag) {
            Collections.reverse(vkMessages);
        }
        FragmentSettingsDialog.flag = false;
        singleDialogAdapter = new SingleDialogAdapter(view.getContext());

        id = getArguments().getInt(USER_ID);



        text = (EditText) view.findViewById(R.id.textmsg);
        listView = (ListView) view.findViewById(R.id.listmsg);
        send = (Button) view.findViewById(R.id.sendmsg);

        new DownloadingMessages().execute();

        encryptionMode = PublicKeyHandler.checkEncryprionMode(id);

        send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (!sendFlag)
                    sendFlag = true;

                VKRequest request;

                final String msg = text.getText().toString();
                final String messageToSend;

                try {

                    if (encryptionMode) {
                        if (!friendKey.equals("none")) {
                            ActivityBase.encryptor.setPublicKey(friendKey);
                            Log.d("resp_friend_key", ActivityBase.encryptor.getPublicKey());
                            messageToSend = ActivityBase.encryptor.encode(PREFIX + msg);

                        } else {
                            new DownloadingKey().execute();
                            Toast.makeText(getContext(), "Друг еще не начал диалог с Вами. Попробуйте еще раз.",
                                    Toast.LENGTH_SHORT).show();
                            return;
                        }
                    } else
                        messageToSend = msg;

                    text.setText("");

                    request = new VKRequest("messages.send", VKParameters.from(VKApiConst.USER_ID, id,
                            VKApiConst.MESSAGE, messageToSend));

                    request.executeWithListener(new VKRequest.VKRequestListener() {
                        @Override
                        public void onComplete(VKResponse response) {
                            super.onComplete(response);
                            int msgId = 0;
                            try {
                                msgId = (int) response.json.get("response");
                                MyMessagesHistory myMessage = new MyMessagesHistory(id, msg, msgId);
                                myMessage.save();
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                            ChatMessage chatMessage = new ChatMessage(msg, true, null);
                            singleDialogAdapter.add(chatMessage);
                            singleDialogAdapter.notifyDataSetChanged();
                        if(msgId != 0) {
                            VKRequest getById = new VKRequest("messages.getById", VKParameters.from("message_ids", msgId));
                            getById.executeWithListener(new VKRequest.VKRequestListener() {
                                @Override
                                public void onComplete(VKResponse response) {
                                    super.onComplete(response);
                                    if(!wasResfesh[0]) {
                                        try {
                                            JSONArray message = response.json.getJSONObject("response").getJSONArray("items");
                                            JSONObject obj = new JSONObject(String.valueOf(message.get(0)));
                                            obj.remove("body");
                                            obj.put("body", msg);
                                            Log.d("message", String.valueOf(obj));
                                            vkMessages.add(new VKApiMessage(obj));
                                        } catch (JSONException e) {
                                            e.printStackTrace();
                                        }
                                    }
                                }
                            });
                        }
                    }
                });


            } catch (Exception e) {
                e.printStackTrace();
            }

            }
        });
        mswipeRefreshLayout = (SwipyRefreshLayout) view.findViewById(R.id.refresh);
        mswipeRefreshLayout.setOnRefreshListener(new SwipyRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh(SwipyRefreshLayoutDirection direction) {
            Log.i("REFRESH", "REFRESH");
            wasResfesh[0] = true;
            final ArrayList<VKApiMessage> msgList = new ArrayList<>();
            final ArrayList<Integer> idList = new ArrayList<>();
            final ArrayList<ChatMessage> addMessagesList = new ArrayList<>();

            update = new VKRequest("messages.getLongPollHistory", VKParameters.from("pts", ActivityBase.pts));

            mswipeRefreshLayout.setRefreshing(true);

            update.executeWithListener(new VKRequest.VKRequestListener() {
                @Override
                public void onComplete(VKResponse response) {
                try {
                    outCounter = 0;
                    JSONArray new_messages = response.json.getJSONObject("response").getJSONObject("messages").getJSONArray("items");
                    for (int i = 0; i < new_messages.length(); i++) {
                        VKApiMessage mes = new VKApiMessage(new_messages.getJSONObject(i));
                        if(mes.out)
                            outCounter++;
                    }
                    buf = outCounter;
                    String [] strings = {};
                    List<MyMessagesHistory> lastOut = MyMessagesHistory.find(MyMessagesHistory.class,
                            "", strings, "", "id DESC", String.valueOf(outCounter));
                    Collections.reverse(lastOut);
                    int toDelete = 0;
                    for (int i = 0; i < new_messages.length(); i++) {
                        VKApiMessage mes = new VKApiMessage(new_messages.getJSONObject(i));
                        if(!mes.out) {
                            if (mes.user_id == title_id) {
                                if (mes.body.length() == 174 && mes.body.charAt(mes.body.length() - 1) == '=') {
                                    mes.body = ActivityBase.encryptor.decode(mes.body);
                                    if (mes.body.startsWith(PREFIX))
                                        mes.body = mes.body.substring(PREFIX.length());
                                }

                            }
                        } else {
                            mes.body = lastOut.get(toDelete).getMsg();
                            Log.d("message", mes.body);
                            toDelete++;
                        }
                        msgList.add(mes);
                        idList.add(mes.id);
                    }
                    vkMessages.addAll(msgList);

                    for(int i = vkMessages.size() - 1; i >=0 ; i--){
                        if (toDelete != 0){
                            if(vkMessages.get(i).out){
                                Log.d("vkMessages_del", vkMessages.get(i).body + " " + toDelete);
                                vkMessages.remove(i);
                                toDelete--;
                            }
                        }
                    }

                    if (msgList.size() == 0) {
                        mswipeRefreshLayout.setRefreshing(false);
                        return;
                    }
                    String [] strings_new = {};
                    int toReplace = singleDialogAdapter.msgToReplace();
                    String body;

                    if(toReplace != 0) {
                        List<MyMessagesHistory> history = MyMessagesHistory.find(MyMessagesHistory.class,
                                "", strings_new, "", "id DESC", String.valueOf(toReplace));
                        Collections.reverse(history);
                        Log.d("history", String.valueOf(history.size()) + history.get(0).getMsg());
                        for (int i = 0; i < toReplace; i++){
                            singleDialogAdapter.deleteMessage(0);
                        }
                        singleDialogAdapter.notifyDataSetChanged();

                        int cnt = 0;
                        for (int i = 0; i < msgList.size(); i++) {

                            VKApiMessage mess = msgList.get(i);
                            if (mess.out) {
                                body = history.get(cnt).getMsg();
                                cnt++;
                                ChatMessage chatMessage = new ChatMessage(body, true, mess.date);
                                addMessagesList.add(chatMessage);
                                //singleDialogAdapter.add(chatMessage);
                            } else {
                                if (mess.body.startsWith(PREFIX)) {
                                    body = mess.body.substring(PREFIX.length());
                                } else
                                    body = mess.body;

                                ChatMessage chatMessage = new ChatMessage(body, false, mess.date);
                                addMessagesList.add(chatMessage);

                                //singleDialogAdapter.add(chatMessage);
                            }
                        }
                    } else {
                        int cnt = 0;
                        for (int i = 0; i < msgList.size(); i++) {

                            VKApiMessage mess = msgList.get(i);
                            if (mess.out) {
                                    body = mess.body;
                                cnt++;
                                ChatMessage chatMessage = new ChatMessage(body, true, mess.date);
                                addMessagesList.add(chatMessage);
                                //singleDialogAdapter.add(chatMessage);
                            } else {
                                if (mess.body.startsWith(PREFIX)) {
                                    body = mess.body.substring(PREFIX.length());
                                } else
                                    body = mess.body;

                                ChatMessage chatMessage = new ChatMessage(body, false, mess.date);
                                addMessagesList.add(chatMessage);

                                //singleDialogAdapter.add(chatMessage);
                            }
                        }
                    }

                    Collections.reverse(addMessagesList);
                    singleDialogAdapter.addArrayList(addMessagesList);
                    singleDialogAdapter.notifyDataSetChanged();
                    mswipeRefreshLayout.setRefreshing(false);

                    VKRequest markAsRead = new VKRequest("messages.markAsRead", VKParameters
                            .from("message_ids", idList));
                    markAsRead.executeWithListener(new VKRequest.VKRequestListener() {
                        @Override
                        public void onComplete(VKResponse response) {
                            super.onComplete(response);
                            idList.clear();
                        }
                    });

                } catch (Exception e) {
                    e.printStackTrace();
                }
                super.onComplete(response);
                }
                });

                VKRequest request_long_poll = new VKRequest("messages.getLongPollServer", VKParameters.from("need_pts", 1));
                request_long_poll.executeWithListener(new VKRequest.VKRequestListener() {
                    @Override
                    public void onComplete(VKResponse response) {
                    super.onComplete(response);
                    try {
                        ActivityBase.pts = response.json.getJSONObject("response").getInt("pts");
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    }
                });

                //getActivity().setTitle(title);
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

        update = new VKRequest("messages.getLongPollHistory", VKParameters.from("pts", ActivityBase.pts));

        VKRequest my_request = VKApi.users().get(VKParameters.from(VKApiConst.USER_IDS, title_id,
                VKApiConst.FIELDS, "first_name, last_name"));
        my_request.executeWithListener(new VKRequest.VKRequestListener() {
            @Override
            public void onComplete(VKResponse response) {
            super.onComplete(response);
            list_s = (VKList) response.parsedModel;
            name_id[0] = String.valueOf(FragmentSingleDialog.this.list_s.getById(title_id));
            String[] parts = name_id[0].split(" ");
            title = parts[0];
            if (getActivity() != null)
                getActivity().setTitle(title);
            }
        });

        getActivity().findViewById(R.id.toolbar).findViewById(R.id.toolbar_button).setVisibility(View.VISIBLE);
        Button myButton = (Button) getActivity().findViewById(R.id.toolbar).findViewById(R.id.toolbar_button);
        myButton.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.ic_settings, 0);

        encryptionMode = PublicKeyHandler.checkEncryprionMode(id);
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
            for (int i = 0; i < buf; i++){
                Log.d("message_del1", singleDialogAdapter.getMessage(1).getMsg());
                singleDialogAdapter.deleteMessage(0);
                Log.d("message_del2", singleDialogAdapter.getMessage(1).getMsg());
            }
            Log.d("message_del3", singleDialogAdapter.getMessage(1).getMsg());
            buf = 0;
            singleDialogAdapter.notifyDataSetChanged();
        }
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        getActivity().getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
    }
}
