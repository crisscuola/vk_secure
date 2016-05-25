package com.example.kirill.techpark16.Fragments;

import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;
import android.widget.ToggleButton;

import com.example.kirill.techpark16.MyMessagesHistory;
import com.example.kirill.techpark16.PublicKeyHandler;
import com.example.kirill.techpark16.PublicKeysTable;
import com.example.kirill.techpark16.R;
import com.vk.sdk.api.VKApiConst;
import com.vk.sdk.api.VKParameters;
import com.vk.sdk.api.VKRequest;
import com.vk.sdk.api.VKResponse;

import org.json.JSONException;
import java.util.List;

/**
 * Created by konstantin on 10.04.16
 */
public class FragmentSettingsDialog extends Fragment {

    private final String NEW_DEVICE_NOTIFICATION = "Я пишу с нового устройства.";
    static int title_id;
    Button newDevice;
    ToggleButton encryptionMode;
    static boolean flag = false;

    public static FragmentSettingsDialog getInstance(int user_id){
        FragmentSettingsDialog fragmentSettingsDialog = new FragmentSettingsDialog();
        Bundle bundle = new Bundle();

        title_id = user_id;

        fragmentSettingsDialog.setArguments(bundle);
        return fragmentSettingsDialog;
    }

    private class UploadKey extends AsyncTask<String, Void, String> {
        String pk = "no";
        @Override
        protected String doInBackground(String... params) {
            pk = PublicKeyHandler.uploadMyPublicKey(title_id);

            return pk;
        }

        @Override
        protected void onPostExecute(String result) {
            if(!pk.equals("no")) {
                VKRequest request = new VKRequest("messages.send", VKParameters.from(VKApiConst.USER_ID, title_id,
                        VKApiConst.MESSAGE, NEW_DEVICE_NOTIFICATION));

                request.executeWithListener(new VKRequest.VKRequestListener() {
                    @Override
                    public void onComplete(VKResponse response) {
                    super.onComplete(response);
                    int msgId;
                    try {
                        msgId = (int) response.json.get("response");
                        MyMessagesHistory myMessage = new MyMessagesHistory(title_id,
                                NEW_DEVICE_NOTIFICATION, msgId);
                        myMessage.save();
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    }
                });
                Toast.makeText(getContext(), "Ключ отправлен", Toast.LENGTH_SHORT).show();
            } else
                Toast.makeText(getContext(), "Ключ не был отправлен", Toast.LENGTH_SHORT).show();
            newDevice.setText("Загрузить новый ключ");
            newDevice.setClickable(true);
        }

        @Override
        protected void onPreExecute() {
            newDevice.setClickable(false);
            newDevice.setText("Отправка ключа");
        }

        @Override
        protected void onProgressUpdate(Void... values) {
        }
    }


    public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
    }

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_dialog_settings, container, false);

        flag = true;

        newDevice = (Button) view.findViewById(R.id.new_device);
        //final UploadKey uploadKey = new UploadKey();
        newDevice.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    new UploadKey().execute();
                } catch (IllegalStateException e) {
                    e.printStackTrace();
                    Toast.makeText(getContext(),"Вы уже отправили ключ.",Toast.LENGTH_SHORT).show();
                }
            }
        });

        encryptionMode = (ToggleButton) view.findViewById(R.id.mode);
        encryptionMode.setChecked(PublicKeyHandler.checkEncryprionMode(title_id));
        encryptionMode.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d("resp_encMode", String.valueOf(encryptionMode.isChecked()));
            }
        });

        VKRequest request_long_poll = new VKRequest("messages.getLongPollServer", VKParameters.from("need_pts", 1));
        request_long_poll.executeWithListener(new VKRequest.VKRequestListener() {
            @Override
            public void onComplete(VKResponse response) {
                super.onComplete(response);
                try {
                    ActivityBase.pts = response.json.getJSONObject("response").getInt("pts");
                } catch (JSONException e) {
                    e.printStackTrace();
                }
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

    @Override
    public void onStop() {
        super.onStop();

        PublicKeysTable key;
        List<PublicKeysTable> friend = PublicKeysTable.find(PublicKeysTable.class, "user_id = ?",
                String.valueOf(title_id));
        if (friend.size() == 0){
            key = new PublicKeysTable(title_id, null);
        } else {
            key = friend.get(0);
        }
        key.setEncryptionMode(encryptionMode.isChecked());
        key.save();
    }
}

