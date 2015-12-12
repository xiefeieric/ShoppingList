package uk.me.feixie.shoppinglist.model;

import java.io.Serializable;

/**
 * Created by Fei on 08/12/2015.
 */
public class ShopList implements Serializable {

    private int id;
    private String listDate;
    private String title;
    private String money;
    private String itemBought;
    private String latitude;
    private String longitude;
    private int show;



    public ShopList() {
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getListDate() {
        return listDate;
    }

    public void setListDate(String listDate) {
        this.listDate = listDate;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getMoney() {
        return money;
    }

    public void setMoney(String money) {
        this.money = money;
    }

    public String getItemBought() {
        return itemBought;
    }

    public void setItemBought(String itemBought) {
        this.itemBought = itemBought;
    }

    public String getLatitude() {
        return latitude;
    }

    public void setLatitude(String latitude) {
        this.latitude = latitude;
    }

    public String getLongitude() {
        return longitude;
    }

    public void setLongitude(String longitude) {
        this.longitude = longitude;
    }

    public int getShow() {
        return show;
    }

    public void setShow(int show) {
        this.show = show;
    }

    @Override
    public String toString() {
        return "ShopList{" +
                "id=" + id +
                ", listDate='" + listDate + '\'' +
                ", title='" + title + '\'' +
                ", money='" + money + '\'' +
                ", itemBought='" + itemBought + '\'' +
                ", latitude='" + latitude + '\'' +
                ", longitude='" + longitude + '\'' +
                ", show=" + show +
                '}';
    }
}
