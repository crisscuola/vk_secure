package com.example.kirill.techpark16.Fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import com.example.kirill.techpark16.MyMessagesHistory;
import com.example.kirill.techpark16.PublicKeysTable;
import com.example.kirill.techpark16.R;
import com.vk.sdk.api.VKRequest;
import com.vk.sdk.api.VKResponse;

/**
 * Created by konstantin on 09.04.16.
 */
public class FragmentSettings extends android.support.v4.app.Fragment {

    Button setOff, clearDB;

    public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
    }

    public View onCreateView(final LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_settings, container, false);

        FragmentSettingsDialog.flag = true;

        setOff = (Button) view.findViewById(R.id.off_button);
        setOff.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final VKRequest request_offline = new VKRequest("account.setOffline");
                request_offline.executeWithListener(new VKRequest.VKRequestListener() {
                    @Override
                    public void onComplete(VKResponse response) {
                        super.onComplete(response);
                    }
                });
            }
        });

        clearDB = (Button) view.findViewById(R.id.clear_db);

        clearDB.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                PublicKeysTable.deleteAll(PublicKeysTable.class);
                MyMessagesHistory.deleteAll(MyMessagesHistory.class);
                Toast.makeText(getContext(),"Данные очищены..", Toast.LENGTH_SHORT).show();

            }
        });

        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        getActivity().setTitle(R.string.settings_title);
        getActivity().findViewById(R.id.toolbar).findViewById(R.id.toolbar_button).setVisibility(View.INVISIBLE);
    }

}
