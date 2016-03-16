package com.example.kirill.techpark16;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;

import com.vk.sdk.VKScope;
import com.vk.sdk.VKSdk;

public class MainActivity extends AppCompatActivity {


    private String [] scope = new String[] {VKScope.MESSAGES,VKScope.FRIENDS,VKScope.WALL};

    @Override
    protected void onCreate(Bundle savedInstanceState)  {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);

        //VKSdk.initialize(this);

        VKSdk.initialize(this);

        VKSdk.login(this, scope);

//        String[] fingerprints = VKUtil.getCertificateFingerprint(this, this.getPackageName());
//        System.out.println(Arrays.asList(fingerprints));



    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu_activity; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);


        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

}
