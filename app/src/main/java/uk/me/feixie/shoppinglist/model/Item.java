package uk.me.feixie.shoppinglist.model;

import java.io.Serializable;

/**
 * Created by Fei on 02/12/2015.
 */
public class Item implements Serializable {

    private int id;
    private String name;
    private String category;
    private String barcode;
    private String price;

    public Item() {
    }

    public Item(int id, String name, String category, String barcode, String price) {
        this.id = id;
        this.name = name;
        this.category = category;
        this.barcode = barcode;
        this.price = price;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getBarcode() {
        return barcode;
    }

    public void setBarcode(String barcode) {
        this.barcode = barcode;
    }

    public String getPrice() {
        return price;
    }

    public void setPrice(String price) {
        this.price = price;
    }

    @Override
    public String toString() {
        return "Item{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", category='" + category + '\'' +
                ", barcode='" + barcode + '\'' +
                ", price='" + price + '\'' +
                '}';
    }
}
