package com.example.tiffintrain;

import java.io.Serializable;

public class TiffinCentre implements Serializable {
    private String name ;
    private String address ;
    private String email ;
    private int pincode ;
    private int contactNo ;
    private double centre_latitude;
    private double centre_longitude;

    public TiffinCentre(){

    }

    public TiffinCentre(String name , String email , String address , int pincode , int contactNo , double centre_latitude , double centre_longitude){
        this.name = name ;
        this.address = address ;
        this.email = email ;
        this.contactNo = contactNo ;
        this.pincode= pincode ;
        this.centre_latitude = centre_latitude ;
        this.centre_longitude = centre_longitude ;
    }

    public String getEmail() {
        return email;
    }

    public String getName() {
        return name;
    }

    public int getContactNo() {
        return contactNo;
    }

    public int getPincode() {
        return pincode;
    }

    public String getAddress() {
        return address;
    }

    public double getCentre_latitude() {
        return centre_latitude;
    }

    public double getCentre_longitude() {
        return centre_longitude;
    }
}
