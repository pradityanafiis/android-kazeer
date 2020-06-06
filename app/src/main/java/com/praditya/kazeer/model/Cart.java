package com.praditya.kazeer.model;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;

@Entity(tableName = "cart")
public class Cart {
    @PrimaryKey(autoGenerate = true)
    private int id;

    @ColumnInfo(name = "product_id")
    private int productId;

    @ColumnInfo(name = "product_name")
    private String productName;

    @ColumnInfo(name = "quantity")
    private int quantity;

    @ColumnInfo(name = "price")
    private int price;

    @ColumnInfo(name = "total_price")
    private int totalPrice;

    public Cart(int productId, String productName, int quantity, int price, int totalPrice) {
        this.productId = productId;
        this.productName = productName;
        this.quantity = quantity;
        this.price = price;
        this.totalPrice = totalPrice;
    }

    @Ignore
    public Cart(int productId, int price) {
        this.productId = productId;
        this.price = price;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getProductId() {
        return productId;
    }

    public void setProductId(int productId) {
        this.productId = productId;
    }

    public String getProductName() {
        return productName;
    }

    public void setProductName(String productName) {
        this.productName = productName;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public int getPrice() {
        return price;
    }

    public void setPrice(int price) {
        this.price = price;
    }

    public int getTotalPrice() {
        return totalPrice;
    }

    public void setTotalPrice(int totalPrice) {
        this.totalPrice = totalPrice;
    }
}
