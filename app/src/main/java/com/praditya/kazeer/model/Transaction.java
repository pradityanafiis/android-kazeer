package com.praditya.kazeer.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.Date;

public class Transaction {
    private int id;
    @SerializedName("customer_id")
    @Expose
    private int customerId;
    private String invoiceNumber;
    private int amount;
    private int pay;
    private int change;
    @SerializedName("created_at")
    @Expose
    private String date;
    private Customer customer;
    @SerializedName("products")
    @Expose
    private ArrayList<ProductTransaction> productTransactions;

    public Transaction(int customerId, int amount, int pay, int change, ArrayList<ProductTransaction> productTransactions) {
        this.customerId = customerId;
        this.amount = amount;
        this.pay = pay;
        this.change = change;
        this.productTransactions = productTransactions;
    }

    public int getId() {
        return id;
    }

    public int getCustomerId() {
        return customerId;
    }

    public String getInvoiceNumber() {
        return invoiceNumber;
    }

    public int getAmount() {
        return amount;
    }

    public int getPay() {
        return pay;
    }

    public int getChange() {
        return change;
    }

    public String getDate() {
        return date;
    }

    public Customer getCustomer() {
        return customer;
    }

    public ArrayList<ProductTransaction> getProductTransactions() {
        return productTransactions;
    }
}
