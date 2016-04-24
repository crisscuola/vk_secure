package com.example.kirill.techpark16;

import com.orm.SugarApp;
import com.vk.sdk.VKAccessToken;
import com.vk.sdk.VKAccessTokenTracker;
import com.vk.sdk.VKScope;
import com.vk.sdk.VKSdk;

import java.io.UnsupportedEncodingException;


/**
 * Created by konstantin on 19.02.16.
 */
public class Application extends SugarApp {
    private String [] scope = new String[] {VKScope.MESSAGES,VKScope.FRIENDS,VKScope.WALL, VKScope.OFFLINE, VKScope.STATUS, VKScope.NOTES};


    VKAccessTokenTracker vkAccessTokenTracker = new VKAccessTokenTracker() {
        @Override
        public void onVKAccessTokenChanged(VKAccessToken oldToken, VKAccessToken newToken) {

        }
    };
    @Override
    public void onCreate() {
        super.onCreate();
        vkAccessTokenTracker.startTracking();
        VKSdk.initialize(this);

    }

    @Override
    public void onTerminate() {
        super.onTerminate();
    }
}
