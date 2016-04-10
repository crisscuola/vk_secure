package com.example.kirill.techpark16.Test;

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
import android.widget.Toast;

import com.example.kirill.techpark16.DetailDialogFragment;
import com.example.kirill.techpark16.FragmentDialogsList;
import com.example.kirill.techpark16.R;
import com.vk.sdk.api.VKApi;
import com.vk.sdk.api.VKApiConst;
import com.vk.sdk.api.VKParameters;
import com.vk.sdk.api.VKRequest;
import com.vk.sdk.api.VKResponse;
import com.vk.sdk.api.model.VKApiDialog;
import com.vk.sdk.api.model.VKApiGetDialogResponse;
import com.vk.sdk.api.model.VKApiMessage;
import com.vk.sdk.api.model.VKList;

import org.json.JSONArray;
import org.json.JSONException;

import java.util.ArrayList;

//import android.app.Fragment;

/**
 * Created by konstantin on 09.04.16.
 */
public  class ActivityBase extends AppCompatActivity implements FragmentDialogsList.onItemSelectedListener, NavigationView.OnNavigationItemSelectedListener {

    Fragment fragmentSet[] = new Fragment[5];
    ActionBarDrawerToggle toggle;
    android.support.v4.app.FragmentTransaction fragmentTransaction;
    Toolbar toolbar;
    DrawerLayout drawer;
    NavigationView navigationView;
    BroadcastReceiver br;

    final static String BROADCAST_EVENT = "com.example.kirill.techpark16";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.base_activity);

        setBroadcastReceiver();

        IntentFilter intentFilter = new IntentFilter(BROADCAST_EVENT);

        registerReceiver(br, intentFilter);

        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        fragmentSet[Fragments.DIALOGSLIST] = new FragmentDialogsList();
        fragmentSet[Fragments.FRIENDSLIST] = new FragmentFriendsList();
        fragmentSet[Fragments.SETTINGS] = new FragmentSettings();
        fragmentSet[Fragments.SINGLEDIALOG] = new DetailDialogFragment();

        // Add other fragments

        fragmentTransaction = getSupportFragmentManager().beginTransaction();
        fragmentTransaction.add(R.id.fragmentPlace, fragmentSet[Fragments.DIALOGSLIST]);
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
        if (getFragmentManager().getBackStackEntryCount() > 0 ){
            getFragmentManager().popBackStack();
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {

        int id = item.getItemId();


        switch (id) {
            case R.id.nav_dialogs:
                Toast.makeText(ActivityBase.this, "Clicked DIALOGS", Toast.LENGTH_SHORT).show();
                fragmentTransaction = getSupportFragmentManager().beginTransaction();
                fragmentTransaction.replace(R.id.fragmentPlace, fragmentSet[Fragments.DIALOGSLIST]);
                fragmentTransaction.addToBackStack(null);
                fragmentTransaction.commit();
                break;

            case R.id.nav_friends:
                Toast.makeText(ActivityBase.this, "Clicked FRIENDS", Toast.LENGTH_SHORT).show();
                fragmentTransaction = getSupportFragmentManager().beginTransaction();
                fragmentTransaction.replace(R.id.fragmentPlace, fragmentSet[Fragments.FRIENDSLIST]);
                fragmentTransaction.addToBackStack(null);
                fragmentTransaction.commit();
                break;

            case R.id.nav_settings:
                Toast.makeText(ActivityBase.this, "Clicked SETTINGS", Toast.LENGTH_SHORT).show();
                fragmentTransaction = getSupportFragmentManager().beginTransaction();
                fragmentTransaction.replace(R.id.fragmentPlace, fragmentSet[Fragments.SETTINGS]);
                fragmentTransaction.addToBackStack(null);
                fragmentTransaction.commit();
                break;

            case R.id.nav_other:
                Toast.makeText(ActivityBase.this, "Clicked OTHER", Toast.LENGTH_SHORT).show();

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
                            Log.i("inList", String.valueOf((inList.get(2))));
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                        DetailDialogFragment newFragment = DetailDialogFragment.getInstance(id, inList, outList);
                        Toast.makeText(ActivityBase.this, "Clicked SETTINGS", Toast.LENGTH_SHORT).show();
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
