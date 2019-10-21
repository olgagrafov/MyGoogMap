package com.example.olgag.mygoogmap.model;

/**
 * Created by Sergey on 15-Sep-17.
 */

public class Place {
    private long id;
    private String city, street, picture,searchType,placesName,distance,href;
    private int icon;
    private double lat, lng;

    public Place(long id, String city, String street, int icon, String searchType, String placesName, String distance, double lat, double lng, String href) {
        this.id = id;
        this.city = city;
        this.street = street;
        this.icon=icon;
        this.searchType = searchType;
        this.placesName = placesName;
        this.distance = distance;
        this.lat = lat;
        this.lng = lng;
        this.href = href;

    }


      public Place(String city, String street, String picture, String searchType, String placesName, String distance, double lat, double lng, String href) {
        this.city = city;
        this.street = street;
        this.picture = picture;
        this.searchType = searchType;
        this.placesName = placesName;
        this.distance = distance;
        this.lat = lat;
        this.lng = lng;
        this.href = href;
    }

    public int getIcon() {
        return icon;
    }

    public void setIcon(int icon) {
        this.icon = icon;
    }

    public String getHref() {
        return href;
    }

    public void setHref(String href) {
        this.href = href;
    }

    public long getId() {
        return id;
    }

    public void setId(long icon) {
        this.id = id;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getStreet() {
        return street;
    }

    public void setStreet(String street) {
        this.street = street;
    }

    public String getPicture() {
        return picture;
    }

    public void setPicture(String picture) {
        this.picture = picture;
    }

    public String getSearchType() {
        return searchType;
    }

    public void setSearchType(String searchType) {
        this.searchType = searchType;
    }

    public String getPlacesName() {
        return placesName;
    }

    public void setPlacesName(String placesName) {
        this.placesName = placesName;
    }

    public String getDistance() {
        return distance;
    }

    public void setDistance(String distance) {
        this.distance = distance;
    }

    public double getLat() {
        return lat;
    }

    public void setLat(double lat) {
        this.lat = lat;
    }

    public double getLng() {
        return lng;
    }

    public void setLng(double lng) {
        this.lng = lng;
    }

    @Override
    public String toString() {
        return "The place should you see " +
                "address is " + street  +
                ", place's name is" + placesName ;
    }
}
