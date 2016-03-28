package com.android.pralhad.models;

/**
 * Created by Pralhad on 3/22/2016.
 */
public class Restaurant {
    private String name;
    private String address;
    private String rating;
    private String type;

    private double latitude;
    private double longitude;
    private String telephone;

    public Restaurant() {
    }

    public Restaurant(String name, String address, String rating, String type) {
        this.name = name;
        this.address = address;
        this.rating = rating;
        this.type = type;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getRating() {
        if (rating == null || rating == "" || rating == "NA")
            return "";
        else
            return "Rating : " + rating;
    }

    public void setRating(String rating) {
        this.rating = rating;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }

    public String getTelephone() {
        return telephone;
    }

    public void setTelephone(String telephone) {
        this.telephone = telephone;
    }
}
