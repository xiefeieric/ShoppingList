package uk.me.feixie.shoppinglist.db;

import android.content.Context;

/**
 * Created by Fei on 02/12/2015.
 */
public class DBHelper {

    public static final String TABLE = "item";
    private DB mDB;

    public DBHelper(Context context) {
        mDB = new DB(context);
    }
}
