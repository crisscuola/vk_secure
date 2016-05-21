package com.example.kirill.techpark16.Fragments;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.ListFragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
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
    private VKList<VKApiUser> usersArray;
    private  ArrayList id_array = new ArrayList();

    final private String ENCRYPTED_MSG = "[ENCRYPTED MESSAGE]";
    final private String MEDIA_MSG = "[MEDIA MESSAGE]";
    private final String PREFIX = "cpslbs_";



    @Override
    public void onListItemClick(ListView l, View v, int position, long id) {
        mCallback.onDialogSelected(position);
    }

    @Override
    public View onCreateView(final LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        FragmentSettingsDialog.flag = true;

        final VKRequest request_dialogs_one = VKApi.messages().getDialogs(VKParameters.from(VKApiConst.COUNT, 10));

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

                        usersArray = (VKList<VKApiUser>) response.parsedModel;

                        final VKRequest request_dialogs_two = VKApi.messages().getDialogs(
                                VKParameters.from(VKApiConst.COUNT, 10));
                        request_dialogs_two.executeWithListener(new VKRequest.VKRequestListener() {
                            @Override
                            public void onComplete(VKResponse response) {
                                super.onComplete(response);

                                VKApiGetDialogResponse getMessagesResponse = (VKApiGetDialogResponse)
                                        response.parsedModel;

                                final VKList<VKApiDialog> list = getMessagesResponse.items;


                                 final ArrayList<String> messages = new ArrayList<>();

                                for ( final VKApiDialog msg : list) {

                                    VKApiMessage test = msg.message;

                                    if (test.body.length() == 174 && test.body.charAt(test.body.length() - 1) == '=') {
                                        messages.add(ENCRYPTED_MSG);
                                    } else if (msg.message.attachments.size() != 0 || msg.message.body.isEmpty()
                                            || !msg.message.fwd_messages.isEmpty()){
                                        messages.add(MEDIA_MSG);
                                    } else {
                                        String mess =  msg.message.body;

                                        mess = mess.replaceAll("\\r|\\n", PREFIX);

                                        if (mess.contains(PREFIX)) {
                                            Integer pos = mess.indexOf(PREFIX);
                                            mess = mess.substring(0, pos);
                                            mess += " ...";
                                        }

                                        if ( mess.length() > 30) {
                                           mess = mess.substring(0,30);
                                           mess += " ...";
                                        }
                                        messages.add(mess);
                                    }
                                }
                                setListAdapter(new DialogsListAdapter(inflater.getContext(), usersArray, messages));
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
