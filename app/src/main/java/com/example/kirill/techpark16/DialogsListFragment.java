package com.example.kirill.techpark16;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.ListFragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;

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
public class DialogsListFragment extends ListFragment {
    String[] numbers_text = new String[]{"one", "two", "three", "four",
            "five", "six", "seven", "eight", "nine", "ten", "eleven",
            "twelve", "thirteen", "fourteen", "fifteen"};

    static ArrayList<String> msgs = new ArrayList<>();
    static ArrayList<String> username = new ArrayList<>();

    private onItemSelectedListener mCallback;

    @Override
    public void onListItemClick(ListView l, View v, int position, long id) {
        mCallback.onDialogSelected(position);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        final VKRequest request = VKApi.messages().getDialogs(VKParameters.from(VKApiConst.COUNT, 10));
        request.executeWithListener(new VKRequest.VKRequestListener() {
            @Override
            public void onComplete(VKResponse response) {
                super.onComplete(response);

                VKApiGetDialogResponse getMessagesResponse = (VKApiGetDialogResponse) response.parsedModel;

                final VKList<VKApiDialog> list = getMessagesResponse.items;

                ArrayList<String> messages = new ArrayList<>();
                ArrayList<String> users = new ArrayList<>();

                for (VKApiDialog msg : list) {
                    users.add(String.valueOf(msg.message.user_id));

                    messages.add(msg.message.body);
                    DialogsListFragment.msgs.add(msg.message.body);
                    DialogsListFragment.username.add(String.valueOf(msg.message.user_id));
                    Log.i("message", msg.message.body);
                }
                Log.i("aaa", msgs.get(2));
            }
        });
        ArrayAdapter<String> ad = new ArrayAdapter<String>(inflater.getContext(), R.layout.dialogs_fragment,
                R.id.msg, msgs);

        ArrayAdapter<String> ad2 = new ArrayAdapter<String>(inflater.getContext(), R.layout.dialogs_fragment,
                R.id.user_name, username);

        DialogsListAdapter adapter = new DialogsListAdapter(inflater.getContext(),username,msgs);
        setListAdapter(adapter);

        return super.onCreateView(inflater, container, savedInstanceState);
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
