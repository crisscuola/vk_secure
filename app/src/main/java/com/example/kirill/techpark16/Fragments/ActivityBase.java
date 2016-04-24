package com.example.kirill.techpark16.Fragments;

import android.app.Application;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;


import com.example.kirill.techpark16.FullEncryption;
import com.example.kirill.techpark16.PublicKeysTable;
import com.example.kirill.techpark16.R;
import com.example.kirill.techpark16.RSAEncryption;
import com.vk.sdk.VKScope;
import com.vk.sdk.VKSdk;
import com.vk.sdk.api.VKApi;
import com.vk.sdk.api.VKApiConst;
import com.vk.sdk.api.VKError;
import com.vk.sdk.api.VKParameters;
import com.vk.sdk.api.VKRequest;
import com.vk.sdk.api.VKResponse;
import com.vk.sdk.api.model.VKApiDialog;
import com.vk.sdk.api.model.VKApiGetDialogResponse;
import com.vk.sdk.api.model.VKApiMessage;
import com.vk.sdk.api.model.VKApiModel;
import com.vk.sdk.api.model.VKList;

import org.json.JSONArray;
import org.json.JSONException;

import java.security.PublicKey;
import java.util.ArrayList;
import java.util.List;


/**
 * Created by konstantin on 09.04.16.
 */
public  class ActivityBase extends AppCompatActivity implements FragmentDialogsList.onItemSelectedListener,
        FragmentFriendsList.onItemSelectedListener ,NavigationView.OnNavigationItemSelectedListener,
        FragmentFriendsSend.onItemSelectedListener {

    Fragment fragmentSet[] = new Fragment[10];
    ActionBarDrawerToggle toggle;
    android.support.v4.app.FragmentTransaction fragmentTransaction;
    Toolbar toolbar;
    DrawerLayout drawer;
    NavigationView navigationView;
    BroadcastReceiver br;
    Button toolbarButton;

    @Override
    protected void onStop() {
        super.onStop();
//        List<PublicKeysTable> keys = PublicKeysTable.find(PublicKeysTable.class, "id > 0");
//        for (PublicKeysTable key:keys
//             ) {
//            key.delete();
//        }

    }

    static PublicKey pk;
    static RSAEncryption rsaInstance = new RSAEncryption();
    private String [] scope = new String[] {VKScope.MESSAGES,VKScope.FRIENDS,VKScope.WALL, VKScope.OFFLINE, VKScope.STATUS, VKScope.NOTES};
    public static FullEncryption encryptor = new FullEncryption();
    static FullEncryption encryptionFriend = new FullEncryption();

    final static String BROADCAST_EVENT = "com.example.kirill.techpark16";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    VKSdk.login(this, scope);

        setContentView(R.layout.base_activity);


//        final int my_id = Integer.parseInt(VKSdk.getAccessToken().userId);


        String a = encryptor.getPublicKey();

        final VKRequest note_request = new VKRequest("notes.add", VKParameters.from("title",a , "text", "public_key"));

        note_request.executeWithListener(new VKRequest.VKRequestListener() {
            @Override
            public void onComplete(VKResponse response) {
                super.onComplete(response);
            }


            @Override
            public void onError(VKError error) {
                Log.i("notes", String.valueOf(error.errorCode));
            }

        });

        setBroadcastReceiver();

        IntentFilter intentFilter = new IntentFilter(BROADCAST_EVENT);

        registerReceiver(br, intentFilter);


        toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbarButton = (Button)toolbar.findViewById(R.id.toolbar_button);

        toolbarButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Fragment currentFragment = getSupportFragmentManager().findFragmentById(R.id.fragmentPlace);

                if (currentFragment instanceof FragmentDialogsList) {

                    fragmentTransaction = getSupportFragmentManager().beginTransaction();
                    fragmentTransaction.replace(R.id.fragmentPlace, fragmentSet[FragmentsConst.FRIENDSEND]);
                    fragmentTransaction.addToBackStack(null);
                    fragmentTransaction.commit();
                    toolbar.setTitle(R.string.friends_title);
                    toolbar.setTitle(R.string.send);
                    toolbar.findViewById(R.id.toolbar_button).setVisibility(View.INVISIBLE);
                }

                if (currentFragment instanceof FragmentSingleDialog) {

                    fragmentTransaction = getSupportFragmentManager().beginTransaction();
                    fragmentTransaction.replace(R.id.fragmentPlace, fragmentSet[FragmentsConst.SETTINGSDIALOG]);
                    fragmentTransaction.addToBackStack(null);
                    fragmentTransaction.commit();
                    toolbar.setTitle(R.string.friends_title);
                    toolbar.findViewById(R.id.toolbar_button).setVisibility(View.INVISIBLE);
                }
            }
        });


        setSupportActionBar(toolbar);


        drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();


        navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        fragmentSet[FragmentsConst.DIALOGSLIST] = new FragmentDialogsList();
        fragmentSet[FragmentsConst.FRIENDSLIST] = new FragmentFriendsList();
        fragmentSet[FragmentsConst.SETTINGS] = new FragmentSettings();
        fragmentSet[FragmentsConst.SINGLEDIALOG] = null;
        fragmentSet[FragmentsConst.SINGLEDIALOG] = new FragmentSingleDialog();
        fragmentSet[FragmentsConst.SETTINGSDIALOG] = new FragmentSettingsDialog();
        fragmentSet[FragmentsConst.FRIENDSEND] = new FragmentFriendsSend();


        toolbar.setTitle(R.string.dialog_list_title);
        toolbar.findViewById(R.id.toolbar_button).setVisibility(View.VISIBLE);
        fragmentTransaction = getSupportFragmentManager().beginTransaction();
        fragmentTransaction.add(R.id.fragmentPlace, fragmentSet[FragmentsConst.DIALOGSLIST]);
        fragmentTransaction.commit();




    }



    private void setBroadcastReceiver() {
        br = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                try {

                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        };

    }


    public void changeFragment(int oldFragment, int newFragment, String option){

        fragmentTransaction = getSupportFragmentManager().beginTransaction();
        fragmentTransaction.replace(R.id.fragmentPlace, fragmentSet[newFragment]);
        fragmentTransaction.commit();

    }

    public void changeToggle(int fragment){

            toggle.setDrawerIndicatorEnabled(true);
            toolbar.setNavigationOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    drawer.openDrawer(GravityCompat.START);
                }
            });

            navigationView.setNavigationItemSelectedListener(this);
    }

    protected Application getApp() {
        return getApplication();
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    public void onBackPressed() {
        if (getFragmentManager().getBackStackEntryCount() > 1 ){
            getFragmentManager().popBackStack();
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        //getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {

        int id = item.getItemId();

        switch (id) {
            case R.id.nav_dialogs:
                fragmentTransaction = getSupportFragmentManager().beginTransaction();
                fragmentTransaction.replace(R.id.fragmentPlace, fragmentSet[FragmentsConst.DIALOGSLIST]);
                fragmentTransaction.addToBackStack(null);
                fragmentTransaction.commit();
                toolbar.setTitle(R.string.dialog_list_title);
                toolbar.findViewById(R.id.toolbar_button).setVisibility(View.VISIBLE);
                break;

            case R.id.nav_friends:
                fragmentTransaction = getSupportFragmentManager().beginTransaction();
                fragmentTransaction.replace(R.id.fragmentPlace, fragmentSet[FragmentsConst.FRIENDSLIST]);
                fragmentTransaction.addToBackStack(null);
                fragmentTransaction.commit();
                toolbar.setTitle(R.string.friends_title);
                toolbar.findViewById(R.id.toolbar_button).setVisibility(View.INVISIBLE);
                break;

            case R.id.nav_settings:
                fragmentTransaction = getSupportFragmentManager().beginTransaction();
                fragmentTransaction.replace(R.id.fragmentPlace, fragmentSet[FragmentsConst.SETTINGS]);
                fragmentTransaction.addToBackStack(null);
                fragmentTransaction.commit();
                toolbar.setTitle(R.string.settings_title);
                toolbar.findViewById(R.id.toolbar_button).setVisibility(View.INVISIBLE);

                break;

        }
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }


    @Override
    public void onDialogSelected(final int position) {

        final VKRequest request = VKApi.messages().getDialogs(VKParameters.from(VKApiConst.COUNT, 10));

        request.executeWithListener(new VKRequest.VKRequestListener() {
            @Override
            public void onComplete(VKResponse response) {
                super.onComplete(response);
                VKApiGetDialogResponse getMessagesResponse = (VKApiGetDialogResponse) response.parsedModel;

                final VKList<VKApiDialog> list = getMessagesResponse.items;

                final int id = list.get(position).message.user_id;

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

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }


                        FragmentSingleDialog newFragment = FragmentSingleDialog.getInstance(id, inList, outList);
                        fragmentTransaction = getSupportFragmentManager().beginTransaction();
                        fragmentTransaction.replace(R.id.fragmentPlace, newFragment);
                        fragmentTransaction.addToBackStack(null);
                        fragmentTransaction.commit();

                    }
                });
            }
        });


    }



    @Override
    public void onFriendSelected(final int position) {

        VKRequest request_list_friend = VKApi.friends().get(VKParameters.from(VKApiConst.FIELDS, "first_name, last_name", "order", "hints"));

        request_list_friend.executeWithListener(new VKRequest.VKRequestListener() {
            @Override
            public void onComplete(VKResponse response) {

                super.onComplete(response);

                VKList list = new VKList();

                list = (VKList) response.parsedModel;

                int id = 0;

                VKApiModel model = list.get(position);
                try {
                    id = model.fields.getInt("id");
                } catch (JSONException e) {
                    e.printStackTrace();
                }

                String firstname = "";
                String lastname = "";


                try {
                    firstname = model.fields.getString("first_name");
                    lastname = model.fields.getString("last_name");

                } catch (JSONException e) {
                    e.printStackTrace();
                }

                FragmentSingleFriend newFragment = FragmentSingleFriend.getInstance(id, firstname, lastname);
                fragmentTransaction = getSupportFragmentManager().beginTransaction();
                fragmentTransaction.replace(R.id.fragmentPlace, newFragment);
                fragmentTransaction.addToBackStack(null);
                fragmentTransaction.commit();
            }
        });
    }



    @Override
    public void onFriendSendSelected(final int position) {
        final VKRequest request = VKApi.friends().get(VKParameters.from(VKApiConst.FIELDS, "first_name, last_name", "order", "hints"));


        request.executeWithListener(new VKRequest.VKRequestListener() {
            @Override
            public void onComplete(VKResponse response) {
                super.onComplete(response);

                VKList list = new VKList();

                list = (VKList) response.parsedModel;

                int id_f = 0;

                VKApiModel model = list.get(position);

                try {
                    id_f = model.fields.getInt("id");
                } catch (JSONException e) {
                    e.printStackTrace();
                }

                VKRequest request = new VKRequest("messages.getHistory", VKParameters.from(VKApiConst.USER_ID, id_f));
                final int finalId_f = id_f;
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

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                        String temp ;

                        temp = String.valueOf(finalId_f);

                        int id = Integer.parseInt(temp);


                        FragmentSingleDialog newFragment = FragmentSingleDialog.getInstance(id, inList, outList);
                        fragmentTransaction = getSupportFragmentManager().beginTransaction();
                        fragmentTransaction.replace(R.id.fragmentPlace, newFragment);
                        fragmentTransaction.addToBackStack(null);
                        fragmentTransaction.commit();

                    }
                });
            }
        });
    }

    public void sendMessageButton(View view) {

    }



}
