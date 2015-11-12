package com.example.guelmis.deliveryffap.models;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

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

    @Override
    public int hashCode(){

        return new HashCodeBuilder(13, 29).append(title).
                append(brand).
                append(model).
                append(year).
                append(imageurl).
                toHashCode();
    }

    @Override
    public boolean equals(Object obj){
        if(!(obj instanceof LineItem)){
            return false;
        }
        if(obj == this){
            return true;
        }

        LineItem tocomp = (LineItem) obj;
        return new EqualsBuilder().
                append(title, tocomp.title).
                append(brand, tocomp.brand).
                append(model, tocomp.model).
                append(year, tocomp.year).
                append(imageurl, tocomp.imageurl).
                isEquals();
    }
}