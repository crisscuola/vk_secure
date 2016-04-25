package com.example.kirill.techpark16;

import android.util.Log;

import com.example.kirill.techpark16.Fragments.ActivityBase;

import java.io.IOException;

import okhttp3.FormBody;
import okhttp3.HttpUrl;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

/**
 * Created by kirill on 24.04.16
 */
public class HttpConnectionHandler {
    final static String URL = "www.sermalenk.myjino.ru";
    final static String PATH_1 = "dialog";
    final static String PATH_2 = "key";
    OkHttpClient client;

    public HttpConnectionHandler(){
        client = new OkHttpClient();
    }

    public String doGetRequest(String friendId) throws IOException {

        HttpUrl url = new HttpUrl.Builder()
                .scheme("http")
                .host(URL)
                .addPathSegment(PATH_1)
                .addPathSegment(PATH_2)
                .addQueryParameter("my_id", String.valueOf(ActivityBase.MY_ID))
                .addQueryParameter("to_id", friendId)
                .build();

        Request request = new Request.Builder()
                .url(url)
                .build();
        Response response = client.newCall(request).execute();

        return response.body().string();
    }

    public String doPostRequest(String friendId, String pk) throws IOException {

        HttpUrl url = new HttpUrl.Builder()
                .scheme("http")
                .host(URL)
                .addPathSegment(PATH_1)
                .addPathSegment(PATH_2)
                .addPathSegment("")
                .build();


        RequestBody formBody = new FormBody.Builder()
                .add("my_id", String.valueOf(ActivityBase.MY_ID))
                .add("to_id", friendId)
                .add("key", pk)
                .build();

        Request request = new Request.Builder()
                .url(url)
                .post(formBody)
                .build();


        Response response = client.newCall(request).execute();

        if (!response.isSuccessful()) throw new IOException("Unexpected code " + response);

        return  response.body().string();
    }
}
