package com.example.kirill.techpark16;

import com.vk.sdk.VKSdk;

/**
 * Created by konstantin on 19.02.16.
 */
public class Application extends android.app.Application {
    @Override
    public void onCreate() {
        super.onCreate();

        VKSdk.initialize(this);
    }
}
