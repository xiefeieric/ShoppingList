package uk.me.feixie.shoppinglist.model;

import java.io.Serializable;

/**
 * Created by Fei on 02/12/2015.
 */
public class Item implements Serializable {

    private int id;
    private int slId;
    private String name;
    private String category;
    private String barcode;
    private String price;
    private String quantity;
    private int buyStatus;

    public Item() {
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getSlId() {
        return slId;
    }

    public void setSlId(int slId) {
        this.slId = slId;
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

    public String getQuantity() {
        return quantity;
    }

    public void setQuantity(String quantity) {
        this.quantity = quantity;
    }

    public int getBuyStatus() {
        return buyStatus;
    }

    public void setBuyStatus(int buyStatus) {
        this.buyStatus = buyStatus;
    }

    @Override
    public String toString() {
        return "Item{" +
                "id=" + id +
                ", slId=" + slId +
                ", name='" + name + '\'' +
                ", category='" + category + '\'' +
                ", barcode='" + barcode + '\'' +
                ", price='" + price + '\'' +
                ", quantity='" + quantity + '\'' +
                ", buyStatus=" + buyStatus +
                '}';
    }
}
