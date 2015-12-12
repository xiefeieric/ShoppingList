package uk.me.feixie.shoppinglist.db;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import java.util.ArrayList;
import java.util.List;

import uk.me.feixie.shoppinglist.model.Item;
import uk.me.feixie.shoppinglist.model.ShopList;

/**
 * Created by Fei on 02/12/2015.
 */
public class DBHelper {

    public static final String TABLE_ITEM = "item";
    public static final String TABLE_LIST = "list";
    private DB mDB;

    public DBHelper(Context context) {
        mDB = new DB(context);
    }

    public void addList(ShopList list) {
        SQLiteDatabase writableDatabase = mDB.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("listDate", list.getListDate());
        values.put("title", list.getTitle());
        values.put("money", list.getMoney());
        values.put("itemBought", list.getItemBought());
        values.put("latitude", list.getLatitude());
        values.put("longitude", list.getLongitude());
        values.put("show", list.getShow());
        writableDatabase.insert(TABLE_LIST, null, values);
        writableDatabase.close();
    }

    public void hideList(ShopList list) {
        SQLiteDatabase writableDatabase = mDB.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("show", 1);
        writableDatabase.update(TABLE_LIST, values, "id=?", new String[]{String.valueOf(list.getId())});
        writableDatabase.close();
    }

    public List<ShopList> queryList() {
        SQLiteDatabase readableDatabase = mDB.getReadableDatabase();
        Cursor cursor = readableDatabase.query(TABLE_LIST, null, null, null, null, null, null);
        List<ShopList> shopLists = new ArrayList<>();
        if (cursor != null) {
            while (cursor.moveToNext()) {
                ShopList shopList = new ShopList();
                int id = cursor.getInt(cursor.getColumnIndex("_id"));
                shopList.setId(id);
                String listDate = cursor.getString(cursor.getColumnIndex("listDate"));
                shopList.setListDate(listDate);
                String title = cursor.getString(cursor.getColumnIndex("title"));
                shopList.setTitle(title);
                String money = cursor.getString(cursor.getColumnIndex("money"));
                shopList.setMoney(money);
                String itemBought = cursor.getString(cursor.getColumnIndex("itemBought"));
                shopList.setItemBought(itemBought);
                String latitude = cursor.getString(cursor.getColumnIndex("latitude"));
                shopList.setLatitude(latitude);
                String longitude = cursor.getString(cursor.getColumnIndex("longitude"));
                shopList.setLongitude(longitude);
                int show = cursor.getInt(cursor.getColumnIndex("show"));
                shopList.setShow(show);
                shopLists.add(shopList);
            }
        }
        cursor.close();
        readableDatabase.close();
        return shopLists;
    }

    public int queryListId(ShopList list) {
        SQLiteDatabase readableDatabase = mDB.getReadableDatabase();
        Cursor cursor = readableDatabase.query(TABLE_LIST, new String[]{"_id"}, "title=? AND listDate=?",
                new String[]{list.getTitle(), list.getListDate()}, null, null, null);
        if (cursor.moveToNext()) {
            int id = cursor.getInt(cursor.getColumnIndex("_id"));
            cursor.close();
            readableDatabase.close();
            return id;

        }
        cursor.close();
        readableDatabase.close();
        return 0;
    }

    public void addItem(Item item) {
        SQLiteDatabase writableDatabase = mDB.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("slId", item.getSlId());
        values.put("name", item.getName());
        values.put("category", item.getCategory());
        values.put("barcode", item.getBarcode());
        values.put("price", item.getPrice());
        values.put("quantity", item.getQuantity());
        values.put("buyStatus", item.getBuyStatus());
        writableDatabase.insert(TABLE_ITEM, null, values);
        writableDatabase.close();
    }

    public void deleteItem(Item item) {
        SQLiteDatabase writableDatabase = mDB.getWritableDatabase();
        writableDatabase.delete(TABLE_ITEM, "_id=?", new String[]{String.valueOf(item.getId())});
        writableDatabase.close();
    }

    public void updateItem(Item item) {
        SQLiteDatabase writableDatabase = mDB.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("name", item.getName());
        values.put("category", item.getCategory());
        values.put("barcode", item.getBarcode());
        values.put("price", item.getPrice());
        values.put("quantity", item.getQuantity());
        values.put("buyStatus", item.getBuyStatus());
        writableDatabase.update(TABLE_ITEM, values, "_id=?", new String[]{String.valueOf(item.getId())});
        writableDatabase.close();
    }

    public List<Item> queryAllItems(ShopList shopList) {
        SQLiteDatabase readableDatabase = mDB.getReadableDatabase();
        Cursor cursor = readableDatabase.query(TABLE_ITEM, null, "slId=?", new String[]{String.valueOf(shopList.getId())}, null, null, null);
        List<Item> items = new ArrayList<>();
        if (cursor != null) {
            while (cursor.moveToNext()) {
                Item item = new Item();
                int id = cursor.getInt(cursor.getColumnIndex("_id"));
                item.setId(id);
//                int slId = cursor.getInt(cursor.getColumnIndex("slId"));
//                item.setSlId(slId);
                String name = cursor.getString(cursor.getColumnIndex("name"));
                item.setName(name);
                String category = cursor.getString(cursor.getColumnIndex("category"));
                item.setCategory(category);
                String barcode = cursor.getString(cursor.getColumnIndex("barcode"));
                item.setBarcode(barcode);
                String price = cursor.getString(cursor.getColumnIndex("price"));
                item.setPrice(price);
                String quantity = cursor.getString(cursor.getColumnIndex("quantity"));
                item.setQuantity(quantity);
                int buyStatus = cursor.getInt(cursor.getColumnIndex("buyStatus"));
                item.setBuyStatus(buyStatus);
                items.add(item);
            }
            cursor.close();
            readableDatabase.close();
            return items;
        }
        cursor.close();
        readableDatabase.close();
        return null;
    }

}
