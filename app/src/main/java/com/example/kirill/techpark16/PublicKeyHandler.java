package com.example.kirill.techpark16;

import android.util.Base64;

import com.example.kirill.techpark16.Fragments.ActivityBase;

import java.io.IOException;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by kirill on 22.04.16
 */
public class PublicKeyHandler {
    public static HttpConnectionHandler client = new HttpConnectionHandler();

    public static String downloadFriendPublicKey(int id) throws InvalidKeySpecException, NoSuchAlgorithmException {
        List<PublicKeysTable> friendsKey = new ArrayList<>();
        String pk = "";
        friendsKey = PublicKeysTable.find(PublicKeysTable.class, "id = ?", String.valueOf(id));
        if (friendsKey.size() != 0){
            pk = friendsKey.get(0).getPk();
        } else {
            try {
                pk = client.doGetRequest();
            } catch (IOException e) {
                e.printStackTrace();
            }
            ActivityBase.encryptor.setPublicKey(pk);
            PublicKeysTable key = new PublicKeysTable(id, pk);
            key.save();
        }
        return pk;
    }

    public static String uploadMyPublicKey() {
        String pk = "";
        List<PublicKeysTable> myKey = PublicKeysTable.find(PublicKeysTable.class, "id = ?", String.valueOf(ActivityBase.MY_ID));
        if (myKey.size() != 0) {
            pk = myKey.get(0).getPk();
        } else {
            try {
                pk = client.doGetRequest();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return pk;
    }
}
