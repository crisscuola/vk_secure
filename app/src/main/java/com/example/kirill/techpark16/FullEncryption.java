package com.example.kirill.techpark16;

import android.util.Base64;
import android.util.Log;

import com.example.kirill.techpark16.Fragments.ActivityBase;

import java.io.UnsupportedEncodingException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;

/**
 * Created by kirill on 21.04.16
 */
public class FullEncryption {
    RSAEncryption rsaInstance;

    public FullEncryption() {
        rsaInstance = new RSAEncryption();
        try {
            rsaInstance.generateKeys();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public String encode(String inputMessage) throws Exception {
        byte[] msgBytes = rsaInstance.encrypt(inputMessage);
        return Base64.encodeToString(msgBytes, Base64.DEFAULT);
    }

    public String decode(String inputMessage)  throws Exception{
        byte[] msgBytes = Base64.decode(inputMessage, Base64.DEFAULT);
        return rsaInstance.decrypt(msgBytes);
    }
}
