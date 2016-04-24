package com.example.kirill.techpark16;

import java.io.IOException;

import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

/**
 * Created by kirill on 24.04.16
 */
public class HttpConnectionHandler {
    final static String GET_REQUEST_URL = "url";
    final static String POST_REQUEST_URL = "url";
    OkHttpClient client;

    HttpConnectionHandler(){
        client = new OkHttpClient();
    }

    public String doGetRequest() throws IOException {
        Request request = new Request.Builder()
                .url(GET_REQUEST_URL)
                .build();
        Response response = client.newCall(request).execute();

        return response.body().string();
    }

    public String doPostRequest(String pk) throws IOException {
        RequestBody body = new FormBody.Builder()
                .add("pk", pk)
                .build();
        Request request = new Request.Builder()
                .url(POST_REQUEST_URL)
                .post(body)
                .build();

        Response response = client.newCall(request).execute();

        return  response.body().string();
    }
}
