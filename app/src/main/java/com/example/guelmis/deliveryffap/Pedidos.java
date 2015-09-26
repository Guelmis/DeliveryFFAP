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

public class Pedidos extends Activity {
    public String Punto1;
    public String Punto2;
    ListView List;
    ArrayAdapter<String> adaptador;
    ArrayList<String> datos;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.pedidos);

        datos = new ArrayList<String>();
        FillList();
        List = (ListView) findViewById(R.id.ListaPedidos);

        adaptador = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, datos);
        List.setAdapter(adaptador);
        List.setOnItemClickListener (new AdapterView.OnItemClickListener() {


            @Override
            public void onItemClick(AdapterView<?> arg0, View vista, int posicion, long arg3) {
                if ((String) ((TextView) vista).getText() == "Retrovisor Honda Civic 2007")
                {
                    mapa1();
                }
                else if ((String) ((TextView) vista).getText() == "Luz Trasera Toyota Camry 2010"){

                    mapa2();
                }
                else if ((String) ((TextView) vista).getText() == "Luz Delantera Mazda 3 2013"){

                    mapa3();
                }
                else if ((String) ((TextView) vista).getText() == "Bumper Lexus IS 250 2009"){

                    mapa4();
                }
                else if ((String) ((TextView) vista).getText() == "Motor Volkswagen Jetta 2011"){

                    mapa5();
                }
            }
        });
    }
    public void mapa1 (){
        Intent LatPoint= new Intent (this, Rutas.class);
        Bundle point = new Bundle();
        point.putDouble("Lat",18.5007493);
        point.putDouble("Long",-69.7902433);
        LatPoint.putExtras(point);
        startActivity(LatPoint);
    }

    public void mapa2 (){
        Intent LatPoint= new Intent (this, Rutas.class);
        Bundle point = new Bundle();
        point.putDouble("Lat",18.4792998);
        point.putDouble("Long",-69.8661014);
        LatPoint.putExtras(point);
        startActivity(LatPoint);
    }
    public void mapa3 (){
        Intent LatPoint= new Intent (this, Rutas.class);
        Bundle point = new Bundle();
        point.putDouble("Lat",18.5126984);
        point.putDouble("Long",-69.8409318);
        LatPoint.putExtras(point);
        startActivity(LatPoint);
    }
    public void mapa4 (){
        Intent LatPoint= new Intent (this, Rutas.class);
        Bundle point = new Bundle();
        point.putDouble("Lat",18.5062603);
        point.putDouble("Long", -69.8566022);
        LatPoint.putExtras(point);
        startActivity(LatPoint);
    }
    public void mapa5 (){
        Intent LatPoint= new Intent (this, Rutas.class);
        Bundle point = new Bundle();
        point.putDouble("Lat",18.4882953);
        point.putDouble("Long",-69.9270731);
        LatPoint.putExtras(point);
        startActivity(LatPoint);
    }
    private void FillList(){

        datos.add("Retrovisor Honda Civic 2007");
        datos.add("Luz Trasera Toyota Camry 2010");
        datos.add("Luz Delantera Mazda 3 2013");
        datos.add("Bumper Lexus IS 250 2009");
        datos.add("Motor Volkswagen Jetta 2011");
    }
}