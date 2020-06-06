package com.praditya.kazeer.model;

import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;

public class TransactionSummary {
    @SerializedName("count_transaction")
    private int countTransaction;

    @SerializedName("sum_amount")
    private int sumAmount;

    @SerializedName("last_week_transaction")
    private ArrayList<DailyTransaction> dailyTransactions;

    public int getCountTransaction() {
        return countTransaction;
    }

    public void setCountTransaction(int countTransaction) {
        this.countTransaction = countTransaction;
    }

    public int getSumAmount() {
        return sumAmount;
    }

    public void setSumAmount(int sumAmount) {
        this.sumAmount = sumAmount;
    }

    public ArrayList<DailyTransaction> getDailyTransactions() {
        return dailyTransactions;
    }

    public void setDailyTransactions(ArrayList<DailyTransaction> dailyTransactions) {
        this.dailyTransactions = dailyTransactions;
    }
}
