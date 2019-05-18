package com.beesocial.unijobs.models;

public class UserRegister {

    private String email;
    private String name;
    private String password;
    private String image;

    public UserRegister(String email, String name, String password, String image) {
        this.email = email;
        this.password = password;
        this.name = name;
        this.image = image;
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

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String password) {
        this.image = image;
    }

}