package com.example.kirill.techpark16;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import com.vk.sdk.VKAccessToken;
import com.vk.sdk.VKCallback;
import com.vk.sdk.VKScope;
import com.vk.sdk.VKSdk;
import com.vk.sdk.api.VKApi;
import com.vk.sdk.api.VKApiConst;
import com.vk.sdk.api.VKError;
import com.vk.sdk.api.VKParameters;
import com.vk.sdk.api.VKRequest;
import com.vk.sdk.api.VKResponse;
import com.vk.sdk.api.model.VKApiGetMessagesResponse;
import com.vk.sdk.api.model.VKApiMessage;
import com.vk.sdk.api.model.VKList;

import java.util.ArrayList;

/**
 * Created by konstantin on 21.02.16.
 */
public class DialogsListActivity extends AppCompatActivity {

    private String [] scope = new String[] {VKScope.MESSAGES,VKScope.FRIENDS,VKScope.WALL};
    //private ListView listView;
    private Button showMessage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dialogs_activity);


        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null)
        {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        SideMenu.getDrawer(this, toolbar).build();

        VKSdk.login(this, scope);

        showMessage = (Button) findViewById(R.id.showMessage);
        showMessage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                final VKRequest request = VKApi.messages().get(VKParameters.from(VKApiConst.COUNT, 10));
                request.executeWithListener(new VKRequest.VKRequestListener() {
                    @Override
                    public void onComplete(VKResponse response) {
                        super.onComplete(response);

                        final ListView listView = (ListView) findViewById(R.id.listView);

                        VKApiGetMessagesResponse getMessagesResponse = (VKApiGetMessagesResponse) response.parsedModel;

                        VKList<VKApiMessage> list = getMessagesResponse.items;

                        ArrayList<String> arrayList = new ArrayList<>();

                        for (VKApiMessage msg : list) {
                            arrayList.add(msg.body);
                        }

                        ArrayAdapter<String> arrayAdapter = new ArrayAdapter<>(DialogsListActivity.this,
                                android.R.layout.simple_expandable_list_item_1, arrayList);

                        listView.setAdapter(arrayAdapter);
                    }
                });
            }
        });

    }



    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (!VKSdk.onActivityResult(requestCode, resultCode, data, new VKCallback<VKAccessToken>() {
            @Override
            public void onResult(VKAccessToken res) {
// Пользователь успешно авторизовался


            }
            @Override
            public void onError(VKError error) {
// Произошла ошибка авторизации (например, пользователь запретил авторизацию)
            }
        })) {
            super.onActivityResult(requestCode, resultCode, data);
        }
    }




    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        TextView infoTextView = (TextView) findViewById(R.id.textView2);
//        infoTextView.setText("FIRST!!!");

        switch (id) {
            case R.id.action_settings:
                Log.i("settings", "SETTINGS!!!!!");
                infoTextView.setText("seettings!!!");
               // Intent activity1 = new Intent(this, MenuActivity.class);
               // startActivity(activity1);
                return true;
            case R.id.action_friend:
                Intent activity2 = new Intent(this, FriendListActivity.class);
                startActivity(activity2);
                infoTextView.setText("friend");
                return true;
            case R.id.action_audio:

                infoTextView.setText("audio!!!");

                //Intent activity3 = new Intent(this, AudioActivity.class);
                //startActivity(activity3);
                return true;
            default:
                infoTextView.setText("seetting@@222222s!!!");
                return super.onOptionsItemSelected(item);
        }

    }





}
