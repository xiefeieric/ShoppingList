package uk.me.feixie.shoppinglist.db;

import android.content.ContentValues;
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
//        private int id;
//        private int slId;
//        private String name;
//        private String category;
//        private String barcode;
//        private String price;
//        private String quantity;
//        private boolean buyStatus;
        String sql = "CREATE TABLE user (_id INTEGER PRIMARY KEY AUTOINCREMENT, name TEXT, notice TEXT)";
        db.execSQL(sql);

        sql = "CREATE TABLE list (_id INTEGER PRIMARY KEY AUTOINCREMENT, uid INTEGER NOT NULL, listDate TEXT, " +
                "title TEXT, money TEXT, itemBought TEXT, latitude TEXT, longitude TEXT, show INTEGER, FOREIGN KEY (uid) REFERENCES user (_id))";
        db.execSQL(sql);



        sql = "CREATE TABLE item (_id INTEGER PRIMARY KEY AUTOINCREMENT, slid INTEGER NOT NULL, name TEXT NOT NULL, " +
                "category TEXT, barcode TEXT, price TEXT, quantity TEXT, expireDate TEXT, buyStatus INTEGER, FOREIGN KEY (slid) REFERENCES list (_id))";
        db.execSQL(sql);

        ContentValues values = new ContentValues();
        values.put("name","Show All");
        db.insert("user",null,values);
//        private int id;
//        private String listDate;
//        private String title;
//        private String money;
//        private String itemBought;
//        private String latitude;
//        private String longitude;


    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }
}
