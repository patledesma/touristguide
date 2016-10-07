package com.sulkud.touristguide.models;

import com.google.gson.annotations.SerializedName;

import org.apache.commons.lang.builder.ToStringBuilder;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class RouteModel {


    @SerializedName("destination_addresses")
    public List<String> destinationAddresses = new ArrayList<>();

    @SerializedName("origin_addresses")
    public List<String> originAddresses = new ArrayList<>();

    @SerializedName("rows")
    public List<Row> rows = new ArrayList<>();

    @SerializedName("status")
    public String status;

    @Override
    public String toString() {
        return "=\n===========================================\n" +
                Arrays.deepToString(destinationAddresses.toArray()) + "\n" +
                Arrays.deepToString(originAddresses.toArray()) + "\n" +
                Arrays.deepToString(rows.toArray()) + "\n" +
                status + "\n===========================================";
    }

    public class Row {

        @SerializedName("elements")
        public List<Element> elements = new ArrayList<>();

        @Override
        public String toString() {
            return ToStringBuilder.reflectionToString(this);
        }
    }

    public class Element {

        @SerializedName("distance")
        public Distance distance;

        @SerializedName("duration")
        public Duration duration;

        @SerializedName("status")
        public String status;

        @Override
        public String toString() {
            return ToStringBuilder.reflectionToString(this);
        }

    }

    public class Duration {

        @SerializedName("text")
        public String text;

        @SerializedName("value")
        public int value;

        @Override
        public String toString() {
            return ToStringBuilder.reflectionToString(this);
        }

    }

    public class Distance {

        @SerializedName("text")
        public String text;

        @SerializedName("value")
        public int value;

        @Override
        public String toString() {
            return ToStringBuilder.reflectionToString(this);
        }
    }
}
