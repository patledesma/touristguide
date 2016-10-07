package com.sulkud.touristguide.models;

import com.google.gson.annotations.SerializedName;

public class RouteModel {

    @SerializedName("destination_addresses")
    public String destination_addresses;

    @SerializedName("origin_addresses")
    public String origin_addresses;

    @SerializedName("rows")
    public Elements[] rows;

    @SerializedName("status")
    public String status;

    public class Elements {

        @SerializedName("distance")
        public DistanceDuration distance;

        @SerializedName("duration")
        public DistanceDuration duration;

        @SerializedName("status")
        public String status;
    }

    public class DistanceDuration {

        @SerializedName("text")
        public String text;

        @SerializedName("value")
        public int value;
    }

}
