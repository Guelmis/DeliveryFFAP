package com.example.guelmis.deliveryffap;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.support.v7.app.ActionBarActivity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;


public class MainActivity extends ActionBarActivity {
    Button login;
    EditText email;
    EditText password;
    private static String KEY_SUCCESS = "success";
    private static String KEY_MESSAGE = "message";

    /*
    Manejar esta excepcion desde el JSON Parser para indicar que no se pudo conectar a la pagina.
    * HttpHostConnectException
    *
    * */

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        email = (EditText) findViewById(R.id.editTextEmail);
        password = (EditText) findViewById(R.id.editTextEmail);
        login = (Button) findViewById(R.id.botonlogin);
        login.setOnClickListener(new View.OnClickListener() {

            public void onClick(View view) {
                AlertDialog alertDialog = new AlertDialog.Builder(MainActivity.this).create();
                if ((!email.getText().toString().equals("")) && (!password.getText().toString().equals(""))) {
                    NetAsync(view);
                } else if ((!email.getText().toString().equals(""))) {
                    alertDialog.setTitle("No se pudo iniciar sesion");
                    alertDialog.setMessage("El campo de la contrase�a est� vac�o, Por favor introduzca su contrase�a");
                    alertDialog.setButton(AlertDialog.BUTTON_NEUTRAL, "OK",
                            new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int which) {
                                    dialog.dismiss();
                                }
                            });
                    alertDialog.show();

                } else if ((!password.getText().toString().equals(""))) {
                    alertDialog.setTitle("No se pudo iniciar sesi�n");
                    alertDialog.setMessage("El campo de usuario est� vac�o, Por favor introduzca su nombre de usuario");
                    alertDialog.setButton(AlertDialog.BUTTON_NEUTRAL, "OK",
                            new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int which) {
                                    dialog.dismiss();
                                }
                            });
                    alertDialog.show();
                } else {
                    alertDialog.setTitle("No se pudo iniciar sesi�n");
                    alertDialog.setMessage("Campo de usuario y contrase�a vac�os, Por favor introduzca su nombre de usuario y contrase�a");
                    alertDialog.setButton(AlertDialog.BUTTON_NEUTRAL, "OK",
                            new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int which) {
                                    dialog.dismiss();
                                }
                            });
                    alertDialog.show();
                }
            }
        });
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
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private class NetCheck extends AsyncTask<String, String, Boolean> {
        private ProgressDialog nDialog;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            nDialog = new ProgressDialog(MainActivity.this);
            nDialog.setTitle("Verificando la conexi�n a Internet");
            nDialog.setMessage("Por favor, espere..");
            nDialog.setIndeterminate(false);
            nDialog.setCancelable(true);
            nDialog.show();
        }

        @Override
        protected Boolean doInBackground(String... args) {

            ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkInfo netInfo = cm.getActiveNetworkInfo();
            if (netInfo != null && netInfo.isConnected()) {
                try {
                    URL url = new URL("http://www.google.com");
                    HttpURLConnection urlc = (HttpURLConnection) url.openConnection();
                    urlc.setConnectTimeout(3000);
                    urlc.connect();
                    if (urlc.getResponseCode() == 200) {
                        return true;
                    }
                } catch (MalformedURLException e1) {
                    // TODO Auto-generated catch block
                    e1.printStackTrace();
                } catch (IOException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
            }
            return false;

        }

        @Override
        protected void onPostExecute(Boolean th) {

            if (th == true) {
                nDialog.dismiss();
                new ProcessLogin().execute();
            } else {
                nDialog.dismiss();
                AlertDialog alertDialog = new AlertDialog.Builder(MainActivity.this).create();
                alertDialog.setTitle("No se pudo iniciar sesi�n");
                alertDialog.setMessage("Error al conectarse a la red, Por favor verifique su conexi�n a Internet");
                alertDialog.setButton(AlertDialog.BUTTON_NEUTRAL, "OK",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                            }
                        });
                alertDialog.show();
            }
        }
    }

    private class ProcessLogin extends AsyncTask<String, String, JSONObject> {


        private ProgressDialog pDialog;

        String correo, contrasena;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();

            email = (EditText) findViewById(R.id.editTextEmail);
            password = (EditText) findViewById(R.id.editTextPass);
            correo = email.getText().toString();
            contrasena = password.getText().toString();
            pDialog = new ProgressDialog(MainActivity.this);
            pDialog.setTitle("Comunicandose con el servidor");
            pDialog.setMessage("Autenticando usuario");
            pDialog.setIndeterminate(false);
            pDialog.setCancelable(true);
            pDialog.show();
        }

        @Override
        protected JSONObject doInBackground(String... args) {

            UserFunction userFunction = new UserFunction();
            JSONObject json = userFunction.loginUser(correo, contrasena);
            return json;
        }

        @Override
        protected void onPostExecute(JSONObject json) {
            try {
                if (json.getString(KEY_SUCCESS) != null) {

                    String res = json.getString(KEY_SUCCESS);

                    if (res.equals("true")) {

                        pDialog.dismiss();
                        Intent intent = new Intent(MainActivity.this, Pedidos.class);
                        intent.putExtra("usuario", correo);
                        startActivity(intent);

                    } else {

                        pDialog.dismiss();
                        AlertDialog alertDialog = new AlertDialog.Builder(MainActivity.this).create();
                        alertDialog.setTitle("No se pudo iniciar sesi�n");
                        alertDialog.setMessage("Nombre de usuario y/o contrase�a incorrectos, Por favor intentelo de nuevo");
                        alertDialog.setButton(AlertDialog.BUTTON_NEUTRAL, "OK",
                                new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int which) {
                                        dialog.dismiss();
                                    }
                                });
                        alertDialog.show();
                    }
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }

    public void NetAsync(View view) {
        new NetCheck().execute();
    }
}
