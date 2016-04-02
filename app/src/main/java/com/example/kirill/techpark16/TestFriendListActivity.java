package com.example.kirill.techpark16;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

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
import com.vk.sdk.api.model.VKList;

/**
 * Created by konstantin on 02.04.16.
 */
public class TestFriendListActivity extends AppCompatActivity{


    private String [] scope = new String[] {VKScope.MESSAGES,VKScope.FRIENDS,VKScope.WALL};

    static protected VKList list = new VKList();



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.friends_fragments_activity);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null)
        {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        SideMenu.getDrawer(this, toolbar).build();


        //VKSdk.login(this, scope);
        final ListView listView = (ListView) findViewById(R.id.listViewFriends);


        VKRequest request = VKApi.friends().get(VKParameters.from(VKApiConst.FIELDS, "first_name, last_name", "order", "hints"));

        //VKRequest request = VKApi.friends().getOnline(VKParameters.from());


        request.executeWithListener(new VKRequest.VKRequestListener() {
                                        @Override
                                        public void onComplete(VKResponse response) {
                                            super.onComplete(response);

                                            VKList list = (VKList) response.parsedModel;


                                            ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(TestFriendListActivity.this,
                                                    android.R.layout.simple_expandable_list_item_1, list);



                                            listView.setAdapter(arrayAdapter);
                                        }
                                    }
        );


    }



    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (!VKSdk.onActivityResult(requestCode, resultCode, data, new VKCallback<VKAccessToken>() {
            @Override
            public void onResult(VKAccessToken res) {


            }

            @Override
            public void onError(VKError error) {
                Toast.makeText(getApplicationContext(), "Not Good", Toast.LENGTH_LONG).show();
            }
        })) {
            super.onActivityResult(requestCode, resultCode, data);
        }
    }

}