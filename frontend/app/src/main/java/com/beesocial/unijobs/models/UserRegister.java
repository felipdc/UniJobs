package com.beesocial.unijobs.models;

import java.io.Serializable;

@SuppressWarnings("serial")

public class UserRegister implements Serializable {

    private String password, email, name, image, phoneNumber, facebook;

    public UserRegister(String email, String name, String image, String phoneNumber, String facebook, String password) {
        this.email = email;
        this.name = name;
        this.image = image;
        this.phoneNumber = phoneNumber;
        this.facebook = facebook;
        this.password = password;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
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

    public String getphoneNumber() {
        return phoneNumber;
    }

    public void setphoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public String getFacebook() {
        return facebook;
    }

    public void setFacebook(String facebook) {
        this.facebook = facebook;
    }

}
