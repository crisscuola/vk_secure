package com.example.kirill.techpark16;

import android.util.Base64;

import com.example.kirill.techpark16.Fragments.ActivityBase;

import java.security.NoSuchAlgorithmException;
import java.security.PublicKey;
import java.security.spec.InvalidKeySpecException;

/**
 * Created by kirill on 22.04.16
 */
//TODO: requests to server
public class PublicKeyHandler {

    public static PublicKey downloadFriendPublicKey(int id) throws InvalidKeySpecException, NoSuchAlgorithmException {
        String pk = "here's gonna be friend's public key from server";
        byte[] pkBytes = Base64.decode(pk, Base64.DEFAULT);
        ActivityBase.encryptor.setPublicKey(pkBytes);
        return ActivityBase.encryptor.rsaInstance.getPublicKey();
    }

    public static int uploadPublicKey(){
        PublicKey pk = ActivityBase.encryptor.rsaInstance.getPublicKey();
        return 1;
    }

}
