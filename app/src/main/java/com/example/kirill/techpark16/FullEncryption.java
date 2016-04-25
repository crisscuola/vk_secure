package com.example.kirill.techpark16;

import android.util.Base64;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;

/**
 * Created by kirill on 21.04.16
 */
public class FullEncryption {
    public RSAEncryption rsaInstance;

    public FullEncryption() {
        rsaInstance = new RSAEncryption();
    }

    public String encode(String inputMessage) throws Exception {
        byte[] msgBytes = rsaInstance.encrypt(inputMessage);
        return Base64.encodeToString(msgBytes, Base64.DEFAULT);
    }

    public String decode(String inputMessage)  throws Exception{
        byte[] msgBytes = Base64.decode(inputMessage, Base64.DEFAULT);
        return rsaInstance.decrypt(msgBytes);
    }

    public String getPublicKey(){
        byte[] pk = rsaInstance.getPublicKey().getEncoded();
        return Base64.encodeToString(pk, Base64.DEFAULT);
    }

    public String getPrivateKey(){
        byte[] pk = rsaInstance.getPrivateKey().getEncoded();
        return Base64.encodeToString(pk, Base64.DEFAULT);
    }

    public void setPublicKey(String pk) throws InvalidKeySpecException, NoSuchAlgorithmException {
        byte[] pkBytes = Base64.decode(pk, Base64.DEFAULT);
        rsaInstance.setPublicKey(pkBytes);
    }
}
