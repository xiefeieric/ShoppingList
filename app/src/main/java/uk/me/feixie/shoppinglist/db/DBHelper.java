package uk.me.feixie.shoppinglist.db;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import java.util.ArrayList;
import java.util.List;

import uk.me.feixie.shoppinglist.model.Item;
import uk.me.feixie.shoppinglist.model.ShopList;
import uk.me.feixie.shoppinglist.model.User;

/**
 * Created by Fei on 02/12/2015.
 */
public class DBHelper {

    public static final String TABLE_ITEM = "item";
    public static final String TABLE_LIST = "list";
    public static final String TABLE_USER = "user";
    private DB mDB;

    public DBHelper(Context context) {
        mDB = new DB(context);
    }

    public synchronized void addList(ShopList list) {
        SQLiteDatabase writableDatabase = mDB.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("uid",list.getUid());
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

    public synchronized void hideList(ShopList list) {
        SQLiteDatabase writableDatabase = mDB.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("show", 1);
        writableDatabase.update(TABLE_LIST, values, "id=?", new String[]{String.valueOf(list.getId())});
        writableDatabase.close();
    }

    public synchronized void updateList(ShopList list) {
        SQLiteDatabase writableDatabase = mDB.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("uid",list.getUid());
        values.put("listDate",list.getListDate());
        values.put("title",list.getTitle());
        values.put("money",list.getMoney());
        values.put("itemBought",list.getItemBought());
        values.put("latitude",list.getLatitude());
        values.put("longitude",list.getLongitude());
        values.put("show",list.getShow());
        writableDatabase.update(TABLE_LIST,values,"_id=?",new String[]{String.valueOf(list.getId())});
    }

    public synchronized List<ShopList> queryList() {
        SQLiteDatabase readableDatabase = mDB.getReadableDatabase();
        Cursor cursor = readableDatabase.query(TABLE_LIST, null, "show=?", new String[]{"0"}, null, null, null);
        List<ShopList> shopLists = new ArrayList<>();
        if (cursor != null) {
            while (cursor.moveToNext()) {
                ShopList shopList = new ShopList();
                int id = cursor.getInt(cursor.getColumnIndex("_id"));
                shopList.setId(id);
                int uid = cursor.getInt(cursor.getColumnIndex("uid"));
                shopList.setUid(uid);
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

    public synchronized List<ShopList> queryUserList(int uid) {
        SQLiteDatabase readableDatabase = mDB.getReadableDatabase();
        Cursor cursor = readableDatabase.query(TABLE_LIST, null, "uid=? AND show=?", new String[]{String.valueOf(uid),"0"}, null, null, null);
        List<ShopList> shopLists = new ArrayList<>();
        if (cursor != null) {
            while (cursor.moveToNext()) {
                ShopList shopList = new ShopList();
                int id = cursor.getInt(cursor.getColumnIndex("_id"));
                shopList.setId(id);
//                int uid1 = cursor.getInt(cursor.getColumnIndex("uid"));
                shopList.setUid(uid);
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
                shopLists.add(shopList);
            }
        }
        cursor.close();
        readableDatabase.close();
        return shopLists;
    }

    public synchronized int queryListId(ShopList list) {
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

    public synchronized void addItem(Item item) {
        SQLiteDatabase writableDatabase = mDB.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("slId", item.getSlId());
        values.put("name", item.getName());
        values.put("category", item.getCategory());
        values.put("barcode", item.getBarcode());
        values.put("price", item.getPrice());
        values.put("quantity", item.getQuantity());
        values.put("buyStatus", item.getBuyStatus());
        values.put("expireDate",item.getExpireDate());
        writableDatabase.insert(TABLE_ITEM, null, values);
        writableDatabase.close();
    }

    public synchronized void deleteItem(Item item) {
        SQLiteDatabase writableDatabase = mDB.getWritableDatabase();
        writableDatabase.delete(TABLE_ITEM, "_id=?", new String[]{String.valueOf(item.getId())});
        writableDatabase.close();
    }

    public synchronized void updateItem(Item item) {
        SQLiteDatabase writableDatabase = mDB.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("name", item.getName());
        values.put("category", item.getCategory());
        values.put("barcode", item.getBarcode());
        values.put("price", item.getPrice());
        values.put("quantity", item.getQuantity());
        values.put("buyStatus", item.getBuyStatus());
        values.put("expireDate", item.getExpireDate());
        writableDatabase.update(TABLE_ITEM, values, "_id=?", new String[]{String.valueOf(item.getId())});
        writableDatabase.close();
    }

    public synchronized List<Item> queryAllItems(ShopList shopList) {
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
                String expireDate = cursor.getString(cursor.getColumnIndex("expireDate"));
                item.setExpireDate(expireDate);
                int buyStatus = cursor.getInt(cursor.getColumnIndex("buyStatus"));
                item.setBuyStatus(buyStatus);
                items.add(item);
            }
            cursor.close();
            readableDatabase.close();
            return items;
        } else {
            cursor.close();
            readableDatabase.close();
            return null;
        }
    }

    public synchronized List<Item> queryMostUsedItems() {
        SQLiteDatabase readableDatabase = mDB.getReadableDatabase();
        String sql = "SELECT       `name`,\n" +
                "             COUNT(`name`) AS `name_occurrence` \n" +
                "    FROM     `item`\n" +
                "    GROUP BY `name`\n" +
                "    ORDER BY `name_occurrence` DESC";
        Cursor cursor = readableDatabase.rawQuery(sql, null);
        List<Item> items = new ArrayList<>();
        if (cursor != null) {
            while (cursor.moveToNext()) {
                Item item = new Item();
//                int id = cursor.getInt(cursor.getColumnIndex("_id"));
//                item.setId(id);
//                int slId = cursor.getInt(cursor.getColumnIndex("slId"));
//                item.setSlId(slId);
                String name = cursor.getString(cursor.getColumnIndex("name"));
                item.setName(name);
//                String category = cursor.getString(cursor.getColumnIndex("category"));
//                item.setCategory(category);
//                String barcode = cursor.getString(cursor.getColumnIndex("barcode"));
//                item.setBarcode(barcode);
//                String price = cursor.getString(cursor.getColumnIndex("price"));
//                item.setPrice(price);
//                String quantity = cursor.getString(cursor.getColumnIndex("quantity"));
//                item.setQuantity(quantity);
//                String expireDate = cursor.getString(cursor.getColumnIndex("expireDate"));
//                item.setExpireDate(expireDate);
//                int buyStatus = cursor.getInt(cursor.getColumnIndex("buyStatus"));
//                item.setBuyStatus(buyStatus);
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

    public synchronized List<User> queryAllUser() {
        SQLiteDatabase readableDatabase = mDB.getReadableDatabase();
        Cursor cursor = readableDatabase.query(TABLE_USER, null, "show=?", new String[]{"2"}, null, null, null);
        List<User> userList = new ArrayList<>();
        if (cursor != null) {
            while (cursor.moveToNext()) {
                User user = new User();
                int id = cursor.getInt(cursor.getColumnIndex("_id"));
                user.setId(id);
                String name = cursor.getString(cursor.getColumnIndex("name"));
                user.setName(name);
                String notice = cursor.getString(cursor.getColumnIndex("notice"));
                user.setNotice(notice);
//                String show = cursor.getString(cursor.getColumnIndex("show"));
//                user.setShow(Integer.parseInt(show));
                userList.add(user);
            }
        }
        cursor.close();
        readableDatabase.close();
        return userList;
    }

    public synchronized void addUser(User user) {
        SQLiteDatabase writableDatabase = mDB.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("name",user.getName());
        values.put("notice",user.getNotice());
        values.put("show",String.valueOf(user.getShow()));
        writableDatabase.insert(TABLE_USER,null,values);
        writableDatabase.close();
    }

    public synchronized void deleteUser(User user) {
        SQLiteDatabase writableDatabase = mDB.getWritableDatabase();
        writableDatabase.delete(TABLE_USER,"_id=?",new String[]{String.valueOf(user.getId())});
        writableDatabase.close();
    }

    public synchronized void updateUser(User user) {
        SQLiteDatabase writableDatabase = mDB.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("name",user.getName());
        values.put("notice",user.getNotice());
        values.put("show",String.valueOf(user.getShow()));
        writableDatabase.update(TABLE_USER,values,"_id=?",new String[]{String.valueOf(user.getId())});
    }

}
