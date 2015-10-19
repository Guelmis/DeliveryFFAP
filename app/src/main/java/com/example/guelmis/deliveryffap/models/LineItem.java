package com.example.guelmis.deliveryffap.models;

public class LineItem extends Product {
    private int quantity;

    public LineItem(Product input, int _q){
        title = input.title;
        brand = input.brand;
        model = input.model;
        imageurl = input.imageurl;
        year = input.year;
        id = input.id;

        quantity = _q;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public void addOne(){
        this.quantity += 1;
    }

    public void substractOne(){
        this.quantity -= 1;
    }
}