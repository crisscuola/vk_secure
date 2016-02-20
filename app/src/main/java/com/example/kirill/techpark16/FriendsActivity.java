package com.example.kirill.techpark16;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.view.View;
import android.widget.Button;

/**
 * Created by kirill on 18.02.16.
 */
public class FriendsActivity extends Activity {//FragmentActivity{

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.friends_fragments_activity);

        final Button button1 = (Button) findViewById(R.id.test_friend_btn);
        button1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent activity1 = new Intent(view.getContext(), MenuActivity.class);
                startActivity(activity1);
            }
        });
    }

}
