package com.example.kirill.techpark16;

import android.util.Log;

import java.io.UnsupportedEncodingException;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.security.InvalidKeyException;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.PublicKey;

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
    PrivateKey privateKey;
    byte[] encryptedBytes, dectyptedBytes;
    Cipher cipherToEncrypt, cipherToDecrypt;
    String encryptedString, decryptedString;

    public byte[] encrypt(String plain) throws NoSuchAlgorithmException, NoSuchPaddingException, InvalidKeyException, BadPaddingException, IllegalBlockSizeException, UnsupportedEncodingException {
        kpg = KeyPairGenerator.getInstance("RSA");
        kpg.initialize(1024);
        kp = kpg.genKeyPair();
        publicKey = kp.getPublic();
        privateKey = kp.getPrivate();

        cipherToEncrypt = Cipher.getInstance("RSA");
        cipherToEncrypt.init(Cipher.ENCRYPT_MODE,publicKey);
        encryptedBytes = cipherToEncrypt.doFinal(plain.getBytes());
        encryptedString = new String(encryptedBytes, "UTF-8");
        Log.i("encryptedStr", encryptedString);

        return encryptedBytes;
    }

    public String decrypt(byte[] encryptedBytes) throws NoSuchPaddingException, NoSuchAlgorithmException, InvalidKeyException, BadPaddingException, IllegalBlockSizeException {
        cipherToDecrypt = Cipher.getInstance("RSA");
        cipherToDecrypt.init(Cipher.DECRYPT_MODE, privateKey);
        dectyptedBytes = cipherToDecrypt.doFinal(encryptedBytes);
        decryptedString = new String(dectyptedBytes);
        Log.i("decryptedStr", decryptedString);

        return decryptedString;
    }
}
