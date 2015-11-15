package com.example.guelmis.deliveryffap;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;

import android.app.ActionBar;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Notification;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.Toolbar;
import android.util.SparseBooleanArray;
import android.view.Menu;
import android.view.MenuItem;
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

public class Pedidos extends ActionBarActivity {
    private ListView List;
    private ArrayAdapter<String> adaptador;
    private ArrayList<String> datos;
    private ArrayList<DeliveryInfo> deliveries;
    private String usuario;
    Button refresh;
    TextView user;
    Button ruta;
    android.support.v7.app.ActionBar actionbar;
    private int orderquant;
    private Toolbar toolbar;
    private TextView textViewDate,textViewCantidad;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.pedidos);
        setActionBar();
        //        actionbar = getSupportActionBar();
//        actionbar.setDisplayShowHomeEnabled(true);
//        actionbar.setTitle("  DeliveryFFAP Ordenes");
//        actionbar.setIcon(R.mipmap.moto_ab);
        ruta = (Button) findViewById(R.id.btnruta);
        refresh = (Button) findViewById (R.id.btnrefresh);
        user = (TextView) findViewById(R.id.textViewUser);
        textViewDate = (TextView) findViewById(R.id.textViewDate);
        textViewCantidad = (TextView) findViewById(R.id.textViewPeiddosCantidad);
        textViewDate.setText(getCurrentDate());
        Intent myIntent = getIntent();
        usuario = myIntent.getStringExtra("usuario");
        user.setText(usuario);
        datos = new ArrayList<>();
        usuario = getIntent().getStringExtra("usuario");
        deliveries = ServerSignal.listDeliveries(usuario);
        for(int i=0; i<deliveries.size(); i++){
            datos.add("Orden "+ deliveries.get(i).getOrder_id() + ", Cliente " + deliveries.get(i).getUsername());
        }
        adaptador = new ArrayAdapter<>(this, android.R.layout.simple_list_item_multiple_choice, datos);

        List = (ListView) findViewById(R.id.ListaPedidos);
        List.setAdapter(adaptador);
        List.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE);
        List.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> arg0, View vista, int posicion, long arg3) {

            }
        });

        refresh.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                orderquant = datos.size();
                refresh();
                if (datos.size() == 0) {
                    AlertDialog alertDialog = new AlertDialog.Builder(Pedidos.this).create();
                    alertDialog.setTitle("No tiene ordenes pendientes");
                    alertDialog.setMessage("No se le han asignado mas ordenes");
                    alertDialog.setButton(AlertDialog.BUTTON_NEGATIVE, "OK",
                            new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int which) {
                                    dialog.dismiss();
                                }
                            });
                    alertDialog.show();
                } else if (datos.size() == orderquant) {
                    AlertDialog alertDialog = new AlertDialog.Builder(Pedidos.this).create();
                    alertDialog.setTitle("No se le han asignado mas ordenes");
                    alertDialog.setMessage("");
                    alertDialog.setButton(AlertDialog.BUTTON_NEGATIVE, "OK",
                            new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int which) {
                                    dialog.dismiss();
                                }
                            });
                    alertDialog.show();
                }
            }
        });

        ruta.setOnClickListener(new View.OnClickListener() {
        public void onClick(View view) {
            SparseBooleanArray selectedItems = List.getCheckedItemPositions();
            Intent pedidos = new Intent(Pedidos.this, Rutas.class);
            Bundle extras = new Bundle();
            if(selectedItems.size() == 0){
                AlertDialog alertDialog = new AlertDialog.Builder(Pedidos.this).create();
                alertDialog.setTitle("Ningún pedido seleccionado");
                alertDialog.setMessage("Por favor seleccione por lo menos un pedido");
                alertDialog.setButton(AlertDialog.BUTTON_NEGATIVE, "OK",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                            }
                        });
                alertDialog.show();
            }
            else {
                int delcount =0;
                for(int i=0; i<deliveries.size(); i++){
                    if(selectedItems.get(i)){
                        extras.putInt("delivery_id" + delcount, deliveries.get(i).getId());
                        delcount++;
                    }
                }
                extras.putInt("cantidad", delcount);
                extras.putString("usuario", usuario);
                extras.putInt("offset", 0);
                pedidos.putExtras(extras);
                startActivity(pedidos);
            }
        }
    });
        updateCantidad();
    }

    private void updateCantidad()
    {
        textViewCantidad.setText("Pedidos:" +datos.size());
    }

    private String getCurrentDate()
    {
        Calendar c = Calendar.getInstance();
        System.out.println("Current time => " + c.getTime());

        SimpleDateFormat df = new SimpleDateFormat("dd-MM-yyyy");
        String formattedDate = df.format(c.getTime());

        return formattedDate;
    }
    public void setActionBar()
    {
        toolbar = (Toolbar) findViewById(R.id.app_bar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Ordenes");
        getSupportActionBar().setIcon(R.mipmap.moto_ab);

    }
    @Override
    protected void onResume(){
        super.onResume();
        refresh();
        //adaptador.notifyDataSetChanged();
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        switch (item.getItemId()) {
            case R.id.id_logout:
                finishAffinity();
                startActivity(new Intent(this, MainActivity.class));
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void refresh()
    {
        deliveries.clear();
        deliveries = ServerSignal.listDeliveries(usuario);
        datos.clear();
        for (int i = 0; i < deliveries.size(); i++) {
            datos.add("Orden " + deliveries.get(i).getOrder_id() + "\n" + "Cliente: " + deliveries.get(i).getUsername());
        }
        List = (ListView) findViewById(R.id.ListaPedidos);
        List.setAdapter(adaptador);
        List.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE);
        List.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> arg0, View vista, int posicion, long arg3) {

            }
        });
        updateCantidad();
    }
}