package com.example.kirill.techpark16.Fragments;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import com.example.kirill.techpark16.MyMessagesHistory;
import com.example.kirill.techpark16.PublicKeyHandler;
import com.example.kirill.techpark16.PublicKeysTable;
import com.example.kirill.techpark16.R;
import com.vk.sdk.api.VKRequest;
import com.vk.sdk.api.VKResponse;

import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;
import java.util.List;

/**
 * Created by konstantin on 09.04.16
 */
public class FragmentSettings extends android.support.v4.app.Fragment {

    Button setOff, clearDB;
    AlertDialog.Builder ad;

    public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
    }

    public View onCreateView(final LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_settings, container, false);

        FragmentSettingsDialog.flag = true;

        String title = "Вы уверены, что хотите очистить кэш?";
        String message = "В результате этого действия будут сгенерированы новые ключи шифрования.";
        String button1String = "Да";
        String button2String = "Отмена";

        ad = new AlertDialog.Builder(getContext());
        ad.setTitle(title);
        ad.setMessage(message);
        ad.setPositiveButton(button1String, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                PublicKeysTable.deleteAll(PublicKeysTable.class);
                MyMessagesHistory.deleteAll(MyMessagesHistory.class);
                new PublicKeyChecking().execute();
                Toast.makeText(getContext(),"Данные очищены.", Toast.LENGTH_SHORT).show();
            }
        });
        ad.setNegativeButton(button2String, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int arg1) {

            }
        });
        ad.setCancelable(true);
        ad.setOnCancelListener(new DialogInterface.OnCancelListener() {
            public void onCancel(DialogInterface dialog) {
                Toast.makeText(getContext(), "Вы ничего не выбрали",
                        Toast.LENGTH_SHORT).show();
            }
        });

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
                Toast.makeText(getContext(),"Вы офлайн.", Toast.LENGTH_SHORT).show();
            }
        });

        clearDB = (Button) view.findViewById(R.id.clear_db);

        clearDB.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ad.show();
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

    private class PublicKeyChecking extends AsyncTask<Object, Void, Void> {

        @Override
        protected Void doInBackground(Object[] params) {
            List<PublicKeysTable> myPk = PublicKeysTable.find(PublicKeysTable.class,"user_id = ?",
                    String.valueOf(0));
            List<PublicKeysTable> priv = PublicKeysTable.find(PublicKeysTable.class, "user_id = ?",
                    String.valueOf(-1));
            if (myPk.size() == 0) {
                try {
                    ActivityBase.encryptor.rsaInstance.generateKeys();
                    PublicKeyHandler.deleteMyPublicKey();
                    PublicKeysTable pk = new PublicKeysTable(0, ActivityBase.encryptor.getPublicKey());
                    pk.save();
                    pk = new PublicKeysTable(-1, ActivityBase.encryptor.getPrivateKey());
                    pk.save();
                } catch (NoSuchAlgorithmException | InvalidKeySpecException e) {
                    e.printStackTrace();
                }
            } else {
                try {
                    ActivityBase.encryptor.setPublicKey(myPk.get(0).getPk());
                    ActivityBase.encryptor.setPrivateKey(priv.get(0).getPk());

                    Log.d("pk_publ", ActivityBase.encryptor.getPublicKey());

                    Log.d("pk_from_DB", "publ: " + ActivityBase.encryptor.getPublicKey() + " priv: "
                            + ActivityBase.encryptor.getPrivateKey());
                } catch (InvalidKeySpecException | NoSuchAlgorithmException e) {
                    e.printStackTrace();
                }
            }
            return null;
        }
    }

}
