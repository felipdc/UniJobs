package com.beesocial.unijobs.models;

import java.util.List;

public class Service {

    private String name;
    private Date createdAt;
    private Date updatedAt;
    private String description;
    private boolean isOffer;
    private List<String> likedBy;
    private String location;
    private boolean active;
    private String createdBy;

    public String getName() {
        return this.name;
    }

    /**
     * @param name
     */
    public void setName(String name) {
        this.name = name;
    }

    public Date getCreatedAt() {
        return this.createdAt;
    }

    /**
     * @param createdAt
     */
    public void setCreatedAt(Date createdAt) {
        this.createdAt = createdAt;
    }

    public Date getUpdatedAt() {
        return this.updatedAt;
    }

    /**
     * @param updatedAt
     */
    public void setUpdatedAt(Date updatedAt) {
        this.updatedAt = updatedAt;
    }

    public String getDescription() {
        return this.description;
    }

    /**
     * @param description
     */
    public void setDescription(String description) {
        this.description = description;
    }

    public boolean getIsOffer() {
        return this.isOffer;
    }

    /**
     * @param isOffer
     */
    public void setIsOffer(boolean isOffer) {
        this.isOffer = isOffer;
    }

    public List<String> getLikedBy() {
        return this.likedBy;
    }

    /**
     * @param likedBy
     */
    public void setLikedBy(List<String> likedBy) {
        this.likedBy = likedBy;
    }

    public String getLocation() {
        return this.location;
    }

    /**
     * @param location
     */
    public void setLocation(String location) {
        this.location = location;
    }

    public boolean getActive() {
        return this.active;
    }

    /**
     * @param active
     */
    public void setActive(boolean active) {
        this.active = active;
    }

    public String getCreatedBy() {
        return this.createdBy;
    }

    /**
     * @param createdBy
     */
    public void setCreatedBy(String createdBy) {
        this.createdBy = createdBy;
    }

}