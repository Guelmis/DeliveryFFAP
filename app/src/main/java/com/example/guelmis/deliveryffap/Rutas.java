package com.example.guelmis.deliveryffap;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import android.view.View.OnClickListener;
import com.example.guelmis.deliveryffap.signaling.BasicResponse;
import com.example.guelmis.deliveryffap.signaling.ServerSignal;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;

import android.content.Intent;
import android.graphics.Color;
import android.location.Location;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.View;
import android.widget.AbsoluteLayout;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import org.w3c.dom.Document;

public class Rutas extends FragmentActivity implements LocationProvider.LocationCallback {
    public static final String TAG = Rutas.class.getSimpleName();
    private LocationProvider mLocationProvider;
    private GoogleMap map;
    private ArrayList<LatLng> tiendas;
    private LatLng cliente1;
    private LatLng cliente2;
    private static LatLng ubicacion = new LatLng(0,0);
    private Polyline newPolyline;
    private LatLngBounds latlngBounds;
    private boolean areaApplied;
    private Integer deliveryID;
    private static Double distTotal;
    private static Double tTotal;
    Button paquete;
    View view;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.ruta);
        paquete = (Button) findViewById(R.id.btnpackage);
        cliente1 = new LatLng(18.482233, -69.912613);
        cliente2 = new LatLng(18.483255, -69.939677);
        tiendas = new ArrayList<>();

        final Bundle points = getIntent().getExtras();

        for(int i=0; points.getDouble("Lat"+Integer.toString(i+1)) != 0.0; i++){
            tiendas.add(new LatLng(points.getDouble("Lat"+Integer.toString(i+1)), points.getDouble("Long"+Integer.toString(i+1))));
        }

        areaApplied = false;
        //deliveryID = ServerSignal.getDeliveryID();
        mLocationProvider = new LocationProvider(this, this);
        try
        {
            initializeMap();
            map.setMyLocationEnabled(true);
        }
        catch(Exception e) {
            e.printStackTrace();
        }
        for(int i=0; i<tiendas.size(); i++) {
            map.addMarker(new MarkerOptions().position(tiendas.get(i)));
        }

        map.addMarker(new MarkerOptions().position(cliente1)
                .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_AZURE)));
        map.addMarker(new MarkerOptions().position(cliente2)
                .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_AZURE)));

        distTotal=0.0;
        tTotal=0.0;
        paquete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent myIntent = new Intent(Rutas.this, Rutas.class);
                Bundle newpoints = new Bundle();
                for(int i=0; points.getDouble("Lat"+Integer.toString(i+2)) != 0.0; i++){
                    //tiendas.add(new LatLng(points.getDouble("Lat"+Integer.toString(i+1)), points.getDouble("Long"+Integer.toString(i+1))));
                    newpoints.putDouble("Lat"+Integer.toString(i+1), points.getDouble("Lat"+Integer.toString(i+2)));
                    newpoints.putDouble("Long"+Integer.toString(i+1), points.getDouble("Long"+Integer.toString(i+2)));
                }
                myIntent.putExtras(newpoints);
                finish();
                startActivity(myIntent);
            }
        });
        paquete.setText("Orden Entregada");
    }

    @Override
    protected void onResume() {
        super.onResume();
        initializeMap();
        mLocationProvider.connect();
    }

    @Override
    protected void onPause() {
        super.onPause();
       // mLocationProvider.connect();
        mLocationProvider.disconnect();
    }
    public void handleGetDirectionsResult(ArrayList directionPoints) {
        Polyline newPolyline;
        GoogleMap mMap = ((SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map)).getMap();
        PolylineOptions rectLine = new PolylineOptions().width(8).color(Color.BLUE);
        for (int i = 0; i < directionPoints.size(); i++) {
            rectLine.add((LatLng) directionPoints.get(i));
        }
        newPolyline = mMap.addPolyline(rectLine);
    }

    private LatLngBounds createLatLngBoundsObject(LatLng firstLocation, LatLng secondLocation){
        if (firstLocation != null && secondLocation != null)
        {
            LatLngBounds.Builder builder = new LatLngBounds.Builder();
            builder.include(firstLocation).include(secondLocation);

            return builder.build();
        }
        return null;
    }
    public void findDirections(double fromPositionDoubleLat, double fromPositionDoubleLong, double toPositionDoubleLat, double toPositionDoubleLong, String mode)
    {
        Map<String, String> map = new HashMap<String, String>();
        map.put(GetDirectionsAsyncTask.USER_CURRENT_LAT, String.valueOf(fromPositionDoubleLat));
        map.put(GetDirectionsAsyncTask.USER_CURRENT_LONG, String.valueOf(fromPositionDoubleLong));
        map.put(GetDirectionsAsyncTask.DESTINATION_LAT, String.valueOf(toPositionDoubleLat));
        map.put(GetDirectionsAsyncTask.DESTINATION_LONG, String.valueOf(toPositionDoubleLong));
        map.put(GetDirectionsAsyncTask.DIRECTIONS_MODE, mode);

        GetDirectionsAsyncTask asyncTask = new GetDirectionsAsyncTask(this);
        asyncTask.execute(map);
    }

    private void initializeMap() {
        if (map == null) {
            map = ((SupportMapFragment) getSupportFragmentManager().findFragmentById(
                    R.id.map)).getMap();
            // check if map is created successfully or not
            if (map == null) {
                Toast.makeText(getApplicationContext(),
                        "Ha ocurrido un error, no se pudo cargar su ruta", Toast.LENGTH_LONG)
                        .show();
            }
        }
    }

    public void area(){
         map.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(ubicacion.latitude, ubicacion.longitude), 13f));
    }

    public void handleNewLocation(Location location) {
        Log.d(TAG, location.toString());
        //Toast.makeText(Rutas.this, "Moviendo " + currentLatitude + " " + currentLongitude, Toast.LENGTH_LONG).show();
        ubicacion = new LatLng(location.getLatitude(), location.getLongitude());

        map.addMarker(new MarkerOptions().position(ubicacion).title("Ubicacion Actual"));
        //BasicResponse confirm = ServerSignal.sendLocation(deliveryID, ubicacion);
        Toast.makeText(Rutas.this, distTotal.toString() + " km " + tTotal.toString() + " min", Toast.LENGTH_LONG).show();

        if(!areaApplied){
            CrearRuta(ubicacion, tiendas);
            area();
            areaApplied = true;
        }
    }

    private void CrearRuta(LatLng _ubicacion, ArrayList<LatLng> destinos) {
        findDirections(_ubicacion.latitude, _ubicacion.longitude, destinos.get(0).latitude, destinos.get(0).longitude,
                GMapV2Direction.MODE_DRIVING);
        int laststore = 0;
        for(int i=1; i<destinos.size(); i++){
            findDirections(destinos.get(i-1).latitude, destinos.get(i-1).longitude, destinos.get(i).latitude, destinos.get(i).longitude,
                    GMapV2Direction.MODE_DRIVING);
            laststore = i;
        }

        findDirections(destinos.get(laststore).latitude, destinos.get(laststore).longitude, cliente1.latitude, cliente1.longitude,
                GMapV2Direction.MODE_DRIVING);
        findDirections(cliente1.latitude, cliente1.longitude, cliente2.latitude, cliente2.longitude,
                GMapV2Direction.MODE_DRIVING);
    }

    public static void CalcEstimate(String distance, String duration){
        distTotal += Double.parseDouble(distance);
        tTotal += Double.parseDouble(duration);
    }
}
