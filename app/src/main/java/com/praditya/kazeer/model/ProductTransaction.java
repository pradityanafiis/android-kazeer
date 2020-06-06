package com.praditya.kazeer.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class ProductTransaction {
    private int id;

    @SerializedName("transaction_id")
    @Expose
    private int transactionId;

    @SerializedName("product_id")
    @Expose
    private int productId;

    @SerializedName("product_name")
    @Expose
    private String productName;

    @SerializedName("product_price")
    @Expose
    private int productPrice;

    private int quantity;

    @SerializedName("total_price")
    @Expose
    private int totalPrice;

    public ProductTransaction(int productId, String productName, int productPrice, int quantity, int totalPrice) {
        this.productId = productId;
        this.productName = productName;
        this.productPrice = productPrice;
        this.quantity = quantity;
        this.totalPrice = totalPrice;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getTransactionId() {
        return transactionId;
    }

    public void setTransactionId(int transactionId) {
        this.transactionId = transactionId;
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

    public int getProductPrice() {
        return productPrice;
    }

    public void setProductPrice(int productPrice) {
        this.productPrice = productPrice;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public int getTotalPrice() {
        return totalPrice;
    }

    public void setTotalPrice(int totalPrice) {
        this.totalPrice = totalPrice;
    }
}
