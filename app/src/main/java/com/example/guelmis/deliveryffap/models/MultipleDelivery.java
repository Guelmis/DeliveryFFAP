package com.example.guelmis.deliveryffap.models;

import com.google.android.gms.maps.model.LatLng;

import java.util.ArrayList;

/**
 * Created by mario on 11/09/15.
 */
public class MultipleDelivery {
    private ArrayList<Customer> customers;
    private ArrayList<Seller> sellers;

    public MultipleDelivery(ArrayList<Customer> _customers, ArrayList<Seller> _sellers){
        customers = _customers;
        sellers = _sellers;
    }

    public ArrayList<Seller> getSellers() {
        return sellers;
    }

    public ArrayList<Customer> getCustomers() {
        return customers;
    }

    public ArrayList<Integer> getAllDeliveryIDs(){
        ArrayList<Integer> ret = new ArrayList<>();

        for(Customer thisCustomer : customers){
            ret.addAll(thisCustomer.getDeliveryIDs());
        }

        return ret;
    }

    public ArrayList<LatLng> getAllSellerLocations(){
        ArrayList<LatLng> ret = new ArrayList<>();

        for(Seller seller: sellers){
            ret.add(seller.getLocation());
        }

        return ret;
    }

    public ArrayList<LatLng> getAllCustomerLocations(){
        ArrayList<LatLng> ret = new ArrayList<>();

        for(Customer customer: customers){
            ret.add(customer.getLocation());
        }

        return ret;
    }
}
