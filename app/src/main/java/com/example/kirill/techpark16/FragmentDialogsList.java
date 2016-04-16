package com.example.kirill.techpark16;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.ListFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import com.example.kirill.techpark16.Adapters.DialogsListAdapter;
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
 * Created by kirill on 17.03.16
 */
public class FragmentDialogsList extends ListFragment {

    private onItemSelectedListener mCallback;
    private VKList list,list_s;
    private  ArrayList id_array = new ArrayList();



    @Override
    public void onListItemClick(ListView l, View v, int position, long id) {
        mCallback.onDialogSelected(position);
    }

    @Override
    public View onCreateView(final LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

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

                VKRequest my_request = VKApi.users().get(VKParameters.from(VKApiConst.USER_IDS, id_array, VKApiConst.FIELDS, "first_name, last_name"));

                my_request.executeWithListener(new VKRequest.VKRequestListener() {
                    @Override
                    public void onComplete(VKResponse response) {
                        super.onComplete(response);

                        list_s = (VKList) response.parsedModel;


                final VKRequest request_dialogs_two = VKApi.messages().getDialogs(VKParameters.from(VKApiConst.COUNT, 10));
                request_dialogs_two.executeWithListener(new VKRequest.VKRequestListener() {
                    @Override
                    public void onComplete(VKResponse response) {
                        super.onComplete(response);

                        VKApiGetDialogResponse getMessagesResponse = (VKApiGetDialogResponse) response.parsedModel;

                        final VKList<VKApiDialog> list = getMessagesResponse.items;


                         final ArrayList<String> messages = new ArrayList<>();
                         final ArrayList<String> users = new ArrayList<>();

                        for ( final VKApiDialog msg : list) {

                            users.add(String.valueOf(FragmentDialogsList.this.list_s.getById(msg.message.user_id)));
                            messages.add(msg.message.body);


                        }
                        setListAdapter(new DialogsListAdapter(inflater.getContext(), users, messages));
                    }
                });

                    }
                });

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

        Activity a;

        if (context instanceof Activity){
            a=(Activity) context;
            try {
                mCallback = (onItemSelectedListener) a;
            } catch (ClassCastException e) {
                throw new ClassCastException(a.toString()
                        + " must implement OnItemSelectedListener");
            }
        }
    }

    public interface onItemSelectedListener {
        public void onDialogSelected(int position);
    }
}
