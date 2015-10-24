package com.example.guelmis.deliveryffap.signaling;

import com.example.guelmis.deliveryffap.models.Delivery;
import com.example.guelmis.deliveryffap.models.DeliveryInfo;
import com.example.guelmis.deliveryffap.models.LineItem;
import com.example.guelmis.deliveryffap.models.Product;
import com.example.guelmis.deliveryffap.models.Seller;
import com.google.android.gms.maps.model.LatLng;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.ExecutionException;

/**
 * Created by mario on 09/22/15.
 */
public class ServerSignal {

    /*
    public static final String loginURL = "http://10.0.0.21:3000/mobile_login/";
    public static final String searchURL = "http://10.0.0.21:3000/product_query/search/";
    public static final String spinnersURL = "http://10.0.0.21:3000/info_query/";
    public static final String sellersURL = "http://10.0.0.21:3000/seller_query/";
    public static final String productosURL = "http://10.0.0.21:3000/product_query/";
    public static final String cartshowURL = "http://10.0.0.21:3000/cart_query/";
    public static final String cartaddURL = "http://10.0.0.21:3000/cart_add/";
    public static final String cartremoveURL = "http://10.0.0.21:3000/cart_remove/";
    public static final String cartdestroyURL = "http://10.0.0.21:3000/cart_destroy/";

 //   public static final String trackidURL = "http://10.0.0.10:5000/tracking/get_id/";
    public static final String tracksendURL = "http://10.0.0.10:5000/delivery_track/";
    public static final String trackreceiveURL = "http://10.0.0.10:5000/tracking/";
    public static final String deliverylistURL = "http://10.0.0.10:5000/delivery_list/";
    public static final String deliveryshowURL = "http://10.0.0.10:5000/delivery_display/";
    public static final String deliveryfinishURL = "http://10.0.0.10:5000/delivery_finish/";
*/
    public static final String loginURL = "http://ffap-itt-2015.herokuapp.com/mobile_login/";
    public static final String tracksendURL = "http://ffap-itt-2015.herokuapp.com/delivery_track/";
    public static final String trackreceiveURL = "http://ffap-itt-2015.herokuapp.com/tracking/";
    public static final String deliverylistURL = "http://ffap-itt-2015.herokuapp.com/delivery_list/";
    public static final String deliveryshowURL = "http://ffap-itt-2015.herokuapp.com/delivery_display/";
    public static final String deliveryfinishURL = "http://ffap-itt-2015.herokuapp.com/delivery_finish/";

    public static final String searchURL = "http://ffap-itt-2015.herokuapp.com/product_query/search/";
    public static final String spinnersURL = "http://ffap-itt-2015.herokuapp.com/info_query/";
    public static final String sellersURL = "http://ffap-itt-2015.herokuapp.com/seller_query/";
    public static final String commentURL = "http://ffap-itt-2015.herokuapp.com/seller_query/comment/";
    public static final String productosURL = "http://ffap-itt-2015.herokuapp.com/product_query/";
    public static final String cartshowURL = "http://ffap-itt-2015.herokuapp.com/cart_query/";
    public static final String cartaddURL = "http://ffap-itt-2015.herokuapp.com/cart_add/";
    public static final String cartremoveURL = "http://ffap-itt-2015.herokuapp.com/cart_remove/";
    public static final String cartdestroyURL = "http://ffap-itt-2015.herokuapp.com/cart_destroy/";

    public static final String product_tag = "product";
    public static final String image_tag = "image_url";
    public static final String year_tag = "year";
    public static final String model_tag = "model";
    public static final String brand_tag = "brand";
    public static final String query_string_tag = "qstring";
    public static final String login_tag = "login";
    public static final String register_tag = "register";
    public static final String brand_model_map_tag = "brand_model";
    public static final String username_tag = "username";
    public static final String password_tag = "password";

    public static final String KEY_SUCCESS = "success";
    public static final String KEY_MESSAGE = "message";
    public static final String KEY_STATUS = "status";

    public static BasicResponse Login(String username, String password){
        ArrayList<NameValuePair> params = new ArrayList<>();
        JSONObject answer = null;
        BasicResponse ret = new BasicResponse(false, "Error no identificado al autenticarse.", "");

        params.add(new BasicNameValuePair(username_tag, username));
        params.add(new BasicNameValuePair(password_tag, password));

        try {
            answer = new JObjRequester().post(loginURL, params);
            if(answer.getString(KEY_SUCCESS).equals("true")) {
                ret = new BasicResponse(true, answer.getString(KEY_MESSAGE), "");
            }
            else {
                ret = new BasicResponse(false, answer.getString(KEY_MESSAGE), "");
            }
        } catch (ExecutionException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return ret;
    }

    /*public static Integer getDeliveryID(){
        ArrayList<NameValuePair> params = new ArrayList<>();
        JSONObject answer = null;
        Integer id = -1;

        params.add(new BasicNameValuePair("id", id.toString()));

        try {
            answer = new JObjRequester().post(trackidURL, params);
            id = Integer.parseInt(answer.getString("id"));
        } catch (ExecutionException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return id;
    }*/
    public static LatLng getLocation(Integer id){
        ArrayList<NameValuePair> params = new ArrayList<>();
        JSONObject answer = null;
        Double Latitude = 0.00;
        Double Longitude = 0.00;

        try {
            answer = new JObjRequester().get(trackreceiveURL + id.toString(), params);
            Latitude = Double.valueOf(answer.getString("lat"));
            Longitude = Double.valueOf(answer.getString("long"));
        } catch (ExecutionException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (JSONException e) {
            e.printStackTrace();
        }

        LatLng ret = new LatLng(Latitude, Longitude);
        return ret;
    }

    public static BasicResponse sendLocation(Integer id, LatLng location){
        ArrayList<NameValuePair> params = new ArrayList<>();
        JSONObject answer = null;
        Double Latitude = location.latitude;
        Double Longitude = location.longitude;
        BasicResponse ret = new BasicResponse(false, "Error al enviar Localizacion", "");

        params.add(new BasicNameValuePair("latitude", Latitude.toString()));
        params.add(new BasicNameValuePair("longitude", Longitude.toString()));
        params.add(new BasicNameValuePair("delivery_id", id.toString()));

        try {
            answer = new JObjRequester().post(tracksendURL, params);
            if(answer.getString(KEY_SUCCESS).equals("true")){
                ret = new BasicResponse(true, answer.getString(KEY_MESSAGE), "");
            }
        } catch (ExecutionException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return ret;
    }

    public static ArrayList<DeliveryInfo> listDeliveries(String username){
        ArrayList<NameValuePair> params = new ArrayList<>();
        JSONArray answer = null;
        ArrayList<DeliveryInfo> ret = null;

        params.add(new BasicNameValuePair(username_tag, username));

        try {
            answer = new JArrRequester().post(deliverylistURL, params);
            ret = new ArrayList<>();
            for(int i=0; i<answer.length(); i++){
                ret.add(new DeliveryInfo(Integer.parseInt(answer.getJSONObject(i).getString("id")),
                        Integer.parseInt(answer.getJSONObject(i).getJSONObject("order").getString("id")),
                        answer.getJSONObject(i).getJSONObject("client").getString(username_tag)));
            }
        } catch (ExecutionException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return ret;
    }

    public static Delivery getFullDelivery(String delivery_id){
        ArrayList<NameValuePair> params = new ArrayList<>();
        JSONObject answer = null;
        Delivery ret = null;

        params.add(new BasicNameValuePair("delivery_id", delivery_id));

        try {
            answer = new JObjRequester().post(deliveryshowURL, params);
            ArrayList<Seller> sellers = new ArrayList<>();

            JSONArray sellerJarr = answer.getJSONArray("sellers");

            for(int i=0; i<sellerJarr.length(); i++){
                ArrayList<LineItem> piezas = new ArrayList<>();
                JSONArray itemJarr = sellerJarr.getJSONObject(i).getJSONArray("items");
                for(int j=0; j<itemJarr.length(); j++){
                    piezas.add(new LineItem(new Product(
                            itemJarr.getJSONObject(j).getString("title"),
                            itemJarr.getJSONObject(j).getJSONObject("brand").getString("brand_name"),
                            itemJarr.getJSONObject(j).getJSONObject("model").getString("model_name"),
                            itemJarr.getJSONObject(j).getString("image_url"),
                            itemJarr.getJSONObject(j).getJSONObject("model").getInt("year"),
                            itemJarr.getJSONObject(j).getString("id")),
                            itemJarr.getJSONObject(j).getJSONObject("item").getInt("quantity")));
                }
                LatLng location = new LatLng(
                        sellerJarr.getJSONObject(i).getJSONObject("location").getDouble("latitude"),
                        sellerJarr.getJSONObject(i).getJSONObject("location").getDouble("longitude"));
                sellers.add(new Seller(
                        sellerJarr.getJSONObject(i).getJSONObject("seller").getString("id"),
                        sellerJarr.getJSONObject(i).getJSONObject("seller").getString("name"),
                        sellerJarr.getJSONObject(i).getJSONObject("seller").getString("address"),
                        sellerJarr.getJSONObject(i).getJSONObject("seller").getString("phone"),
                        location,
                        piezas
                ));
            }

            LatLng userLocation = new LatLng(
                    answer.getJSONObject("location").getDouble("latitude"),
                    answer.getJSONObject("location").getDouble("longitude"));

            ret = new Delivery(
                    answer.getInt("id"),
                    answer.getJSONObject("order").getInt("id"),
                    answer.getJSONObject("client").getString("username"),
                    userLocation,
                    sellers
            );
        } catch (ExecutionException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return ret;
    }

    public static BasicResponse finishDelivery(String delivery_id){
        ArrayList<NameValuePair> params = new ArrayList<>();
        JSONObject answer = null;
        BasicResponse ret = null;

        params.add(new BasicNameValuePair("delivery_id", delivery_id));

        try {
            answer = new JObjRequester().post(deliveryfinishURL, params);
            if(answer.getString("success").equals("true")){
                ret = new BasicResponse(true, answer.getString(KEY_MESSAGE), "");
            }
            else {
                ret = new BasicResponse(false, answer.getString(KEY_MESSAGE), "");
            }
        } catch (ExecutionException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return ret;
    }

}
