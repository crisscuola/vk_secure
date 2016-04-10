package com.example.kirill.techpark16.Test;

import android.app.ListFragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.kirill.techpark16.DialogsListAdapter;
import com.vk.sdk.api.VKApi;
import com.vk.sdk.api.VKApiConst;
import com.vk.sdk.api.VKParameters;
import com.vk.sdk.api.VKRequest;
import com.vk.sdk.api.VKResponse;
import com.vk.sdk.api.model.VKApiDialog;
import com.vk.sdk.api.model.VKApiGetDialogResponse;
import com.vk.sdk.api.model.VKList;

import java.util.ArrayList;

/**
 * Created by konstantin on 09.04.16.
 */
public class FragmentDialogsList extends ListFragment{

    private VKList list;

    public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
    }

    public View onCreateView(final LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        VKRequest request_list_friends = VKApi.friends().get(VKParameters.from(VKApiConst.FIELDS, "first_name, last_name", "order", "hints"));

        request_list_friends.executeWithListener(new VKRequest.VKRequestListener() {
            @Override
            public void onComplete(VKResponse response) {

                super.onComplete(response);

                list = (VKList) response.parsedModel;

                final VKRequest request_list_dialogs = VKApi.messages().getDialogs(VKParameters.from(VKApiConst.COUNT, 10));

                request_list_dialogs.executeWithListener(new VKRequest.VKRequestListener() {
                    @Override
                    public void onComplete(VKResponse response) {
                        VKApiGetDialogResponse getMessagesResponse = (VKApiGetDialogResponse) response.parsedModel;

                        final VKList<VKApiDialog> list = getMessagesResponse.items;
                        ArrayList<String> messages = new ArrayList<>();
                        ArrayList<String> users = new ArrayList<>();

                        for (VKApiDialog msg : list) {
                            users.add(String.valueOf(FragmentDialogsList.this.list.getById(msg.message.user_id)));
                            messages.add(msg.message.body);
                        }
                        setListAdapter(new DialogsListAdapter(inflater.getContext(), users, messages));

                        super.onComplete(response);
                    }
                });

            }
        });

        return super.onCreateView(inflater, container, savedInstanceState);
        //return view;
    }

}
