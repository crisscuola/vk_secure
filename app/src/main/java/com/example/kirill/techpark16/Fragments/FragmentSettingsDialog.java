package com.example.kirill.techpark16.Fragments;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.example.kirill.techpark16.PublicKeysTable;
import com.example.kirill.techpark16.R;
import com.vk.sdk.api.VKParameters;
import com.vk.sdk.api.VKRequest;
import com.vk.sdk.api.VKResponse;
import com.vk.sdk.api.model.VKList;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.List;

/**
 * Created by konstantin on 10.04.16.
 */
public class FragmentSettingsDialog extends Fragment {

        private Button encryptionSwitcher;
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

            encryptionSwitcher = (Button) view.findViewById(R.id.encryption_switcher);

            encryptionSwitcher.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Toast.makeText(getContext(), "TODO: switcher", Toast.LENGTH_SHORT).show();
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

