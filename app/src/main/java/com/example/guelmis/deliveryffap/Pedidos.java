package com.example.guelmis.deliveryffap;

import java.util.ArrayList;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.guelmis.deliveryffap.models.Delivery;
import com.example.guelmis.deliveryffap.models.DeliveryInfo;
import com.example.guelmis.deliveryffap.signaling.ServerSignal;

public class Pedidos extends Activity {
    private ListView List;
    private ArrayAdapter<String> adaptador;
    private ArrayList<String> datos;
    private ArrayList<DeliveryInfo> deliveries;
    private String usuario;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.pedidos);

        datos = new ArrayList<>();
        usuario = getIntent().getStringExtra("usuario");
        deliveries = ServerSignal.listDeliveries(usuario);
        for(int i=0; i<deliveries.size(); i++){
            datos.add("Orden " + deliveries.get(i).getOrder_id() + ", Cliente " + deliveries.get(i).getUsername());
        }
        List = (ListView) findViewById(R.id.ListaPedidos);

        adaptador = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, datos);
        List.setAdapter(adaptador);
        List.setOnItemClickListener(new AdapterView.OnItemClickListener() {


            @Override
            public void onItemClick(AdapterView<?> arg0, View vista, int posicion, long arg3) {
                Intent myIntent = new Intent(Pedidos.this, Rutas.class);
                Bundle extras = new Bundle();
                extras.putString("usuario",usuario);
                extras.putInt("offset", 0);
                extras.putString("delivery_id", Integer.toString(deliveries.get(posicion).getId()));
                myIntent.putExtras(extras);
                startActivity(myIntent);
            }
        });
    }

    @Override
    protected void onResume(){
        super.onResume();
        deliveries = ServerSignal.listDeliveries(usuario);
        datos.clear();
        for(int i=0; i<deliveries.size(); i++){
            datos.add("Orden " + deliveries.get(i).getOrder_id() + ", Cliente " + deliveries.get(i).getUsername());
        }
        adaptador.notifyDataSetChanged();
    }
}