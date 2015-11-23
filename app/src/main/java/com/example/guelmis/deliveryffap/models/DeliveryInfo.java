package com.example.guelmis.deliveryffap.models;

public class DeliveryInfo {
    private int id;
    private int order_id;
    private String username;
    private String invoice;

    public DeliveryInfo(int _id, int _order_id, String _invoice, String _uname){
        id = _id;
        order_id = _order_id;
        username = _uname;
        invoice = _invoice;
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

    public String getInvoice() {
        return invoice;
    }

}
