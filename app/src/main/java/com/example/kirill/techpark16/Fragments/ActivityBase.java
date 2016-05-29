package com.example.kirill.techpark16.Fragments;

import android.app.AlertDialog;
import android.app.Application;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
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
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.kirill.techpark16.CircleTransform;
import com.example.kirill.techpark16.FullEncryption;
import com.example.kirill.techpark16.PublicKeyHandler;
import com.example.kirill.techpark16.PublicKeysTable;
import com.example.kirill.techpark16.R;
import com.squareup.picasso.Picasso;
import com.vk.sdk.VKAccessToken;
import com.vk.sdk.VKSdk;
import com.vk.sdk.api.VKApi;
import com.vk.sdk.api.VKApiConst;
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

import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;


/**
 * Created by konstantin on 09.04.16
 */
public  class ActivityBase extends AppCompatActivity implements FragmentDialogsList.onItemSelectedListener
        ,NavigationView.OnNavigationItemSelectedListener, FragmentFriendsSend.onItemSelectedListener {

    static public int MY_ID = 0;
    Fragment fragmentSet[] = new Fragment[10];
    ActionBarDrawerToggle toggle;
    android.support.v4.app.FragmentTransaction fragmentTransaction;
    Toolbar toolbar;
    DrawerLayout drawer;
    NavigationView navigationView;
    BroadcastReceiver br;
    Button toolbarButton;
    static Integer pts;
    Context context = this;
    AlertDialog.Builder ad;


    public static FullEncryption encryptor = new FullEncryption();
    final static String BROADCAST_EVENT = "com.example.kirill.techpark16";


    private class PublicKeyChecking extends AsyncTask<Object, Void, Void> {

        @Override
        protected Void doInBackground(Object[] params) {
            List<PublicKeysTable> myPk = PublicKeysTable.find(PublicKeysTable.class,"user_id = ?",
                    String.valueOf(0));
            List<PublicKeysTable> priv = PublicKeysTable.find(PublicKeysTable.class, "user_id = ?",
                    String.valueOf(-1));
            if (myPk.size() == 0) {
                try {
                    encryptor.rsaInstance.generateKeys();
                    PublicKeyHandler.deleteMyPublicKey();
                    PublicKeysTable pk = new PublicKeysTable(0, encryptor.getPublicKey());
                    pk.save();
                    pk = new PublicKeysTable(-1, encryptor.getPrivateKey());
                    pk.save();
                } catch (NoSuchAlgorithmException | InvalidKeySpecException e) {
                    e.printStackTrace();
                }
            } else {
                try {
                    encryptor.setPublicKey(myPk.get(0).getPk());
                    encryptor.setPrivateKey(priv.get(0).getPk());

                    Log.d("pk_publ", encryptor.getPublicKey());

                    Log.d("pk_from_DB", "publ: " + encryptor.getPublicKey() + " priv: "
                            + encryptor.getPrivateKey());
                } catch (InvalidKeySpecException | NoSuchAlgorithmException e) {
                    e.printStackTrace();
                }
            }
            return null;
        }
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.base_activity);


        if (VKAccessToken.currentToken() == null) {
            //nothing to do

        } else
            MY_ID = Integer.parseInt(VKSdk.getAccessToken().userId);

        VKRequest request_long_poll =  new VKRequest("messages.getLongPollServer", VKParameters.from("need_pts", 1));

        request_long_poll.executeWithListener(new VKRequest.VKRequestListener() {
            @Override
            public void onComplete(VKResponse response) {
            super.onComplete(response);
            try {
                pts = response.json.getJSONObject("response").getInt("pts");

                Log.i("PTS", String.valueOf(pts));
            } catch (JSONException e) {
                e.printStackTrace();
            }
            }
        });

        new PublicKeyChecking().execute();


        setBroadcastReceiver();

//        IntentFilter intentFilter = new IntentFilter(BROADCAST_EVENT);

        toolbar = (Toolbar) findViewById(R.id.toolbar);

        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.i("TOOLBAR", "home selected");
            }
        });

        toolbarButton = (Button) toolbar.findViewById(R.id.toolbar_button);

        toolbarButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            View view = getCurrentFocus();
            if (view != null) {
                InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
            }

            Fragment currentFragment = getSupportFragmentManager().findFragmentById(R.id.fragmentPlace);

            if (currentFragment instanceof FragmentDialogsList) {

                fragmentTransaction = getSupportFragmentManager().beginTransaction();
                fragmentTransaction.replace(R.id.fragmentPlace, fragmentSet[FragmentsConst.FRIENDSEND]);
                fragmentTransaction.addToBackStack(null);
                fragmentTransaction.commit();
                toolbar.setTitle(R.string.send);
                toolbar.findViewById(R.id.toolbar_button).setVisibility(View.INVISIBLE);
            }

            if (currentFragment instanceof FragmentSingleDialog) {

                int id = FragmentSingleDialog.title_id;

                FragmentSettingsDialog newFragment = FragmentSettingsDialog.getInstance(id);
                fragmentTransaction = getSupportFragmentManager().beginTransaction();
                fragmentTransaction.replace(R.id.fragmentPlace, newFragment);
                fragmentTransaction.addToBackStack(null);
                fragmentTransaction.commit();
                toolbar.setTitle(R.string.friends_title);
                toolbar.findViewById(R.id.toolbar_button).setVisibility(View.INVISIBLE);
            }

            if (currentFragment instanceof FragmentSettingsDialog) {

                Collections.reverse(FragmentSingleDialog.vkMessages);
            }
            }
        });

        setSupportActionBar(toolbar);

        drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        toggle = new ActionBarDrawerToggle(this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close)
        {
            @Override
            public void onDrawerStateChanged(int newState) {
                super.onDrawerStateChanged(newState);
                View view = getCurrentFocus();
                if (view != null) {
                    InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
                }
            }
        };
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

        String title = "Внимание!";
        String message = "Вы уверены, что хотите выйти?";
        String button1String = "Выйти";
        String button2String = "Отмена";

        ad = new AlertDialog.Builder(context);
        ad.setTitle(title);
        ad.setMessage(message);
        ad.setPositiveButton(button1String, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                VKSdk.logout();
                Intent intent = new Intent(context, ActivitySplashScreen.class);
                startActivity(intent);
                finish();
            }
        });
        ad.setNegativeButton(button2String, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int arg1) {

            }
        });
        ad.setCancelable(true);
        ad.setOnCancelListener(new DialogInterface.OnCancelListener() {
            public void onCancel(DialogInterface dialog) {
                Toast.makeText(context, "Вы ничего не выбрали",
                        Toast.LENGTH_LONG).show();
            }
        });

    }

    @Override
    protected void onStop() {
        super.onStop();
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

    protected Application getApp() {
        return getApplication();
    }

    @Override
    protected void onResume() {

        Bundle extras = getIntent().getExtras();

        fragmentSet[FragmentsConst.DIALOGSLIST] = new FragmentDialogsList();
        fragmentSet[FragmentsConst.DIALOGSLIST].setArguments(extras);

        fragmentTransaction = getSupportFragmentManager().beginTransaction();
        fragmentTransaction.replace(R.id.fragmentPlace, fragmentSet[FragmentsConst.DIALOGSLIST]);

        //fragmentTransaction.addToBackStack(null);
        fragmentTransaction.commit();
        toolbar.setTitle(R.string.dialog_list_title);
        toolbar.findViewById(R.id.toolbar_button).setVisibility(View.VISIBLE);

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

        VKRequest request = new VKRequest("users.get", VKParameters.from(VKApiConst.USER_IDS,MY_ID,
                VKApiConst.FIELDS, "photo_100","first_name, last_name"));

        request.executeWithListener(new VKRequest.VKRequestListener() {
            @Override
            public void onComplete(VKResponse response) {

            String first_name = " ";
            String last_name = " ";
            String photo_url = " ";

            try {
                JSONArray array = response.json.getJSONArray("response");
                first_name = array.getJSONObject(0).getString("first_name");
                last_name = array.getJSONObject(0).getString("last_name");
                photo_url = array.getJSONObject(0).getString("photo_100");

                Log.i("PHOTO", photo_url);

            } catch (JSONException e) {
                e.printStackTrace();
            }

            TextView name = (TextView) findViewById(R.id.nav_username);
            ImageView image = (ImageView) findViewById(R.id.imageView);
            name.setText(first_name + " " + last_name);

            Picasso.with(context).load(photo_url).transform(new CircleTransform())
                        .placeholder(R.drawable.placeholder_dark)
                        .into(image);

            super.onComplete(response);
            }
        });


        return true;
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {

        int id = item.getItemId();

        switch (id) {
            case R.id.nav_dialogs:
                FragmentSettingsDialog.flag = false;
//                FragmentDialogsList newFragment = new FragmentDialogsList();
                fragmentTransaction = getSupportFragmentManager().beginTransaction();
//                fragmentTransaction.replace(R.id.fragmentPlace, newFragment);
                fragmentTransaction.replace(R.id.fragmentPlace, fragmentSet[FragmentsConst.DIALOGSLIST]);
                //fragmentTransaction.addToBackStack(null);
                fragmentTransaction.commit();
                toolbar.setTitle(R.string.dialog_list_title);
                toolbar.findViewById(R.id.toolbar_button).setVisibility(View.VISIBLE);
                break;

            case R.id.nav_friends:
                FragmentSettingsDialog.flag = false;
                fragmentTransaction = getSupportFragmentManager().beginTransaction();
                fragmentTransaction.replace(R.id.fragmentPlace, fragmentSet[FragmentsConst.FRIENDSLIST]);
                //fragmentTransaction.addToBackStack(null);
                fragmentTransaction.commit();
                toolbar.setTitle(R.string.friends_title);
                toolbar.findViewById(R.id.toolbar_button).setVisibility(View.INVISIBLE);
                break;

            case R.id.nav_settings:
                FragmentSettingsDialog.flag = false;
                fragmentTransaction = getSupportFragmentManager().beginTransaction();
                fragmentTransaction.replace(R.id.fragmentPlace, fragmentSet[FragmentsConst.SETTINGS]);
                //fragmentTransaction.addToBackStack(null);
                fragmentTransaction.commit();
                toolbar.setTitle(R.string.settings_title);
                toolbar.findViewById(R.id.toolbar_button).setVisibility(View.INVISIBLE);
                break;

            case R.id.nav_logout:
                ad.show();

        }
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);

        drawer.closeDrawer(GravityCompat.START);
        return true;
    }


    @Override
    public void onDialogSelected(final int position) {
        ProgressBar bar = (ProgressBar) findViewById(R.id.progressDialog);
        if (bar != null)
            bar.setVisibility(View.VISIBLE);

        final VKRequest request = VKApi.messages().getDialogs(VKParameters.from(VKApiConst.COUNT, 30));

        request.executeWithListener(new VKRequest.VKRequestListener() {
            @Override
            public void onComplete(VKResponse response) {
            super.onComplete(response);
            VKApiGetDialogResponse getMessagesResponse = (VKApiGetDialogResponse) response.parsedModel;

            final VKList<VKApiDialog> list = getMessagesResponse.items;

            ArrayList id_array = new ArrayList();

            for ( final VKApiDialog msg : list) {
                if (msg.message.title.equals(" ... "))
                id_array.add(msg.message.user_id);
            }

            final  int id = (int) id_array.get(position);

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
    public void onFriendSendSelected(final int position) {

        final VKRequest request = VKApi.friends().get(VKParameters.from(VKApiConst.FIELDS,
                "first_name, last_name", "order", "hints"));

        request.executeWithListener(new VKRequest.VKRequestListener() {
            @Override
            public void onComplete(VKResponse response) {
            super.onComplete(response);

            VKList list;

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

                String temp ;

                temp = String.valueOf(finalId_f);

                int id = Integer.parseInt(temp);


                FragmentSingleDialog newFragment = FragmentSingleDialog.getInstance(id, msg, ids);
                fragmentTransaction = getSupportFragmentManager().beginTransaction();
                fragmentTransaction.replace(R.id.fragmentPlace, newFragment);
                fragmentTransaction.addToBackStack(null);
                fragmentTransaction.commit();

                }
            });
            }
        });
    }

}
