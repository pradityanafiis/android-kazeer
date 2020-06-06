package com.praditya.kazeer.database;

import android.content.Context;
import android.util.Log;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;

import com.praditya.kazeer.model.Cart;
import com.praditya.kazeer.model.Product;
import com.praditya.kazeer.model.ProductTransaction;

@Database(entities = {Cart.class}, version = 2, exportSchema = false)
public abstract class KazeerDatabase extends RoomDatabase {
    private static final String LOG_TAG = KazeerDatabase.class.getSimpleName();
    private static final Object LOCK = new Object();
    private static final String DB_NAME = "ProductDatabase";
    private static KazeerDatabase instance;

    public static KazeerDatabase getInstance(Context context) {
        if (instance == null) {
            synchronized (LOCK) {
                instance = Room.databaseBuilder(context.getApplicationContext(), KazeerDatabase.class, KazeerDatabase.DB_NAME)
                        .allowMainThreadQueries()
                        .fallbackToDestructiveMigration()
                        .build();
            }
        }
        return instance;
    }

    public abstract CartDao cartDao();
}
