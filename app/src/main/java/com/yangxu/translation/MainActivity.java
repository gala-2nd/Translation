package com.yangxu.translation;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.androidnetworking.AndroidNetworking;
import com.androidnetworking.error.ANError;
import com.androidnetworking.interfaces.JSONArrayRequestListener;
import com.androidnetworking.interfaces.JSONObjectRequestListener;
import com.androidnetworking.interfaces.OkHttpResponseListener;
import com.google.gson.JsonIOException;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.lang.reflect.Array;
import java.lang.reflect.Type;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

import MyClasses.MyDbHelper;
import MyClasses.SharedHelper;
import okhttp3.Response;

public class MainActivity extends AppCompatActivity implements View.OnClickListener, AdapterView.OnItemSelectedListener {
    EditText et_source,et_translated;
    Spinner spn_target;
    TextView tv_detected_lang;
    Button btn_translate;
    HashMap<String,String> languages_name_lang,languages_lang_name;
    String target_lang;

    MyDbHelper myDBHelper;
    SQLiteDatabase db;

    public SharedHelper sh;


    public final static int HOME=110;
    public final static int CONFIG = 111;
    public final static int HISTORY=112;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        sh = new SharedHelper(MainActivity.this);

        languages_name_lang= new HashMap<String ,String>();
        languages_lang_name=new HashMap<String,String>();
        myDBHelper = new MyDbHelper(MainActivity.this, "my.db", null, 1);
        db = myDBHelper.getWritableDatabase();


        et_source=(EditText) findViewById(R.id.et_source);
        spn_target=(Spinner) findViewById(R.id.spn_target);
        tv_detected_lang=(TextView) findViewById(R.id.tv_detected_lang);
        btn_translate=(Button) findViewById(R.id.btn_translate);
        et_translated=(EditText) findViewById(R.id.et_translated);

    }
    @Override
    protected void onStart() {
        super.onStart();
        HashMap<String,String> data = sh.read();
        Configuration.auth_key=data.get("auth_key");
        if(Configuration.auth_key==null||Configuration.auth_key.trim().equals("")){
            Intent intent=new Intent(MainActivity.this,Configuration.class);
            startActivity(intent);
        }else {
            btn_translate.setOnClickListener(this);
            spn_target.setOnItemSelectedListener(this);
            et_translated.setKeyListener(null);

            AndroidNetworking.initialize(MainActivity.this);
            FindLanguages();
        }
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        menu.add(1,HOME,1,"Home");
        menu.add(1,CONFIG,2,"Configuration");
        menu.add(1,HISTORY,3,"History");
        return true;
    }
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        Context context=this;
        switch (id){
            case HOME:
                break;
            case CONFIG:
                Intent intentCon=new Intent(context,Configuration.class);
                startActivity(intentCon);
                break;
            case HISTORY:
                Intent intentHis=new Intent(context,History.class);
                startActivity(intentHis);
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onClick(View view){
        if(!et_source.getText().toString().trim().equals("")){
            final View v = getWindow().peekDecorView();
            //close the keyboard
            if (v != null && v.getWindowToken() != null) {
                InputMethodManager imm = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(v.getWindowToken(), 0);
            }
            Translate();
        }else {
            Toast.makeText(MainActivity.this,"Just enter somthing to translate~",Toast.LENGTH_SHORT).show();}
    }
    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        target_lang=parent.getItemAtPosition(position).toString();
    }
    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }
    void Translate(){
        AndroidNetworking.post(Configuration.url+"translate")
                .addHeaders("Content-Type","application/x-www-form-urlencoded")
                .addBodyParameter("auth_key",Configuration.auth_key)
                .addBodyParameter("text",et_source.getText().toString())
                .addBodyParameter("target_lang",languages_name_lang.get(target_lang))
                .build()
                .getAsJSONObject(new JSONObjectRequestListener() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try{
                            JSONArray translations = response.getJSONArray("translations");
                            String detected_source_language=translations.getJSONObject(0).getString("detected_source_language");
                            tv_detected_lang.setText(languages_lang_name.get(detected_source_language));
                            String text=translations.getJSONObject(0).getString("text");
                            et_translated.setText(text);
                            String format="yyyy-MM-dd HH:mm:ss";
                            SimpleDateFormat sdf = new SimpleDateFormat(format);
                            String date = sdf.format(new Date());

                            Insert(et_source.getText().toString(),text,date);
                        }catch (JsonIOException | JSONException e){
                          Toast.makeText(MainActivity.this,e.toString(),Toast.LENGTH_SHORT).show();
                        }
                    }
                    @Override
                    public void onError(ANError anError) {
                        Toast.makeText(MainActivity.this,"Wait a minute or try again",Toast.LENGTH_SHORT).show();
                    }
                });
    }
    public void FindLanguages(){
        AndroidNetworking.post(Configuration.url+"languages")
                .addHeaders("Content-Type","application/x-www-form-urlencoded")
                .addBodyParameter("auth_key",Configuration.auth_key)
                .build()
                .getAsJSONArray(new JSONArrayRequestListener(){
                    @Override
                    public void onResponse(JSONArray response) {
                        try{
                            for(int i=0;i<response.length();i++){
                                JSONObject jo= (JSONObject) response.get(i);
                                languages_name_lang.put(jo.getString("name"),jo.getString("language"));
                                languages_lang_name.put(jo.getString("language"),jo.getString("name"));
                            }

                            String[] keys=languages_name_lang.keySet().toArray(new String[0]);
                            ArrayAdapter<String> adapter=new ArrayAdapter<String>(
                                    MainActivity.this, com.androidnetworking.R.layout.support_simple_spinner_dropdown_item ,keys);
                            spn_target.setAdapter(adapter);
                        }catch (JsonIOException | JSONException e){
                            Toast.makeText(MainActivity.this,e.toString(),Toast.LENGTH_SHORT).show();
                        }
                    }
                    @Override
                    public void onError(ANError anError) {
                        Toast.makeText(MainActivity.this,"Wait a minute or try again",Toast.LENGTH_SHORT).show();
                    }
                });
    }
    public void Insert(String source,String translated,String date){
        ContentValues values1 = new ContentValues();
        values1.put("source", source);
        values1.put("translated",translated);
        values1.put("date",date);
        db.insert("histories", null, values1);
    }

}