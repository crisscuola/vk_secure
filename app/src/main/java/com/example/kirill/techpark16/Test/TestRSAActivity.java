package com.example.kirill.techpark16.Test;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.security.KeyChain;
import android.security.KeyChainAliasCallback;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.example.kirill.techpark16.R;
import com.example.kirill.techpark16.RSAEncryption;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.nio.charset.StandardCharsets;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.Signature;
import java.security.cert.CertificateException;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;
import java.security.spec.InvalidKeySpecException;

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;

/**
 * Created by kirill on 13.04.16
 */
public class TestRSAActivity extends AppCompatActivity implements View.OnClickListener,
        KeyChainAliasCallback {

    private static final String TAG = "tag";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_settings);

        if (getSupportActionBar() != null)
        {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        RSAEncryption rsaInstance = new RSAEncryption();
        String message = "Hello";

        try {
            rsaInstance.generateKeys();
            byte[] enc = rsaInstance.encrypt(message);
            String decr = rsaInstance.decrypt(enc);
        }
        catch (Exception e) {
            e.printStackTrace();
        }

        CertificateFactory certFactory = null;
        try {
            certFactory = CertificateFactory.getInstance("X.509");
        } catch (CertificateException e) {
            e.printStackTrace();
        }
        Intent intent = KeyChain.createInstallIntent();
        byte[] bytes = rsaInstance.getPrivateKey().getEncoded();
        InputStream in = new ByteArrayInputStream(bytes);
        try {
            X509Certificate cert = (X509Certificate)certFactory.generateCertificate(in);
        intent.putExtra(KeyChain.EXTRA_CERTIFICATE,cert);
        startActivity(intent);
        } catch (CertificateException e) {
            e.printStackTrace();
        }

    }

    @Override
    public void alias(final String alias) {
        Log.d(TAG, "Thread: " + Thread.currentThread().getName());
        Log.d(TAG, "selected alias: " + alias);
    }

    @Override
    public void onClick(View v) {
        KeyChain.choosePrivateKeyAlias(this, this,
                new String[]{"RSA"}, null, null, -1, null);
    }
}
