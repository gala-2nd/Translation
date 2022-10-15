package MyClasses;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class MyDbHelper extends SQLiteOpenHelper {
    public MyDbHelper(Context context, String name, SQLiteDatabase.CursorFactory factory,
                          int version) {super(context, "my.db", null, 1); }
    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("create table histories(historyid integer primary key AUTOINCREMENT," +
                "source text," +
                "translated text,"+
                "date text)"
        );
    }
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
    }
}
