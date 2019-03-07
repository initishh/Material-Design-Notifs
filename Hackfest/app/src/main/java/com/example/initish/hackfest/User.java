package com.example.initish.hackfest;

public class User extends UserId{

    String name,image;

    public User(String name, String image) {
        this.name = name;
        this.image = image;
    }

    public User() {
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }
}
