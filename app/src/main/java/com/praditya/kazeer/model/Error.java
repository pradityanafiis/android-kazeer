package com.praditya.kazeer.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

public class Error {
    @SerializedName("category_id")
    @Expose
    private List<String> categoryId;
    private List<String> name;
    private List<String> price;
    private List<String> stock;

    public List<String> getCategoryId() {
        return categoryId;
    }

    public void setCategoryId(List<String> categoryId) {
        this.categoryId = categoryId;
    }

    public List<String> getName() {
        return name;
    }

    public void setName(List<String> name) {
        this.name = name;
    }

    public List<String> getPrice() {
        return price;
    }

    public void setPrice(List<String> price) {
        this.price = price;
    }

    public List<String> getStock() {
        return stock;
    }

    public void setStock(List<String> stock) {
        this.stock = stock;
    }
}
