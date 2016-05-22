package com.example.kirill.techpark16;

import android.util.Log;

import java.io.UnsupportedEncodingException;
import java.security.InvalidKeyException;
import java.security.KeyFactory;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;

/**
 * Created by kirill on 12.04.16
 */
public class RSAEncryption {

    KeyPairGenerator kpg;
    KeyPair kp;
    PublicKey publicKey;
    byte[] publicKeyBytes;
    PrivateKey privateKey;
    byte[] encryptedData, decryptedData;
    Cipher cipherToEncrypt, cipherToDecrypt;
    String encryptedString, decryptedString;

    public RSAEncryption(){
    }

    public void generateKeys() throws NoSuchAlgorithmException, InvalidKeySpecException {
        kpg = KeyPairGenerator.getInstance("RSA");
        kpg.initialize(1024);
        kp = kpg.genKeyPair();
        publicKeyBytes = kp.getPublic().getEncoded();
        this.setPublicKey(publicKeyBytes);
        privateKey = kp.getPrivate();
        Log.d("pk", "keys generated");
    }


    public byte[] encrypt(String plain) throws NoSuchAlgorithmException, NoSuchPaddingException,
            InvalidKeyException, BadPaddingException, IllegalBlockSizeException, UnsupportedEncodingException,
            InvalidKeySpecException {

        cipherToEncrypt = Cipher.getInstance("RSA");
        cipherToEncrypt.init(Cipher.ENCRYPT_MODE, publicKey);
        encryptedData= cipherToEncrypt.doFinal(plain.getBytes());
        encryptedString = new String(encryptedData, "UTF-8");

        return encryptedData;
    }

    public String decrypt(byte[] encryptedData) throws NoSuchPaddingException, NoSuchAlgorithmException,
            InvalidKeyException, BadPaddingException, IllegalBlockSizeException {
        cipherToDecrypt = Cipher.getInstance("RSA");
        cipherToDecrypt.init(Cipher.DECRYPT_MODE, privateKey);
        decryptedData= cipherToDecrypt.doFinal(encryptedData);
        decryptedString = new String(decryptedData);

        return decryptedString;
    }

    public void setPublicKey(byte[] bytes) throws NoSuchAlgorithmException, InvalidKeySpecException {
        publicKey = KeyFactory.getInstance("RSA").generatePublic(new X509EncodedKeySpec(bytes));
    }

    public void setPrivateKey(byte[] bytes) throws NoSuchAlgorithmException, InvalidKeySpecException {
        privateKey = KeyFactory.getInstance("RSA").generatePrivate(new PKCS8EncodedKeySpec(bytes));
    }

    public PrivateKey getPrivateKey() {
        return privateKey;
    }

    public  PublicKey getPublicKey() {
        return publicKey;
    }

}
