package com.praditya.kazeer.database;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;

import com.praditya.kazeer.model.Cart;

import java.util.List;

@Dao
public interface CartDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void addToCart(Cart cart);

    @Update
    void updateCart(Cart cart);

    @Delete
    void removeFromCart(Cart cart);

    @Query("SELECT * FROM cart")
    List<Cart> getAllCart();

    @Query("SELECT * FROM cart WHERE product_id = :product_id")
    Cart getSingleCart(int product_id);

    @Query("SELECT SUM(total_price) FROM cart")
    int getTotalPrice();

    @Query("SELECT SUM(quantity) FROM cart")
    int getTotalItems();

    @Query("DELETE FROM cart")
    void clearCart();

    @Query("SELECT COUNT(id) FROM cart")
    int countCart();
}
