package com.example.guelmis.deliveryffap;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import com.example.guelmis.deliveryffap.signaling.BasicResponse;
import com.example.guelmis.deliveryffap.signaling.ServerSignal;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;
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
import android.widget.TextView;
import android.widget.Toast;

public class Rutas extends FragmentActivity implements LocationProvider.LocationCallback{
    double Lat1,Lat2,Long1,Long2;
    public static final String TAG = Rutas.class.getSimpleName();
    private LocationProvider mLocationProvider;
    private GoogleMap map;
    private LatLng destino;
    private static LatLng ubicacion = new LatLng(0,0);
    private double currentLatitude,currentLongitude;
    private Polyline newPolyline;
    private LatLngBounds latlngBounds;
    private boolean areaApplied;
    private Integer deliveryID;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        areaApplied = false;
        deliveryID = ServerSignal.getDeliveryID();

        setContentView(R.layout.ruta);
        mLocationProvider = new LocationProvider(this, this);
        try
        {
            initializeMap();
            map.setMyLocationEnabled(true);
        }
        catch(Exception e) {
            e.printStackTrace();
        }

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
        currentLatitude = location.getLatitude();
        currentLongitude = location.getLongitude();
        Toast.makeText(Rutas.this, "Moviendo " + currentLatitude + " " + currentLongitude, Toast.LENGTH_LONG).show();
        Bundle point1 = getIntent().getExtras();
        Lat1=point1.getDouble("Lat");
        Long1=point1.getDouble("Long");
        ubicacion = new LatLng(currentLatitude, currentLongitude);
        destino = new LatLng(Lat1,Long1);
        map.addMarker(new MarkerOptions().position(ubicacion).title("Ubicacion Actual"));
        map.addMarker(new MarkerOptions().position(destino));
        BasicResponse confirm = ServerSignal.sendLocation(deliveryID, ubicacion);
        if(!areaApplied){
            CrearRuta();
            area();
            areaApplied = true;
        }
    }
    public void CrearRuta() {

        findDirections(ubicacion.latitude, ubicacion.longitude, Lat1, Long1, GMapV2Direction.MODE_DRIVING);

    }
}
