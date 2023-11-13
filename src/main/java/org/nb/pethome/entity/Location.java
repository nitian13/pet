package org.nb.pethome.entity;

import lombok.Data;

@Data
public class Location {
    private String formattedAddress; //地址
    private double longitude;//经度
    private double latitude;//维度
    public Location(String formattedAddress,double longitude, double latitude) {
        this.formattedAddress = formattedAddress;
        this.longitude = longitude;
        this.latitude = latitude;
    }
}
