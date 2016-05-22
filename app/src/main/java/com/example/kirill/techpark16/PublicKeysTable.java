package com.example.kirill.techpark16;

import com.orm.SugarRecord;

/**
 * Created by kirill on 24.04.16
 */
//TODO: avatar field
public class PublicKeysTable extends SugarRecord {
    Integer userId;
    String pk;
    Boolean encryptionMode;


    public PublicKeysTable() {
    }

    public PublicKeysTable(Integer userId, String pk){
        this.userId = userId;
        this.pk = pk;
        this.encryptionMode = false;
    }

    public String getPk(){
        return pk;
    }

    public boolean getEncryptionMode(){
        return encryptionMode;
    }

    public void setEncryptionMode(boolean encryptionMode){
        this.encryptionMode = encryptionMode;
    }

}
