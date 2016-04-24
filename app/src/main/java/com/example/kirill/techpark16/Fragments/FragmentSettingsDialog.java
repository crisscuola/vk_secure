package com.example.kirill.techpark16.Fragments;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.example.kirill.techpark16.R;
import com.vk.sdk.api.VKParameters;
import com.vk.sdk.api.VKRequest;
import com.vk.sdk.api.VKResponse;
import com.vk.sdk.api.model.VKList;

import org.json.JSONArray;
import org.json.JSONObject;

/**
 * Created by konstantin on 10.04.16.
 */
public class FragmentSettingsDialog extends Fragment {

        private Button pull_key;
        VKList list_s;
        static int title_id;

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

            final TextView user_status = (TextView) view.findViewById(R.id.user_status);

            pull_key = (Button) view.findViewById(R.id.pull_key);

            final int id_user = title_id;

            pull_key.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    Log.i("SETIDCHECK", String.valueOf(title_id));

                    final VKRequest request_key  = new VKRequest("notes.get", VKParameters.from("user_id", title_id));

                    request_key.executeWithListener(new VKRequest.VKRequestListener() {
                        @Override
                        public void onComplete(VKResponse response) {
                            super.onComplete(response);

                            try {
                                JSONArray array = response.json.getJSONObject("response").getJSONArray("items");
                                Log.d("get_note_len", String.valueOf(array.length()));

                                JSONObject note_get = array.getJSONObject(0);

                                String pkBase64 = String.valueOf(note_get.get("title"));

                                ActivityBase.encryptionFriend.setPublicKey(pkBase64);

                                Log.i("get_note", String.valueOf(note_get.get("title")));


                            } catch (Exception e) {
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

