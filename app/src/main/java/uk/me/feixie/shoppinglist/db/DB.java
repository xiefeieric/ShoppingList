package uk.me.feixie.shoppinglist.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by Fei on 02/12/2015.
 */
public class DB extends SQLiteOpenHelper {

    public DB(Context context) {
        super(context, "shoppinglist.db", null, 1);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String sql = "CREATE TABLE item (_id INTEGER PRIMARY KEY AUTOINCREMENT, name TEXT NOT NULL, category TEXT, barcode TEXT, price TEXT)";
        db.execSQL(sql);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }
}
