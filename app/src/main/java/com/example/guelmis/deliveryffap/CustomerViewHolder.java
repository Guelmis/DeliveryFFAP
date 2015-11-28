package com.example.guelmis.deliveryffap;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.guelmis.deliveryffap.models.Customer;
import com.example.guelmis.deliveryffap.models.LineItem;

import java.util.ArrayList;

/**
 * Created by mario on 11/22/15.
 */
public class CustomerViewHolder extends
        RecyclerView.Adapter<ProductsListRoute.ContactViewHolder>{
    ArrayList<Customer> customerData;

    public CustomerViewHolder(ArrayList<Customer> datosCustomer) {
        this.customerData = datosCustomer;
        //stastatus int i =0;
    }

    protected String itemsString(ArrayList<LineItem> input){
        String ret = "";
        for (int i= 0; i<input.size(); i++){
            ret += input.get(i).toString();
            if(i<(input.size()-1)){
                ret += ", ";
            }
        }
        return ret;
    }

    @Override
    public void onBindViewHolder(ProductsListRoute.ContactViewHolder contactViewHolder, int i) {
        //ContactInfo ci = contactList.get(i);
        contactViewHolder.vName.setText(customerData.get(i).getName());
        contactViewHolder.vPhone.setText(customerData.get(i).getPhone());
        contactViewHolder.vDireccion.setText(customerData.get(i).getAddress());
        contactViewHolder.vItem.setText(itemsString(customerData.get(i).getOrderedItems()));
        //  int j=0;
    }

    @Override
    public ProductsListRoute.ContactViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        View itemView = LayoutInflater.
                from(viewGroup.getContext()).
                inflate(R.layout.list_mapa_detail, viewGroup, false);
        // ContactViewHolder pvh = new ContactViewHolder (itemView);
        return new ProductsListRoute.ContactViewHolder(itemView);
    }
    @Override
    public int getItemCount() {

        return customerData.size();
    }
}
