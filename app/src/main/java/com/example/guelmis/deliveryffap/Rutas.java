package com.example.guelmis.deliveryffap;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import android.content.DialogInterface;
import android.support.v7.app.AlertDialog;

import com.example.guelmis.deliveryffap.models.Customer;
import com.example.guelmis.deliveryffap.models.MultipleDelivery;
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
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

public class Rutas extends FragmentActivity implements LocationProvider.LocationCallback {
    public static final String TAG = Rutas.class.getSimpleName();
    private LocationProvider mLocationProvider;
    private GoogleMap map;
    private ArrayList<LatLng> ubicacionesTiendas;
    private ArrayList<LatLng> ubicacionesClientes;
    private static LatLng ubicacion = new LatLng(0,0);
    private Polyline newPolyline;
    private LatLngBounds latlngBounds;
    private boolean areaApplied;
    private String usuario;
    private MultipleDelivery fulldeliveryinfo;
    private static Double distTotal;
    private static Double tTotal;
    private int offset;
    private boolean lastRoute;
    Button paquete;
    View view;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.ruta);
        paquete = (Button) findViewById(R.id.btnpackage);
        final Bundle points = getIntent().getExtras();
        usuario = points.getString("usuario");
        fulldeliveryinfo = ServerSignal.getSeveralDeliveries(getDeliveryIDsfromBundle(points));

        if(fulldeliveryinfo == null){
            AlertDialog alertDialog = new AlertDialog.Builder(Rutas.this).create();
            alertDialog.setTitle("Error de cominicacion");
            alertDialog.setMessage("No se pudo comunicar con el servidor, Por favor intentelo mas tarde.");
            alertDialog.setButton(AlertDialog.BUTTON_NEUTRAL, "OK",
                    new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                            finish();
                        }
                    });
            alertDialog.show();
        }

        ubicacionesClientes = fulldeliveryinfo.getAllCustomerLocations();

        //Ordena las posiciones de las tiendas de acuerdo al primer cliente.
        ubicacionesTiendas = byDistance(ubicacionesClientes.get(0), fulldeliveryinfo.getAllSellerLocations());

        offset = points.getInt("offset");

        areaApplied = false;
        lastRoute =false;

        mLocationProvider = new LocationProvider(this, this);
        try
        {
            initializeMap();
            map.setMyLocationEnabled(true);
        }
        catch(Exception e) {
            e.printStackTrace();
        }

        for (int i=offsetCustomers(); i<ubicacionesClientes.size(); i++){
            map.addMarker(new MarkerOptions().position(ubicacionesClientes.get(i))
                    .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_AZURE))
                    .title("Cliente "+ fulldeliveryinfo.getCustomers().get(i).getName()));
        }

        distTotal=0.0;
        tTotal=0.0;
        paquete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(offsetSellers() != -1){
                    restartWithOffset();
                }
                else {
                  //  final Customer cliente = fulldeliveryinfo.getCustomers().get(offsetCustomers());
                    AlertDialog alertDialog = new AlertDialog.Builder(Rutas.this).create();
                    alertDialog.setTitle("Confirmar la entrega de la orden");
                    alertDialog.setMessage("Confirmar que la orden se entrego satisfactoriamente?");
                    alertDialog.setButton(AlertDialog.BUTTON_NEUTRAL, "SI",
                            new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int which) {
                                    dialog.dismiss();
                                    if(offset == ubicacionesTiendas.size() + ubicacionesClientes.size() -1){
                                        BasicResponse answer = finishDeliveries(fulldeliveryinfo.getCustomers().get(0).getDeliveryIDs());
                                        for(int i=1; i< fulldeliveryinfo.getCustomers().size(); i++){
                                            BasicResponse current = finishDeliveries(fulldeliveryinfo.getCustomers().get(i).getDeliveryIDs());
                                            if(!current.success()){
                                                answer = current;
                                            }
                                        }
                                        if(answer.success()){
                                            finish();
                                        }
                                        else {
                                            Toast.makeText(Rutas.this, answer.getMessage(), Toast.LENGTH_LONG).show();
                                        }
                                    }
                                    else {
                                        restartWithOffset();
                                    }
                                }
                            });
                    alertDialog.setButton(AlertDialog.BUTTON_NEGATIVE, "NO",
                            new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int which) {
                                    dialog.dismiss();
                                }
                            });
                    alertDialog.show();
                }
            }
        });
        if(offsetSellers() == -1){
            paquete.setText("Orden Entregada");
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
        PolylineOptions rectLine;
        if(!lastRoute){
            rectLine = new PolylineOptions().width(4).color(Color.BLUE);
        }
        else{
            rectLine = new PolylineOptions().width(8).color(Color.RED);
        }
        for (int i = 0; i < directionPoints.size(); i++) {
            rectLine.add((LatLng) directionPoints.get(i));
        }
        lastRoute = false;
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
    public void findDirections(double fromPositionDoubleLat, double fromPositionDoubleLong,
                               double toPositionDoubleLat, double toPositionDoubleLong, String mode)
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

    private double calcDistancia(LatLng punto1, LatLng punto2){
        float[] res = new float[3];
        Location.distanceBetween(punto1.latitude, punto1.longitude, punto2.latitude, punto2.longitude, res);
        return res[0];
    }

    private ArrayList<LatLng> byDistance(LatLng reference, ArrayList<LatLng> input){
        ArrayList<LatLng> ret = new ArrayList<>();

        ArrayList<Double> distances = new ArrayList<>();
        HashMap<Double, LatLng> map = new HashMap<>();
        for(int i=0; i<input.size(); i++){
            Double dist = calcDistancia(reference, input.get(i));
            distances.add(dist);
            map.put(dist, input.get(i));
        }

        ArrayList<Double> ordered = new ArrayList<>();
        Double mayor;
        int index = 0;

        while(distances.size() > 0){
            mayor = distances.get(distances.size()-1);
            for (int i=0; i<distances.size(); i++){
                if(mayor <= distances.get(i)){
                    mayor = distances.get(i);
                    index = i;
                }
            }
            distances.remove(index);
            ordered.add(mayor);
        }

        for (int i=0; i<ordered.size(); i++){
            ret.add(map.get(ordered.get(i)));
        }

        int i =0;

        return ret;
    }

    public void handleNewLocation(Location location) {
        Log.d(TAG, location.toString());
        //Toast.makeText(Rutas.this, "Moviendo " + currentLatitude + " " + currentLongitude, Toast.LENGTH_LONG).show();
        ubicacion = new LatLng(location.getLatitude(), location.getLongitude());

        map.addMarker(new MarkerOptions().position(ubicacion).title("Ubicacion Actual"));
        BasicResponse confirm = updateLocation(fulldeliveryinfo.getAllDeliveryIDs());
        //Toast.makeText(Rutas.this, distTotal.toString() + " km " + tTotal.toString() + " min", Toast.LENGTH_LONG).show();
        Toast.makeText(Rutas.this, offset + " " +  offsetSellers() + " " + offsetCustomers() + " " + ubicacionesTiendas.size(), Toast.LENGTH_LONG).show();

        if(!areaApplied){
            CrearRuta(ubicacion, ubicacionesTiendas);
            area();
            areaApplied = true;
        }
    }

    private void CrearRuta(LatLng _ubicacion, ArrayList<LatLng> destinos) {
        int laststore = destinos.size()-1;
        if(offsetSellers() != -1){
            lastRoute = true;
            findDirections(_ubicacion.latitude, _ubicacion.longitude, destinos.get(offsetSellers()).latitude,
                    destinos.get(offsetSellers()).longitude,GMapV2Direction.MODE_DRIVING);
            map.addMarker(new MarkerOptions().position(destinos.get(offsetSellers())).title(markerTitle(offsetSellers())));

            for(int i=offsetSellers()+1; i<destinos.size(); i++){
                findDirections(destinos.get(i - 1).latitude, destinos.get(i - 1).longitude, destinos.get(i).latitude,
                        destinos.get(i).longitude, GMapV2Direction.MODE_DRIVING);
                map.addMarker(new MarkerOptions().position(destinos.get(i)).title(markerTitle(i)));
            }

            if(offsetCustomers() == 0){
                findDirections(destinos.get(laststore).latitude, destinos.get(laststore).longitude, ubicacionesClientes.get(0).latitude,
                        ubicacionesClientes.get(0).longitude, GMapV2Direction.MODE_DRIVING);
            }
            for(int i=offsetCustomers()+1; i<ubicacionesClientes.size(); i++){
                findDirections(ubicacionesClientes.get(i-1).latitude, ubicacionesClientes.get(i-1).longitude,
                        ubicacionesClientes.get(i).latitude, ubicacionesClientes.get(i).longitude, GMapV2Direction.MODE_DRIVING);
            }
        }
        else{
            lastRoute = true;
            findDirections(_ubicacion.latitude, _ubicacion.longitude, ubicacionesClientes.get(offsetCustomers()).latitude,
                    ubicacionesClientes.get(offsetCustomers()).longitude, GMapV2Direction.MODE_DRIVING);

            if(ubicacionesClientes.size() > 1){
                for(int i=offsetCustomers()+1; i<ubicacionesClientes.size(); i++){
                    findDirections(ubicacionesClientes.get(i-1).latitude, ubicacionesClientes.get(i-1).longitude,
                            ubicacionesClientes.get(i).latitude, ubicacionesClientes.get(i).longitude, GMapV2Direction.MODE_DRIVING);
                }
            }
        }

    }

    private String markerTitle(int index){
        String ret = "";
        ret += fulldeliveryinfo.getSellers().get(index).getName() + "\n";
        /*for (int i=0; i<fulldeliveryinfo.getSellers().get(index).getProducts().size(); i++){
            ret += fulldeliveryinfo.getSellers().get(index).getProducts().get(i).getTitle();
        }*/
        return ret;
    }

    public static void CalcEstimate(String distance, String duration){
        distTotal += Double.parseDouble(distance);
        tTotal += Double.parseDouble(duration);
    }

    private ArrayList<Integer> getDeliveryIDsfromBundle(Bundle bundle){
        ArrayList<Integer> ids = new ArrayList<>();
        for(int i=0; i<bundle.getInt("cantidad"); i++){
            ids.add(bundle.getInt("delivery_id" + i));
        }
        return ids;
    }

    private int offsetSellers(){
        return offset < ubicacionesTiendas.size() ? offset : -1;
    }

    private int offsetCustomers(){
        if(offset < ubicacionesTiendas.size()){
            return 0;
        }
        return offset < ubicacionesClientes.size() + ubicacionesTiendas.size() ? offset - ubicacionesTiendas.size() : -1;
    }

    private BasicResponse updateLocation(ArrayList<Integer> ids){
        BasicResponse ret;

        ret = ServerSignal.sendLocation(ids.get(0), ubicacion, tTotal.toString());
        if(ret.success()){
            for(int i=1; i<ids.size(); i++){
                BasicResponse temp = ServerSignal.sendLocation(ids.get(i), ubicacion, tTotal.toString());
                if(!temp.success()){
                    ret = temp;
                }
            }
        }


        return ret;
    }

    private BasicResponse finishDeliveries(ArrayList<Integer> ids){
        BasicResponse ret;

        ret = ServerSignal.finishDelivery(Integer.toString(ids.get(0)));
        if(ret.success()){
            for(int i=1; i<ids.size(); i++){
                BasicResponse temp = ServerSignal.finishDelivery(Integer.toString(ids.get(i)));
                if(!temp.success()){
                    ret = temp;
                }
            }
        }
        return ret;
    }

    private void restartWithOffset(){
        Intent myIntent = new Intent(Rutas.this, Rutas.class);
        Bundle extras = new Bundle();
        extras.putString("usuario", usuario);

        ArrayList<Customer> custs = fulldeliveryinfo.getCustomers();
        int idcount = 0;
        int init;
       /* if (offsetSellers() == -1){
            init = offsetCustomers()+1;
            extras.putInt("offset", offset);
        }
        else{
        }*/
        //init =offsetCustomers();
        extras.putInt("offset", offset + 1);
        for(int i = 0; i<custs.size(); i++){
            for(Integer delid :custs.get(i).getDeliveryIDs()){
                extras.putInt("delivery_id" + idcount, delid);
                idcount++;
            }
        }
        extras.putInt("cantidad", idcount);

        myIntent.putExtras(extras);
        finish();
        startActivity(myIntent);
    }

    /*
    ArrayList<Integer> ids = fulldeliveryinfo.getAllDeliveryIDs();
        extras.putInt("cantidad", ids.size());
        for(int i=0; i<ids.size(); i++){
            extras.putInt("delivery_id" + i, ids.get(i));
        }


    */
}
