package com.example.guelmis.deliveryffap.models;

import com.google.android.gms.maps.model.LatLng;

import java.util.ArrayList;

/**
 * Created by mario on 10/18/15.
 */
public class Delivery {
    private int id;
    private int order_id;
    private String username;

    private LatLng userLocation;
    private ArrayList<Seller> sellers;

    public Delivery (int _id, int _order_id, String _uname, LatLng _location, ArrayList _sellers){
        id = _id;
        order_id = _order_id;
        username = _uname;
        userLocation = _location;
        sellers = _sellers;
    }

    public String getUsername() {
        return username;
    }

    public LatLng getUserLocation() {
        return userLocation;
    }

    public ArrayList<Seller> getSellers() {
        return sellers;
    }

    public int getId() {
        return id;
    }

    public int getOrder_id() {
        return order_id;
    }


}
