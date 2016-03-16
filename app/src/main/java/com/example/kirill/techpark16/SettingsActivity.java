package com.example.kirill.techpark16;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;

import com.vk.sdk.VKScope;
import com.vk.sdk.VKSdk;

/**
 * Created by konstantin on 21.02.16.
 */
public class SettingsActivity extends AppCompatActivity{

    private String [] scope = new String[] {VKScope.MESSAGES,VKScope.FRIENDS,VKScope.WALL};


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.settings_activity);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null)
        {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        SideMenu.getDrawer(this, toolbar).build();


        VKSdk.login(this, scope);


    }


}
