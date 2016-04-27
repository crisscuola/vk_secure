package com.example.kirill.techpark16;

import android.util.Base64;
import android.util.Log;

import com.example.kirill.techpark16.Fragments.ActivityBase;

import org.json.JSONException;
import org.json.JSONObject;

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

    public static String downloadFriendPublicKey(int friendId) throws InvalidKeySpecException, NoSuchAlgorithmException {
        List<PublicKeysTable> friendsKey = new ArrayList<>();
        String pk = "";
        friendsKey = PublicKeysTable.find(PublicKeysTable.class, "user_id = ?", String.valueOf(friendId));
        if (friendsKey.size() != 0){
            pk = friendsKey.get(0).getPk();
            Log.d("resp",pk);
        } else {
            try {
                pk = requestPublicKeyFromServer(friendId);
                Log.d("resp_friend_pk", pk);
                if (!pk.equals("none")){
                    PublicKeysTable key = new PublicKeysTable(friendId, pk);
                    key.save();
                }
            } catch (IOException | JSONException e) {
                e.printStackTrace();
            }

        }
        return pk;
    }
    //TODO: finish this method
    private static String uploadMyPublicKey(int friendId) {
        String pk = "no";
        String myPk = ActivityBase.publicKey;
        JSONObject json;
        try {
            String response = client.doPostRequest(String.valueOf(friendId), myPk);
            json = new JSONObject(response);
            pk = json.getString("key");
            Log.d("resp_upload", pk);

        } catch (IOException | JSONException e) {
            e.printStackTrace();
        }

        return pk;
    }

    private static String requestPublicKeyFromServer(int friendId) throws IOException, JSONException {
        String key = "none";
        String response = client.doGetRequest(String.valueOf(friendId));
        Log.d("resp", response);
        JSONObject json = new JSONObject(response);
        int status = json.getInt("status");
        if (status == 2) {
            key = uploadMyPublicKey(friendId);
            Log.d("resp", "status=2");
            //show message: friend hasn't started dialog yet
        } else if (status == 1) {
            Log.d("resp", "status=1");
            //show message: friend hasn't started dialog yet
        } else if (status == 0) {
            key = json.getString("key");
            if (!json.getBoolean("my_key"))
                uploadMyPublicKey(friendId);
            Log.d("resp_st0", key);
            //uploadMyPK()
        }
        return key;
    }

    public static String deleteMyPublicKey() {
        String response = "none";
        try {
            response =  client.doPostRequest();
        } catch (IOException e) {
            e.printStackTrace();
        }
        Log.d("pk_del", response);
        return response;
    }
}
