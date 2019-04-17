package com.example.lab4_3;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.google.android.gms.common.GooglePlayServicesNotAvailableException;
import com.google.android.gms.common.GooglePlayServicesRepairableException;
import com.google.android.gms.security.ProviderInstaller;

import org.json.JSONException;
import org.json.JSONObject;

import java.lang.reflect.Array;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;

import javax.net.ssl.SSLContext;

public class MainActivity extends AppCompatActivity {

    EditText nameInput = null;
    EditText idInput = null;
    Button addBtn = null;
    String textToput = "";
    ListView listview = null;
    ArrayList arrayList = new ArrayList<String>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        sslCheck();

        nameInput = findViewById(R.id.name);
        idInput = findViewById(R.id.id);
        addBtn = findViewById(R.id.addBtn);
        listview = findViewById(R.id.listView);

        restApi("AAPL", "Apple");
        restApi("GOOGL", "Alphabet (Google)");
        restApi("FB", "Facebook");
        restApi("NOK", "Nokia");

        addBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addBtnClicked();
            }
        });
    }

    void addBtnClicked(){
        String name = nameInput.getText().toString();
        String id = idInput.getText().toString();
        restApi(id, name);
    }

    private void restApi(final String id, final String name){
        String url = "https://financialmodelingprep.com/api/company/price/" + id + "?datatype=json";

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest
                (Request.Method.GET, url, null, new Response.Listener<JSONObject>() {

                    @Override
                    public void onResponse(JSONObject response) {
                        try{
                         String price = response.getJSONObject(id).get("price") + "";
                         textToput = name + ": " + price + " USD";
                         arrayList.add(textToput);
                         callArrayAdapter();
                        }catch(JSONException e){
                            e.printStackTrace();
                        }

                    }
                }, new Response.ErrorListener() {

                    @Override
                    public void onErrorResponse(VolleyError error) {
                        // TODO: Handle error

                    }
                });

// Access the RequestQueue through your singleton class.
        DemoSingleton.getInstance(this).addToRequestQueue(jsonObjectRequest);
    }

    private void sslCheck(){
        try {
            ProviderInstaller.installIfNeeded(getApplicationContext());
            SSLContext sslContext;
            sslContext = SSLContext.getInstance("TLSv1.2");
            sslContext.init(null, null, null);
            sslContext.createSSLEngine();
        } catch (GooglePlayServicesRepairableException | GooglePlayServicesNotAvailableException
                | NoSuchAlgorithmException | KeyManagementException e) {
            e.printStackTrace();
        }
    }

    void callArrayAdapter(){
        final ArrayAdapter<String> arrayAdapter;
        arrayAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, arrayList);
        listview.setAdapter(arrayAdapter);
    }

}
