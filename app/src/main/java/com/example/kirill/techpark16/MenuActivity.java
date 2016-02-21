package com.example.kirill.techpark16;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class MenuActivity extends AppCompatActivity {
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.menu_activity);



        //TODO: одна функция для слушания кликов трех кнопок
        final Button friendsBtn =(Button)findViewById(R.id.friends_btn);
        friendsBtn.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                Intent friendsActivity = new Intent(view.getContext(), FriendsActivity.class);
                startActivity(friendsActivity);
            }
        });

        final Button settingsBtn =(Button)findViewById(R.id.settings_btn);
        settingsBtn.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                Intent settingsActivity = new Intent(view.getContext(), SettingsActivity.class);
                startActivity(settingsActivity);
            }
        });

        final Button audioBtn =(Button)findViewById(R.id.audio_btn);
        audioBtn.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                Intent audioActivity = new Intent(view.getContext(), AudioActivity.class);
                startActivity(audioActivity);
            }
        });

    }
}
