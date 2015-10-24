package com.example.guelmis.deliveryffap.models;

import com.google.android.gms.maps.model.LatLng;

import java.util.ArrayList;

public class Seller {
    private String id;
    private String name;
    private String address;
    private String phone;
   // private String logo_url;
    private ArrayList<LineItem> products;
    private LatLng location;

    public Seller(String _id, String _name, String _address, String _phone, //String _logo_url,
                   LatLng _location, ArrayList<LineItem> _products){
        name = _name;
        address = _address;
        phone = _phone;
       // logo_url = _logo_url;
        id = _id;
        location = _location;
        products = _products;
    }

    public String getAddress() {
        return address;
    }

    public String getName() {
        return name;
    }

    public String getPhone() {
        return phone;
    }

  //  public String getLogo_url() {
 //       return logo_url;
 //   }

    public ArrayList<LineItem> getProducts() {
        return products;
    }


    public String getID() {
        return id;
    }
    public LatLng getLocation() {
        return location;
    }

}
