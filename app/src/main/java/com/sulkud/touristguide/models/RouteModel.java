package com.sulkud.touristguide.models;

public class RouteModel {

    public String destination_addresses;

    public String origin_addresses;

    public Elements[] rows;

    public String status;

    public class Elements {

        public DistanceDuration distance;

        public DistanceDuration duration;
    }

    public class DistanceDuration {
        public String text;
        public int value;
    }

}
