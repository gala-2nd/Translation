package com.yangxu.translation;

import androidx.appcompat.app.AppCompatActivity;
import com.androidnetworking.AndroidNetworking;
import com.androidnetworking.error.ANError;
import com.androidnetworking.interfaces.JSONArrayRequestListener;
import com.androidnetworking.interfaces.JSONObjectRequestListener;
import com.androidnetworking.interfaces.OkHttpResponseListener;
import com.google.gson.JsonIOException;


import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;

import MyClasses.SharedHelper;
import okhttp3.Response;


public class Configuration extends AppCompatActivity implements View.OnClickListener{
    public static String auth_key=null;
    public static String url="https://api-free.deepl.com/v2/";
    TextView tv_auth_key;
    EditText et_auth_key;
    Button btn_save,btn_add_key;
    TextView tv_usage_rate;
    SharedHelper sh;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_configuration);

        sh = new SharedHelper(Configuration.this);

        tv_auth_key=(TextView) findViewById(R.id.tv_auth_key);
        et_auth_key=(EditText) findViewById(R.id.et_auth_key);
        btn_save=(Button)findViewById(R.id.btn_save);
        tv_usage_rate=(TextView)findViewById(R.id.tv_usage_rate);
        btn_add_key=(Button)findViewById(R.id.btn_add_key);

        btn_save.setOnClickListener(this);
        btn_add_key.setOnClickListener(this);

        AndroidNetworking.initialize(this);

    }
    @Override
    protected void onStart() {
        super.onStart();
        HashMap<String,String> data = sh.read();
        auth_key=data.get("auth_key");
        if(auth_key!=null){
            et_auth_key.setText(auth_key);
            GetUrl();
            FindUsage();
        }
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        menu.add(1,MainActivity.HOME,1,"Home");
        menu.add(1,MainActivity.CONFIG,2,"Configuration");
        menu.add(1,MainActivity.HISTORY,3,"History");
        return true;
    }
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        Context context=this;
        switch (id){
            case MainActivity.HOME:
                if(auth_key==null||auth_key.trim().equals("")){
                    Toast.makeText(Configuration.this,"Please enter your correct auth key of deepl or try again",Toast.LENGTH_SHORT).show();
                    break;
                }
                Intent intentMain=new Intent(context,MainActivity.class);
                startActivity(intentMain);
                break;
            case MainActivity.CONFIG:

                break;
            case MainActivity.HISTORY:
                Intent intentHis=new Intent(context,History.class);
                startActivity(intentHis);
                break;
        }
        return super.onOptionsItemSelected(item);
    }
    public void GetUrl() {
        AndroidNetworking.post("https://api-free.deepl.com/v2/" + "usage")
                .addHeaders("Content-Type", "application/x-www-form-urlencoded")
                .addBodyParameter("auth_key", auth_key)
                .build()
                .getAsOkHttpResponse(new OkHttpResponseListener() {
                    @Override
                    public void onResponse(Response response) {
                        switch (response.code()) {
                            case 200:
                                url="https://api-free.deepl.com/v2/";
                                break;
                            default:
                                url = "https://api.deepl.com/v2/";
                                break;
                        }
                    }
                    @Override
                    public void onError(ANError anError) {
                        Toast.makeText(Configuration.this, anError.toString(), Toast.LENGTH_SHORT).show();
                    }
                });


    }
    public void FindUsage()
    {
        AndroidNetworking.post(url+"usage")
                .addHeaders("Content-Type","application/x-www-form-urlencoded")
                .addBodyParameter("auth_key",auth_key)
                .build()
                .getAsJSONObject(new JSONObjectRequestListener() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try{

                            String count=response.getString("character_count");
                            String limit=response.getString("character_limit");
                            tv_usage_rate.setText(count+"/"+limit);
                        }catch (JsonIOException | JSONException e){
                            auth_key=null;
                            Toast.makeText(Configuration.this,e.getMessage(),Toast.LENGTH_SHORT).show();
                        }
                    }
                    @Override
                    public void onError(ANError anError) {
                        auth_key=null;
                        tv_usage_rate.setText("null/null");
                        Toast.makeText(Configuration.this,"Please enter your correct auth key of deepl or try again",Toast.LENGTH_SHORT).show();
                    }
        });
    }

    @Override
    public void onClick(View view){
        switch(view.getId()){
            case R.id.btn_save:
                auth_key=et_auth_key.getText().toString();
                if(auth_key==null||auth_key.trim().equals("")){
                    sh.save(auth_key);
                    tv_usage_rate.setText("null/null");
                    Toast.makeText(Configuration.this,"Please enter your correct auth key of deepl or try again",Toast.LENGTH_SHORT).show();
                }else{
                    sh.save(auth_key);
                    GetUrl();
                    FindUsage();
                }
            break;
            case R.id.btn_add_key:
                et_auth_key.setText("5a99eeed-cced-14ef-2260-9b7652459634:fx");
                break;
        }
    }
}