package com.sadikul.winchat.Network.Model;

/**
 * Created by ASUS on 10-Dec-17.
 */

public class UserProfile {

    String name;
    String status;
    String image;
    String thumbnailImage;
    String device_token;

    public String getToken() {
        return device_token;
    }

    public void setToken(String token) {
        this.device_token = token;
    }


    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getThumbnailImage() {
        return thumbnailImage;
    }

    public void setThumbnailImage(String thumbnailImage) {
        this.thumbnailImage = thumbnailImage;
    }
}
