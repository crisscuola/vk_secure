package com.example.kirill.techpark16;

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

    public static String downloadFriendPublicKey(int friendId) throws InvalidKeySpecException, NoSuchAlgorithmException, IOException, JSONException {
        List<PublicKeysTable> friendsKey = new ArrayList<>();
        String pk = "";
        friendsKey = PublicKeysTable.find(PublicKeysTable.class, "user_id = ?", String.valueOf(friendId));
        if (friendsKey.size() != 0){
            pk = friendsKey.get(0).getPk();
            Log.d("resp_from_db",pk);
            String response = client.doGetRequest(String.valueOf(friendId));
            JSONObject json = new JSONObject(response);
            int status = json.getInt("status");
            boolean myKey = json.getBoolean("my_key");
            if (status == 0 && !myKey){
                String key = uploadMyPublicKey(friendId);
                Log.d("resp_st0", key);
            }

            String friendPk = requestPublicKeyFromServer(friendId);
            if (!friendPk.equals(pk)){
                pk = friendPk;
                friendsKey.get(0).pk = pk;
                friendsKey.get(0).save();
                Log.d("resp_not_equals", pk);
//                if (!friendPk.equals("none")){
//                    PublicKeysTable key = new PublicKeysTable(friendId, pk);
//                    key.save();
//                }
            }

        } else {
            try {
                pk = requestPublicKeyFromServer(friendId);
                Log.d("resp_friend_pk_server", pk);
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

    public static String uploadMyPublicKey(int friendId) {
        String pk = "no";
        String myPk = ActivityBase.encryptor.getPublicKey();
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
        Log.d("resp_get_req", response);
        JSONObject json = new JSONObject(response);
        int status = json.getInt("status");
        if (status == 2) {
            uploadMyPublicKey(friendId);
            Log.d("resp", "status=2");
        } else if (status == 1) {
            uploadMyPublicKey(friendId);
            Log.d("resp", "status=1");
        } else if (status == 0) {
            key = json.getString("key");
            if (!json.getBoolean("my_key"))
                uploadMyPublicKey(friendId);
            Log.d("resp_st0", key);
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
