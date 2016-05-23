package com.example.kirill.techpark16;

import android.util.Log;

/**
 * Created by kirill on 04.05.16
 */
public class ChatMessage {
    private String msg;
    private Boolean out;
    private Long time;


    public ChatMessage(String msg, Boolean out, Long time){
        this.msg = msg;
        this.out = out;
        this.time = time;
    }

    public String getMsg(){
        return msg;
    }

    public Boolean getOut(){
        return out;
    }

    public Long getTime(){
        return time;
    }

    public void setMsg(String msg){
        this.msg = msg;
    }

    public void setOut(Boolean out){
        this.out = out;
    }

    public void setTime(Long time){
        this.time = time;
    }
}
