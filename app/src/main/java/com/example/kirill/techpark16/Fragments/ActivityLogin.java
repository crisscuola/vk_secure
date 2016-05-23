package com.example.kirill.techpark16.Fragments;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import com.vk.sdk.VKAccessToken;
import com.vk.sdk.VKScope;
import com.vk.sdk.VKSdk;

/**
 * Created by konstantin on 22.05.16.
 */
public class ActivityLogin extends AppCompatActivity {

    private String [] scope = new String[] {VKScope.MESSAGES,VKScope.FRIENDS,VKScope.WALL,
            VKScope.OFFLINE, VKScope.STATUS, VKScope.NOTES};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (VKAccessToken.currentToken() == null) {
            VKSdk.login(this, scope);

        } else {

            Intent intent = new Intent(this, ActivityBase.class);
            startActivity(intent);
        }

    }

    @Override
    protected void onResume() {
        super.onResume();
        if (VKAccessToken.currentToken() == null) {
            // lol

        } else {

            Intent intent = new Intent(this, ActivityBase.class);
            startActivity(intent);
        }

    }

    @Override
    public void onBackPressed() {

        quit();

    }

    public void quit() {
        int pid = android.os.Process.myPid();
        android.os.Process.killProcess(pid);
        System.exit(0);
    }

}
