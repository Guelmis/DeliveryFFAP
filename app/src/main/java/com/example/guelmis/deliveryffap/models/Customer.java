package com.example.guelmis.deliveryffap.models;

import com.google.android.gms.maps.model.LatLng;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import java.util.ArrayList;

/**
 * Created by mario on 11/09/15.
 */
public class Customer {
    private String name;
    private ArrayList<Integer> delivery_ids;
    private ArrayList<Integer> order_ids;
    private LatLng location;
    private ArrayList<LineItem> orderedItems;

    public Customer(String _name, int _delid, int _oid, LatLng _loca, ArrayList<LineItem> _itemsOrdered){
        name = _name;
        delivery_ids = new ArrayList<>();
        delivery_ids.add(_delid);
        order_ids = new ArrayList<>();
        order_ids.add(_oid);
        location = _loca;
        orderedItems = _itemsOrdered;
    }

    public Customer(String _name, ArrayList<Integer> _delids, ArrayList<Integer> _oids, LatLng _loca, ArrayList<LineItem> _itemsOrdered){
        name = _name;
        delivery_ids = _delids;
        order_ids = _oids;
        location = _loca;
        orderedItems = _itemsOrdered;
    }

    public String getName() {
        return name;
    }

    public LatLng getLocation() {
        return location;
    }

    public ArrayList<LineItem> getOrderedItems() {
        return orderedItems;
    }

    public ArrayList<Integer> getDeliveryIDs() {
        return delivery_ids;
    }

    public ArrayList<Integer> getOrderIDs() {
        return order_ids;
    }

    public void mergeCustomer(Customer input){
        delivery_ids.addAll(input.delivery_ids);
        order_ids.addAll(input.order_ids);
        for (LineItem product: input.orderedItems) {
            if(orderedItems.contains(product)){
                int index = orderedItems.indexOf(product);
                orderedItems.get(index).setQuantity(orderedItems.get(index).getQuantity() + product.getQuantity());
            }
            else {
                orderedItems.add(product);
            }
        }
    }

    @Override
    public int hashCode(){

        return new HashCodeBuilder(13, 29).
                append(name).
                append(location).
                toHashCode();
    }

    @Override
    public boolean equals(Object obj){
        if(!(obj instanceof Customer)){
            return false;
        }
        if(obj == this){
            return true;
        }

        Customer tocomp = (Customer) obj;
        return new EqualsBuilder().
                append(name, tocomp.name).
                append(location, tocomp.location).
                isEquals();
    }

}
