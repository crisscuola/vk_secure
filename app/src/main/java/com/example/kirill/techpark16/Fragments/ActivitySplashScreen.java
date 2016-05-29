package com.example.kirill.techpark16.Fragments;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.widget.TextView;

import com.example.kirill.techpark16.Friend;
import com.example.kirill.techpark16.R;
import com.vk.sdk.VKAccessToken;
import com.vk.sdk.VKScope;
import com.vk.sdk.VKSdk;
import com.vk.sdk.api.VKApi;
import com.vk.sdk.api.VKApiConst;
import com.vk.sdk.api.VKParameters;
import com.vk.sdk.api.VKRequest;
import com.vk.sdk.api.VKResponse;
import com.vk.sdk.api.model.VKApiDialog;
import com.vk.sdk.api.model.VKApiGetDialogResponse;
import com.vk.sdk.api.model.VKApiMessage;
import com.vk.sdk.api.model.VKApiUser;
import com.vk.sdk.api.model.VKList;

import java.util.ArrayList;


/**
 * Created by kirill on 25.05.16
 */
public class ActivitySplashScreen extends AppCompatActivity {

    private final int SPLASH_DISPLAY_LENGTH = 1000;

    private VKList<VKApiUser> usersPhoto;
    private VKList usersArray;
    private  ArrayList id_array = new ArrayList();

    final private String ENCRYPTED_MSG = "[ENCRYPTED MESSAGE]";
    final private String MEDIA_MSG = "[MEDIA MESSAGE]";
    private final String PREFIX = "cps_lbs_";
    final ArrayList<VKApiMessage> messages = new ArrayList<>();
    final ArrayList<String> users = new ArrayList<>();
    final  ArrayList<Integer> ids = new ArrayList<>();
    final  VKList<VKApiUser> photo = new VKList<>();
    private String [] scope = new String[] {VKScope.MESSAGES,VKScope.FRIENDS,VKScope.WALL,
            VKScope.OFFLINE, VKScope.STATUS, VKScope.NOTES};
    VKList<VKApiUser> friendList = new VKList<>();

    TextView loading;


    @Override
    protected void onResume() {
        super.onResume();

        if (VKAccessToken.currentToken() == null) {
            VKSdk.login(this, scope);
        } else {
            new DialogsDownloading().execute();
            new DownloadingFriendList().execute();
        }
    }

    @Override
    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        setContentView(R.layout.activity_splash);
        loading = (TextView) findViewById(R.id.splash_loading);
    }

    private class DialogsDownloading extends AsyncTask<Object, Void, Void> {
        @Override
        protected Void doInBackground(Object... params) {

            final VKRequest request_dialogs_one = VKApi.messages().getDialogs(VKParameters.from(VKApiConst.COUNT, 30));

            request_dialogs_one.executeWithListener(new VKRequest.VKRequestListener() {
                @Override
                public void onComplete(VKResponse response) {

                    super.onComplete(response);
                    VKApiGetDialogResponse getMessagesResponse = (VKApiGetDialogResponse) response.parsedModel;

                    final VKList<VKApiDialog> list = getMessagesResponse.items;

                    for (final VKApiDialog msg : list) {

                        id_array.add(msg.message.user_id);
                    }

                    VKRequest my_request = VKApi.users().get(VKParameters.from(VKApiConst.USER_IDS,
                            id_array, VKApiConst.FIELDS, "first_name, last_name, photo_100"));

                    my_request.executeWithListener(new VKRequest.VKRequestListener() {
                        @Override
                        public void onComplete(VKResponse response) {
                            super.onComplete(response);

                            usersArray = (VKList) response.parsedModel;

                            usersPhoto = (VKList<VKApiUser>) response.parsedModel;

                            final VKRequest request_dialogs_two = VKApi.messages().getDialogs(
                                    VKParameters.from(VKApiConst.COUNT, 30));
                            request_dialogs_two.executeWithListener(new VKRequest.VKRequestListener() {
                                @Override
                                public void onComplete(VKResponse response) {
                                    super.onComplete(response);

                                    VKApiGetDialogResponse getMessagesResponse = (VKApiGetDialogResponse)
                                            response.parsedModel;

                                    final VKList<VKApiDialog> list = getMessagesResponse.items;

                                    for (final VKApiDialog msg : list) {

                                        VKApiMessage test = msg.message;

                                        if (test.title.equals(" ... ")) {

                                            if (test.body.length() == 174 && test.body.charAt(test.body.length() - 1) == '=') {
                                                VKApiMessage vkApiMessage = new VKApiMessage();
                                                vkApiMessage.body = ENCRYPTED_MSG;
                                                vkApiMessage.read_state = msg.message.read_state;
                                                messages.add(vkApiMessage);
                                            } else if (msg.message.attachments.size() != 0 || msg.message.body.isEmpty()
                                                    || !msg.message.fwd_messages.isEmpty()) {
                                                VKApiMessage vkApiMessage = new VKApiMessage();
                                                vkApiMessage.body = MEDIA_MSG;
                                                vkApiMessage.read_state = msg.message.read_state;
                                                messages.add(vkApiMessage);
                                            } else {
                                                VKApiMessage vkApiMessage = msg.message;

                                                String mess = "";

                                                if (msg.message.out) {
                                                    mess = mess + "Вы: " + vkApiMessage.body;
                                                } else {
                                                    mess = mess + vkApiMessage.body;
                                                }


                                                mess = mess.replaceAll("\\r|\\n", PREFIX);


                                                if (mess.contains(PREFIX)) {
                                                    Integer pos = mess.indexOf(PREFIX);
                                                    mess = mess.substring(0, pos);
                                                    mess += " ...";
                                                }

                                                if (mess.length() > 30) {
                                                    mess = mess.substring(0, 30);
                                                    mess += " ...";
                                                }
                                                vkApiMessage.body = mess;
                                                messages.add(vkApiMessage);
                                            }
                                        }
                                        if (msg.message.title.equals(" ... ")) {
                                            users.add(String.valueOf(usersArray.getById(msg.message.user_id)));
                                            photo.add(usersPhoto.getById(msg.message.user_id));
                                        }
                                        ids.add(msg.message.user_id);


                                    }
                                    new Handler().postDelayed(new Runnable() {
                                        @Override
                                        public void run() {
                                            Intent mainIntent = new Intent(ActivitySplashScreen.this, ActivityBase.class);
                                            final ArrayList<VKApiUser> photolist = new ArrayList<VKApiUser>(photo);

                                            mainIntent.putStringArrayListExtra("users", users)
                                                    .putParcelableArrayListExtra("photo", photolist)
                                                    .putParcelableArrayListExtra("messages", messages)
                                                    .putExtra("from_splash", true);
                                            ActivitySplashScreen.this.startActivity(mainIntent);
                                            ActivitySplashScreen.this.finish();
                                        }
                                    }, SPLASH_DISPLAY_LENGTH);
                                }
                            });
                        }
                    });
                }
            });

            return null;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();

        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);

        }
    }


    private class DownloadingFriendList extends AsyncTask<Void, Void, Void> {

        @Override
        protected Void doInBackground(Void... params) {
            VKRequest request_list_friend = VKApi.friends().get(VKParameters.from(VKApiConst.FIELDS,
                    "first_name, last_name, photo_100", "order", "hints"));

            request_list_friend.executeWithListener(new VKRequest.VKRequestListener() {
                @Override
                public void onComplete(VKResponse response) {

                    super.onComplete(response);
                    Friend.deleteAll(Friend.class);
                    friendList = (VKList<VKApiUser>) response.parsedModel;

                    for (final VKApiUser user: friendList) {
                        if ((user.first_name.length() + user.last_name.length()) > 20) {
                            user.last_name = user.last_name.substring(0, 17 - user.first_name.length());
                            user.last_name += "... ";
                        }
                        Friend friend = new Friend(user.first_name, user.last_name, user.photo_100, user.id);
                        friend.save();
                    }
                }
            });
            return null;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
        }
    }
}


