package com.sulkud.touristguide.models;

public class PlaceModel {

    public int placeID;
    public String placeName;
    public String latitude;
    public String longitude;
    public String placeDescription;
    public String placeTag;

    public PlaceModel(int placeID, String placeName, String latitude, String longitude, String placeDescription, String placeTag){
        this.placeID = placeID;
        this.placeName = placeName;
        this.latitude = latitude;
        this.longitude = longitude;
        this.placeDescription = placeDescription;
        this.placeTag = placeTag;
    }

}
