package com.example.kirill.techpark16;

import com.orm.SugarRecord;

/**
 * Created by kirill on 24.04.16
 */
//TODO: avatar field
public class PublicKeysTable extends SugarRecord {
    Integer userId;
    String pk;


    public PublicKeysTable() {
    }

    public PublicKeysTable(Integer userId, String pk){
        this.userId = userId;
        this.pk = pk;
    }

    public String getPk(){
        return pk;
    }

}
