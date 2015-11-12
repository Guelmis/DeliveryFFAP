package com.example.guelmis.deliveryffap.models;

import com.google.android.gms.maps.model.LatLng;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

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

    public void absorbProductList(ArrayList<LineItem> list){
        for (LineItem thisItem: list) {
            if (products.contains(thisItem)){
                int index = products.indexOf(thisItem);
                products.get(index).setQuantity(products.get(index).getQuantity() + thisItem.getQuantity());
            }
            else{
                products.add(thisItem);
            }
        }
    }
    
    @Override
    public int hashCode(){

        return new HashCodeBuilder(13, 29).append(id).
                append(name).
                append(address).
                append(phone).
                append(location).
                toHashCode();
    }

    @Override
    public boolean equals(Object obj){
        if(!(obj instanceof Seller)){
            return false;
        }
        if(obj == this){
            return true;
        }

        Seller tocomp = (Seller) obj;
        return new EqualsBuilder().
                append(id, tocomp.id).
                append(name, tocomp.name).
                append(address, tocomp.address).
                append(phone, tocomp.phone).
                append(location, tocomp.location).
                isEquals();
    }

}
