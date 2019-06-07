package com.beesocial.unijobs.models;

import java.io.Serializable;
import java.util.List;

@SuppressWarnings("serial")

public class User implements Serializable {

    private String ps, id, email, name, image, phoneNumber, facebook;
    private List<Service> serviceList;

    public User(String id, String email, String name, String image) {
        this.id = id;
        this.email = email;
        this.name = name;
        this.image = image;
    }

    public User(String id, String email, String name, String image, String phoneNumber, String facebook, List<Service> serviceList) {
        this.id = id;
        this.email = email;
        this.name = name;
        this.image = image;
        this.phoneNumber = phoneNumber;
        this.facebook = facebook;
        this.serviceList = serviceList;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getPs() {
        return ps;
    }

    public void setPs(String ps) {
        this.ps = ps;
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

    public List<Service> getServiceList() {
        return serviceList;
    }

    public void setServiceList(List<Service> serviceList) {
        this.serviceList = serviceList;
    }


}
