package com.example.guelmis.deliveryffap;

/**
 * Created by Guelmis on 11/15/2015.
 */
        import android.support.v7.widget.RecyclerView;
        import android.view.LayoutInflater;
        import android.view.View;
        import android.view.ViewGroup;
        import android.widget.TextView;

        import com.example.guelmis.deliveryffap.models.Customer;
        import com.example.guelmis.deliveryffap.models.DeliveryInfo;
        import com.example.guelmis.deliveryffap.models.LineItem;
        import com.example.guelmis.deliveryffap.models.Seller;
        import java.util.ArrayList;
        import java.util.HashMap;

public class ProductsListRoute extends
        RecyclerView.Adapter<ProductsListRoute.ContactViewHolder> {
    ArrayList<Seller> deliveryData;

    public ProductsListRoute(ArrayList<Seller> datosSeller) {
        this.deliveryData = datosSeller;
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
    public void onBindViewHolder(ContactViewHolder contactViewHolder, int i) {
        //ContactInfo ci = contactList.get(i);
        contactViewHolder.vName.setText(deliveryData.get(i).getName());
        contactViewHolder.vPhone.setText(deliveryData.get(i).getPhone());
        contactViewHolder.vDireccion.setText(deliveryData.get(i).getAddress());
        contactViewHolder.vItem.setText(itemsString(deliveryData.get(i).getProducts()));
      //  int j=0;
    }

    @Override
    public ContactViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        View itemView = LayoutInflater.
                from(viewGroup.getContext()).
                inflate(R.layout.list_mapa_detail, viewGroup, false);
       // ContactViewHolder pvh = new ContactViewHolder (itemView);
        return new ContactViewHolder(itemView);
    }

    public static class ContactViewHolder extends RecyclerView.ViewHolder {

        protected TextView vName;
        protected TextView vPhone;
        protected TextView vItem;
        protected TextView vTitle;
        protected TextView vDireccion;
        protected TextView ItemName;

        public ContactViewHolder(View v) {
            super(v);
            vName =  (TextView) v.findViewById(R.id.textViewName);
            vPhone = (TextView)  v.findViewById(R.id.textViewPhone);
            vDireccion = (TextView)v.findViewById(R.id.textViewDireccion);
            vItem = (TextView)  v.findViewById(R.id.textViewProductos);
            ItemName = (TextView) v.findViewById(R.id.txtItems);
            //int j = 0;
        }
    }
    @Override
    public int getItemCount() {

        return deliveryData.size();
    }
}