package com.example.kirill.techpark16.Fragments;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.example.kirill.techpark16.R;
import com.vk.sdk.api.VKApi;
import com.vk.sdk.api.VKApiConst;
import com.vk.sdk.api.VKParameters;
import com.vk.sdk.api.VKRequest;
import com.vk.sdk.api.VKResponse;
import com.vk.sdk.api.model.VKApiModel;
import com.vk.sdk.api.model.VKList;

import org.json.JSONException;

/**
 * Created by konstantin on 10.04.16.
 */
public class FragmentSettingsDialog extends Fragment {

        private Button pull_key;
         VKList list_s;

        public void onCreate(Bundle savedInstanceState){
            super.onCreate(savedInstanceState);
        }

        public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

            View view = inflater.inflate(R.layout.fragment_dialog_settings, container, false);

            final TextView user_status = (TextView) view.findViewById(R.id.user_status);

            pull_key = (Button) view.findViewById(R.id.pull_key);

            final int id_user = 20759745;

            pull_key.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    VKRequest request_key = VKApi.users().get(VKParameters.from(VKApiConst.USER_IDS, id_user, VKApiConst.FIELDS, "status"));

                    request_key.executeWithListener(new VKRequest.VKRequestListener() {
                        @Override
                        public void onComplete(VKResponse response) {
                           // VKList list = new VKList();

                            list_s = (VKList) response.parsedModel;

                            final String[] status = {""};

                            VKApiModel a = list_s.get(0);

                            try {
                                status[0] = a.fields.getString("status");
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }

                            user_status.setText(status[0]);

                            super.onComplete(response);
                        }
                    });
                }
            });

            return view;
        }

        @Override
        public void onResume() {
            super.onResume();
            getActivity().setTitle(R.string.single_dialog_title);
            getActivity().findViewById(R.id.toolbar).findViewById(R.id.toolbar_button).setVisibility(View.INVISIBLE);
        }



    }

