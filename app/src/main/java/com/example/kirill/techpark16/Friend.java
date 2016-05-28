package com.example.kirill.techpark16;

import com.orm.SugarRecord;

/**
 * Created by kirill on 28.05.16
 */
public class Friend extends SugarRecord {

    String firstName;
    String lastName;
    String photoUrl;
    int friendId;

    public Friend(){}

    public Friend(String firstName, String lastName, String photoUrl, int friendId){
        this.firstName = firstName;
        this.lastName = lastName;
        this.photoUrl = photoUrl;
        this.friendId = friendId;
    }

    public String getFirstName() {
        return firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public int getFriendId() {
        return friendId;
    }

    public String getPhotoUrl() {
        return photoUrl;
    }

    public String getFullName() {
        return firstName + " " + lastName;
    }
}
