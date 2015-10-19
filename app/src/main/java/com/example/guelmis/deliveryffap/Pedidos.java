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
    ListView List;
    ArrayAdapter<String> adaptador;
    ArrayList<String> datos;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.pedidos);

        datos = new ArrayList<>();
       // FillList();
        String usuario = getIntent().getStringExtra("usuario");
        ArrayList<DeliveryInfo> deliveries = ServerSignal.listDeliveries(usuario);
        for(int i=0; i<deliveries.size(); i++){
            datos.add("Orden " + deliveries.get(i).getOrder_id() + ", Cliente " + deliveries.get(i).getUsername());
        }
        List = (ListView) findViewById(R.id.ListaPedidos);
      //  Toast.makeText(Pedidos.this, new Double(Double.parseDouble("10.6 km".split("\\s+")[0])).toString(), Toast.LENGTH_LONG).show();

        adaptador = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, datos);
        List.setAdapter(adaptador);
        List.setOnItemClickListener(new AdapterView.OnItemClickListener() {


            @Override
            public void onItemClick(AdapterView<?> arg0, View vista, int posicion, long arg3) {
                Delivery test = ServerSignal.getFullDelivery(Integer.toString(1));
                Boolean stop = true;
            }
        });
    }
    public void mapa1 (){
        Intent LatPoint= new Intent (this, Rutas.class);
        Bundle point = new Bundle();
        point.putDouble("Lat1",18.5007493);
        point.putDouble("Long1",-69.7902433);
        point.putDouble("Lat2",18.4792998);
        point.putDouble("Long2",-69.8661014);
        point.putDouble("Lat3",18.5126984);
        point.putDouble("Long3",-69.8409318);
        point.putDouble("Lat4",18.5062603);
        point.putDouble("Long4", -69.8566022);
        point.putDouble("Lat5",18.4882953);
        point.putDouble("Long5",-69.9270731);
        LatPoint.putExtras(point);
        startActivity(LatPoint);
    }
   private void FillList(){

        datos.add("Orden 1");
      //  datos.add("Luz Trasera Toyota Camry 2010");
      //  datos.add("Luz Delantera Mazda 3 2013");
      //  datos.add("Bumper Lexus IS 250 2009");
      //  datos.add("Motor Volkswagen Jetta 2011");
    }
}