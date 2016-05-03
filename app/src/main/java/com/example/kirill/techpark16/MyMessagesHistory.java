package com.example.kirill.techpark16;

import com.orm.SugarRecord;

/**
 * Created by kirill on 03.05.16
 */
public class MyMessagesHistory extends SugarRecord {
    String msg;


    public MyMessagesHistory() {
    }

    public MyMessagesHistory(String msg){
        this.msg = msg;
    }

    public String getMsg(){
        return msg;
    }

}
