package com.example.kirill.techpark16;

import com.orm.SugarRecord;

/**
 * Created by kirill on 03.05.16
 */
public class MyMessagesHistory extends SugarRecord {
    Integer userId;
    String msg;


    public MyMessagesHistory() {
    }

    public MyMessagesHistory(Integer userId, String msg){
        this.userId = userId;
        this.msg = msg;
    }

    public String getMsg(){
        return msg;
    }
    public Integer getUserId() {
        return userId;
    }

}
