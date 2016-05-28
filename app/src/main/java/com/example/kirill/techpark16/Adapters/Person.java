package com.example.kirill.techpark16.Adapters;

/**
 * Created by serj on 22.05.16
 */
public class Person {
    String firstName;
    String lastName;
    String photo;
    int id;
    public Person(String firstName, String lastName, String photoUrl, int id) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.photo = photoUrl;
        this.id = id;
    }
    int getId(){
        return this.id;
    }
    String getFullName() {
        return firstName + " " + lastName;
    }
}