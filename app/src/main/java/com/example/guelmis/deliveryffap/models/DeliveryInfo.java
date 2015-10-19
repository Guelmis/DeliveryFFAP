package com.example.guelmis.deliveryffap.models;

/**
 * Created by mario on 10/18/15.
 */
public class DeliveryInfo {
    private int id;
    private int order_id;
    private String username;

    public DeliveryInfo(int _id, int _order_id, String _uname){
        id = _id;
        order_id = _order_id;
        username = _uname;
    }

    public int getId() {
        return id;
    }

    public int getOrder_id() {
        return order_id;
    }

    public String getUsername() {
        return username;
    }
}
