package com.example.kirill.techpark16;

import com.orm.SugarRecord;

/**
 * Created by kirill on 03.05.16
 */
public class MyMessagesHistory extends SugarRecord {
    Integer userId;
    String msg;
    Integer msgId;



    public MyMessagesHistory() {
    }

    public MyMessagesHistory(Integer userId, String msg, Integer msgId){
        this.userId = userId;
        this.msg = msg;
        this.msgId = msgId;
    }

    public String getMsg(){
        return msg;
    }

    public Integer getUserId() {
        return userId;
    }

    public Integer getMsgId() {
        return msgId;
    }

}
