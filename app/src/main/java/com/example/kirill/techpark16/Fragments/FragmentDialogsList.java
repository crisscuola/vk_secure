package com.example.kirill.techpark16.Fragments;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.ListFragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ListView;

import com.example.kirill.techpark16.Adapters.DialogsListAdapter;
import com.example.kirill.techpark16.R;
import com.vk.sdk.api.VKApi;
import com.vk.sdk.api.VKApiConst;
import com.vk.sdk.api.VKError;
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
 * Created by kirill on 17.03.16
 */
public class FragmentDialogsList extends ListFragment {

    private onItemSelectedListener mCallback;
    private VKList<VKApiUser> usersPhoto;
    private VKList usersArray;
    private  ArrayList id_array = new ArrayList();

    final private String ENCRYPTED_MSG = "[ENCRYPTED MESSAGE]";
    final private String MEDIA_MSG = "[MEDIA MESSAGE]";
    private final String PREFIX = "cps_lbs_";



    @Override
    public void onListItemClick(ListView l, View v, int position, long id) {
        mCallback.onDialogSelected(position);
    }

    @Override
    public View onCreateView(final LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        final VKRequest request_dialogs_one = VKApi.messages().getDialogs(VKParameters.from(VKApiConst.COUNT, 30));

        request_dialogs_one.executeWithListener(new VKRequest.VKRequestListener() {
            @Override
            public void onComplete(VKResponse response) {

                super.onComplete(response);
                VKApiGetDialogResponse getMessagesResponse = (VKApiGetDialogResponse) response.parsedModel;

                final VKList<VKApiDialog> list = getMessagesResponse.items;

                for ( final VKApiDialog msg : list) {

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

                                 final ArrayList<VKApiMessage> messages = new ArrayList<>();
                                 final ArrayList<String> users = new ArrayList<>();
                                 final  ArrayList<Integer> ids = new ArrayList<>();
                                 final  VKList<VKApiUser> photo = new VKList<>();

                                for ( final VKApiDialog msg : list) {

                                    VKApiMessage test = msg.message;

                                    if (test.title.equals(" ... ")) {

                                    if (test.body.length() == 174 && test.body.charAt(test.body.length() - 1) == '=') {
                                        VKApiMessage vkApiMessage = new VKApiMessage();
                                        vkApiMessage.body = ENCRYPTED_MSG;
                                        vkApiMessage.read_state = msg.message.read_state;
                                        messages.add(vkApiMessage);
                                    } else if (msg.message.attachments.size() != 0 || msg.message.body.isEmpty()
                                            || !msg.message.fwd_messages.isEmpty()){
                                        VKApiMessage vkApiMessage = new VKApiMessage();
                                        vkApiMessage.body = MEDIA_MSG;
                                        vkApiMessage.read_state = msg.message.read_state;
                                        messages.add(vkApiMessage);
                                    } else {
                                        VKApiMessage vkApiMessage = msg.message;

                                        String mess = "";

                                        if (msg.message.out == true) {
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
                                        users.add(String.valueOf(FragmentDialogsList.this.usersArray.getById(msg.message.user_id)));
                                        photo.add(FragmentDialogsList.this.usersPhoto.getById(msg.message.user_id));
                                    }
                                    ids.add(msg.message.user_id);


                                }
                                setListAdapter(new DialogsListAdapter(inflater.getContext(), users, messages,photo));
                            }
                        });
                    }
                });
            }

            @Override
            public void onError(VKError error) {
                Log.i("ERROR_DIA", String.valueOf(error));
                super.onError(error);
            }
        });


        return super.onCreateView(inflater, container, savedInstanceState);
    }

    @Override
    public void onResume() {
        super.onResume();
        getActivity().setTitle(R.string.dialog_list_title);
        getActivity().findViewById(R.id.toolbar).findViewById(R.id.toolbar_button).setVisibility(View.VISIBLE);
        Button myButton = (Button) getActivity().findViewById(R.id.toolbar).findViewById(R.id.toolbar_button);
        myButton.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.ic_add_white_24dp, 0);
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

        Activity activity;

        if (context instanceof Activity){
            activity=(Activity) context;
            try {
                mCallback = (onItemSelectedListener) activity;
            } catch (ClassCastException e) {
                throw new ClassCastException(activity.toString()
                        + " must implement OnItemSelectedListener");
            }
        }
    }

    public interface onItemSelectedListener {
        void onDialogSelected(int position);
    }
}
