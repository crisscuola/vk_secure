package com.example.kirill.techpark16;

import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;

import com.vk.sdk.VKScope;
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

/**
 * Created by kirill on 02.04.16
 */
public class FragmentsActivity extends AppCompatActivity implements DialogsListFragment.onItemSelectedListener{

    private String [] scope = new String[] {VKScope.MESSAGES,VKScope.FRIENDS,VKScope.WALL};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragments_activity);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null)
        {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        SideMenu.getDrawer(this, toolbar).build();


        //VKSdk.login(this, scope);
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



        VKRequest request = new VKRequest("messages.getHistory", VKParameters.from(VKApiConst.USER_ID,id));
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


//                VKApiGetDialogResponse getMessagesResponse = (VKApiGetDialogResponse) response.parsedModel;
//
//                final VKList<VKApiDialog> list = getMessagesResponse.items;
//                int id = list.get(position).message.user_id;

                DetailDialogFragment newFragment = DetailDialogFragment.getInstance(id, inList, outList);
                FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
                transaction.add(R.id.fragment_container, newFragment);
                transaction.addToBackStack(null);
                transaction.commit();
            }

        });

            }

        });


    }
}
