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
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.LinkedList;

import MyClasses.Data;
import MyClasses.MyAdapter;
import MyClasses.MyDbHelper;

public class History extends AppCompatActivity implements View.OnClickListener{
    ListView list_view_history;
    MyAdapter myAdapter;
    LinkedList<Data> mData;
    Context mContext;
    Button btn_add_history;
    Button btn_delete_all;
    Button btn_add2;

    MyDbHelper myDBHelper;
    SQLiteDatabase db;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_history);
        mContext=this;
        mData=new LinkedList<Data>();
        myAdapter=new MyAdapter(mData,mContext);
        myDBHelper = new MyDbHelper(History.this, "my.db", null, 1);
        db = myDBHelper.getWritableDatabase();

        list_view_history=(ListView) findViewById(R.id.list_view_history);
        btn_add_history=(Button) findViewById(R.id.btn_add_history);
        btn_delete_all=(Button)findViewById(R.id.btn_delete_all);
        btn_add2=(Button) findViewById(R.id.btn_add2);


        list_view_history.setAdapter(myAdapter);
        btn_add_history.setOnClickListener(this);
        btn_delete_all.setOnClickListener(this);
        btn_add2.setOnClickListener(this);
        Query();
    }

    public void AddHistory(String source,String translated,String date,String hour){
        myAdapter.Add(new Data(source,translated,date));
    }
    @Override
    public void onClick(View view ){
        switch (view.getId()){
            case R.id.btn_add_history:
                String format="yyyy-MM-dd HH:mm:ss";
                SimpleDateFormat sdf = new SimpleDateFormat(format);
                String date = sdf.format(new Date());
                Insert("hello","你好",date);
                Query();
                break;
            case R.id.btn_delete_all:
                DeleteAll();
                Query();
                break;
            case R.id.btn_add2:
                String format2="yyyy-MM-dd HH:mm:ss";
                SimpleDateFormat sdf2 = new SimpleDateFormat(format2);
                String date2 = sdf2.format(new Date());
                Insert("hello","Bonjour",date2);
                Query();
                break;
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
        int id = item.getItemId();
        Context context=this;
        switch (id){
            case MainActivity.HOME:
                Intent intentMain=new Intent(context,MainActivity.class);
                startActivity(intentMain);
                break;
            case MainActivity.CONFIG:
                Intent intentCon=new Intent(context,Configuration.class);
                startActivity(intentCon);
                break;
            case MainActivity.HISTORY:
                break;
        }
        return super.onOptionsItemSelected(item);
    }
    public void Query(){
        DeleteOverflow();
        myAdapter.ClearData();
        Cursor cursor = db.query("histories", null, null, null, null, null, null);
        if (cursor.moveToFirst()) {
            do {
                @SuppressLint("Range") String source = cursor.getString(cursor.getColumnIndex("source"));
                @SuppressLint("Range") String translated = cursor.getString(cursor.getColumnIndex("translated"));
                @SuppressLint("Range") String date = cursor.getString(cursor.getColumnIndex("date"));
                myAdapter.Add(new Data(source,translated,date));
            } while (cursor.moveToNext());
        }
        cursor.close();
    }
    public int GetCount(){
        Cursor cursor = db.query("histories", null, null, null, null, null, null);
        return cursor.getCount();
    }
    public void Delete(String id){
        db.delete("histories", "historyid = ?", new String[]{id});
    }
    public void DeleteOverflow(){
        Cursor cursor = db.query("histories", null, null, null, null, null, null);
        if(cursor.moveToFirst()){
        while (cursor.getCount()>10&&cursor.moveToNext()){
        @SuppressLint("Range") int id = cursor.getInt(cursor.getColumnIndex("historyid"));
        Delete(Integer.toString(id));
        cursor = db.query("histories", null, null, null, null, null, null);
        Toast.makeText(History.this,Integer.toString(cursor.getCount()),Toast.LENGTH_SHORT).show();
        }
        }
        cursor.close();
    }
    public void DeleteAll(){
        db.execSQL("delete from histories");
    }

    public void Insert(String source,String translated,String date){
        ContentValues values1 = new ContentValues();
        values1.put("source", source);
        values1.put("translated",translated);
        values1.put("date",date);
        db.insert("histories", null, values1);
    }
}