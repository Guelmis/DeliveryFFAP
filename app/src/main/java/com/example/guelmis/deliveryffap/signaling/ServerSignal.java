package com.example.guelmis.deliveryffap.signaling;

import com.example.guelmis.deliveryffap.models.Customer;
import com.example.guelmis.deliveryffap.models.Delivery;
import com.example.guelmis.deliveryffap.models.DeliveryInfo;
import com.example.guelmis.deliveryffap.models.LineItem;
import com.example.guelmis.deliveryffap.models.MultipleDelivery;
import com.example.guelmis.deliveryffap.models.Product;
import com.example.guelmis.deliveryffap.models.Seller;
import com.google.android.gms.common.api.Api;
import com.google.android.gms.maps.model.LatLng;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.ExecutionException;

public class ServerSignal {
    public static final String domain = "http://10.0.0.23:5000/"; //local
    // public static final String domain = "http://ffap-itt-2015.herokuapp.com/"; //web

    public static final String loginURL = domain + "mobile_login/";
    public static final String sellersURL = domain + "seller_query/";
    public static final String commentURL = domain + "seller_query/comment/";
    public static final String productosURL = domain + "product_query/";
    public static final String ordershowURL = domain + "order_api/";
    public static final String tracksendURL = domain + "delivery_track/";
    public static final String trackreceiveURL = domain + "tracking/";
    public static final String deliverylistURL = domain + "delivery_list/";
    public static final String deliveryshowURL = domain + "delivery_display/";
    public static final String deliveryfinishURL = domain + "delivery_finish/";

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

    public static BasicResponse sendLocation(Integer id, LatLng location, String eta){
        ArrayList<NameValuePair> params = new ArrayList<>();
        JSONObject answer = null;
        Double Latitude = location.latitude;
        Double Longitude = location.longitude;
        BasicResponse ret = new BasicResponse(false, "Error al enviar Localizacion", "");

        params.add(new BasicNameValuePair("estimated_time", eta));
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
                JSONArray itemJarr = sellerJarr.getJSONObject(i).getJSONArray("items");

                ArrayList<LineItem> piezas = extractLineItems(itemJarr);
                LatLng location = extractLocation(sellerJarr.getJSONObject(i));

                sellers.add(new Seller(
                        sellerJarr.getJSONObject(i).getJSONObject("seller").getString("id"),
                        sellerJarr.getJSONObject(i).getJSONObject("seller").getString("name"),
                        sellerJarr.getJSONObject(i).getJSONObject("seller").getString("address"),
                        sellerJarr.getJSONObject(i).getJSONObject("seller").getString("phone"),
                        location,
                        piezas
                ));
            }

            LatLng userLocation = extractLocation(answer);

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

    public static MultipleDelivery getSeveralDeliveries(ArrayList<Integer> delivery_ids_input){
        ArrayList<NameValuePair> params = new ArrayList<>();
        ArrayList<JSONObject> answers = new ArrayList<>();
        MultipleDelivery ret = null;

        try {
            for(Integer ID : delivery_ids_input){
                params.add(new BasicNameValuePair("delivery_id", Integer.toString(ID)));
                answers.add(new JObjRequester().post(deliveryshowURL, params));
                params.clear();
            }
        } catch (ExecutionException | InterruptedException e) {
            e.printStackTrace();
            return null;
        }

        try {
            ArrayList<Seller> sellers = new ArrayList<>();
            ArrayList<Customer> customers = new ArrayList<>();

            for (JSONObject deliveryJSON : answers){
                JSONArray sellerJarr = deliveryJSON.getJSONArray("sellers");
                ArrayList<LineItem> customerItems = new ArrayList<>();

                for(int i=0; i<sellerJarr.length(); i++){
                    Seller thisSeller = new Seller(
                            sellerJarr.getJSONObject(i).getJSONObject("seller").getString("id"),
                            sellerJarr.getJSONObject(i).getJSONObject("seller").getString("name"),
                            sellerJarr.getJSONObject(i).getJSONObject("seller").getString("address"),
                            sellerJarr.getJSONObject(i).getJSONObject("seller").getString("phone"),
                            extractLocation(sellerJarr.getJSONObject(i)),
                            extractLineItems(sellerJarr.getJSONObject(i).getJSONArray("items"))
                    );

                    if (sellers.contains(thisSeller)){
                        int index = sellers.indexOf(thisSeller);
                        sellers.get(index).absorbProductList(thisSeller.getProducts());
                    }
                    else{
                        sellers.add(thisSeller);
                    }
                    //Es importante que se extraiga la lista del JSON de nuevo.
                    customerItems.addAll(extractLineItems(sellerJarr.getJSONObject(i).getJSONArray("items")));
                }
                Customer thisCustomer = new Customer(
                        deliveryJSON.getJSONObject("client").getString("username"),
                        deliveryJSON.getInt("id"),
                        deliveryJSON.getJSONObject("order").getInt("id"),
                        extractLocation(deliveryJSON),
                        customerItems);
                if(customers.contains(thisCustomer)){
                    int index = customers.indexOf(thisCustomer);
                    customers.get(index).mergeCustomer(thisCustomer);
                }
                else {
                    customers.add(thisCustomer);
                }
            }

            ret = new MultipleDelivery(customers, sellers);

        } catch (JSONException e) {
            e.printStackTrace();
            return null;
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
            if(answer.getString(KEY_SUCCESS).equals("true")){
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

    private static ArrayList<LineItem> extractLineItems(JSONArray litemsJson) throws JSONException {
        ArrayList<LineItem> ret = new ArrayList<>();

        for(int j=0; j<litemsJson.length(); j++){
            ret.add(new LineItem(new Product(
                    litemsJson.getJSONObject(j).getString("title"),
                    litemsJson.getJSONObject(j).getJSONObject(brand_tag).getString("brand_name"),
                    litemsJson.getJSONObject(j).getJSONObject(model_tag).getString("model_name"),
                    litemsJson.getJSONObject(j).getString(image_tag),
                    litemsJson.getJSONObject(j).getJSONObject(model_tag).getInt(year_tag),
                    litemsJson.getJSONObject(j).getString("id")),
                    litemsJson.getJSONObject(j).getJSONObject("item").getInt("quantity")));
        }
        return ret;
    }

    private static LatLng extractLocation(JSONObject locationJSON) throws JSONException {
        LatLng ret = new LatLng(
                locationJSON.getJSONObject("location").getDouble("latitude"),
                locationJSON.getJSONObject("location").getDouble("longitude"));

        return ret;
    }

}
