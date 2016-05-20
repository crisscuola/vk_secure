package com.example.kirill.techpark16.Fragments;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import com.example.kirill.techpark16.MyMessagesHistory;
import com.example.kirill.techpark16.R;
import com.vk.sdk.api.VKApiConst;
import com.vk.sdk.api.VKParameters;
import com.vk.sdk.api.VKRequest;
import com.vk.sdk.api.VKResponse;

import org.json.JSONException;

/**
 * Created by konstantin on 10.04.16.
 */
public class FragmentSettingsDialog extends Fragment {

        private Button encryptionSwitcher;
        static int title_id;
        Button use;
        static boolean flag = false;

    public static FragmentSettingsDialog getInstance(int user_id){
        FragmentSettingsDialog fragmentSettingsDialog = new FragmentSettingsDialog();
        Bundle bundle = new Bundle();

        title_id = user_id;

        fragmentSettingsDialog.setArguments(bundle);
        return fragmentSettingsDialog;
    }


        public void onCreate(Bundle savedInstanceState){
            super.onCreate(savedInstanceState);
        }

        public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

            View view = inflater.inflate(R.layout.fragment_dialog_settings, container, false);

            flag = true;

            encryptionSwitcher = (Button) view.findViewById(R.id.encryption_switcher);

            encryptionSwitcher.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Toast.makeText(getContext(), "TODO: switcher", Toast.LENGTH_SHORT).show();
                }


            });

            use = (Button) view.findViewById(R.id.new_device);

            use.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    VKRequest request = new VKRequest("messages.send", VKParameters.from(VKApiConst.USER_ID, title_id,
                            VKApiConst.MESSAGE, "I wtite on new Device !"));

                    request.executeWithListener(new VKRequest.VKRequestListener() {
                        @Override
                        public void onComplete(VKResponse response) {
                            super.onComplete(response);
                            int msgId;
                            try {
                                msgId = (int) response.json.get("response");
                                MyMessagesHistory myMessage = new MyMessagesHistory(title_id, "I wtite on new Device", msgId);
                                myMessage.save();
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
                    });

                }
            });

            return view;
        }

        @Override
        public void onResume() {
            super.onResume();
            getActivity().setTitle(R.string.settings_dialog_title);
            getActivity().findViewById(R.id.toolbar).findViewById(R.id.toolbar_button).setVisibility(View.INVISIBLE);
        }



    }

