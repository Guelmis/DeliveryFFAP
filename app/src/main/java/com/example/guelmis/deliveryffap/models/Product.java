package com.example.guelmis.deliveryffap.models;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import java.util.ArrayList;

public class Product {
    protected String title;
    protected String brand;
    protected String model;
    protected String imageurl;
    protected Integer year;
  //  protected Double price;
    protected String id;

    public Product(){
        title = null;
        brand = null;
        model = null;
        imageurl = null;
        year = null;
     //   price = null;
        id = null;
    }

    public Product(String ptitle, String pbrand, String pmodel, String pimageurl, int pyear, String pid){
        title = ptitle;
        brand = pbrand;
        model = pmodel;
        imageurl = pimageurl;
        year = pyear;
        id = pid;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDesc() {
        return title + " " + brand + " " + model +" "+ year;
    }


    public String getBrand() {
        return brand;
    }

    public void setBrand(String brand) {
        this.brand = brand;
    }

    public String getModel() {
        return model;
    }

    public void setModel(String model) {
        this.model = model;
    }

    public String getImageurl() {
        return imageurl;
    }

    public void setImageurl(String imageurl) {
        this.imageurl = imageurl;
    }

    public Integer getYear() {
        return year;
    }

    public void setYear(Integer year) {
        this.year = year;
    }

    public String getId() { return id; }

    public void setId(String id) { this.id = id; }

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
        if(!(obj instanceof Product)){
            return false;
        }
        if(obj == this){
            return true;
        }

        Product tocomp = (Product) obj;
        return new EqualsBuilder().
                append(brand, tocomp.brand).
                append(model, tocomp.model).
                append(year, tocomp.year).
                append(imageurl, tocomp.imageurl).
                isEquals();
    }
}