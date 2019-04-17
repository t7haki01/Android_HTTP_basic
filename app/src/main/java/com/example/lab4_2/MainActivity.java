package com.example.lab4_2;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.google.android.gms.common.GooglePlayServicesNotAvailableException;
import com.google.android.gms.common.GooglePlayServicesRepairableException;
import com.google.android.gms.security.ProviderInstaller;

import org.json.JSONException;
import org.json.JSONObject;

import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;

import javax.net.ssl.SSLContext;

public class MainActivity extends AppCompatActivity {

    ListView listView = null;
    ArrayList<String> arrayList = new ArrayList<String>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        sslCheck();

        setContentView(R.layout.activity_main);
        listView = findViewById(R.id.stock_view);
        final ArrayAdapter arrayAdapter = new ArrayAdapter(this, android.R.layout.simple_list_item_1, arrayList);
        listView.setAdapter(arrayAdapter);
//        restApi("AAPL", "Apple", true, false);
//        restApi("GOOGL", "Alphabet (Google)", false, false);
//        restApi("FB", "Facebook", false, false);
//        restApi("NOK", "Nokia", false, true);

        restApiInObject("AAPL", "Apple", true, false);
        restApiInObject("GOOGL", "Alphabet (Google)", false, false);
        restApiInObject("FB", "Facebook", false, false);
        restApiInObject("NOK", "Nokia", false, true);

        Collections.sort(arrayList, new Comparator<String>(){
            public int compare(String str1, String str2){
                return str1.compareTo(str2);
            }
        });
    }

    private void sslCheck(){
        /**This is required if android version is low when requesting https request*/
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

    private void restApiInObject(final String stockId, final String name, final boolean isStart, final boolean isEnd){
        String url = "https://financialmodelingprep.com/api/company/price/"+ stockId + "?datatype=json";

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest
                (Request.Method.GET, url, null, new Response.Listener<JSONObject>() {

                    @Override
                    public void onResponse(JSONObject response) {
                        try{
                            if(isStart){
                                String startComment = "!!From here reqeust JsonObject and concat with String";
                                arrayList.add(startComment);
                                callArrayAdater();
                            }
                            String price = response.getJSONObject(stockId).get("price").toString();
                            String result = name + ": " + price + " USD";
                            arrayList.add(result);
                            callArrayAdater();
                            if(isEnd){
                                Collections.sort(arrayList);
                            }
                        }catch (JSONException e){
                            e.getLocalizedMessage();
                        }
                    }
                }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        error.getLocalizedMessage();
                    }
                });
        DemoSingleton.getInstance(this).addToRequestQueue(jsonObjectRequest);
    }

    private void restApi(final String stockId, final String name, final boolean isStart, final boolean isEnd){
        String url = "https://financialmodelingprep.com/api/company/price/"+ stockId;

        StringRequest stringRequest = new StringRequest(Request.Method.GET, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        if(isStart){
                            String startComment = "!From here reqeust string and concat with string";
                            arrayList.add(startComment);
                            callArrayAdater();
                        }
                        int endingBracket = response.indexOf("}", 40);
                        int priceIndex = response.indexOf("price");
                        Log.d("response", "length: " + response.length() + " and index " + response.indexOf("price") + " and text: " + response + " and ending " + endingBracket);
                        String result = name + ": " + response.substring(priceIndex+6, endingBracket-5) + " USD";
                        arrayList.add(result);
                        callArrayAdater();
                        if(isEnd){
                            Collections.sort(arrayList);
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                error.printStackTrace();
            }
        });
        DemoSingleton.getInstance(this).addToRequestQueue(stringRequest);
    }

    public void callArrayAdater(){
        final ArrayAdapter<String> aa;
        aa = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, arrayList);
        listView.setAdapter(aa);
    }
}
