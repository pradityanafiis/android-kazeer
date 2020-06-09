package com.praditya.kazeer.api;

import com.praditya.kazeer.api.response.MultipleResponse;
import com.praditya.kazeer.api.response.SingleResponse;
import com.praditya.kazeer.model.Category;
import com.praditya.kazeer.model.Customer;
import com.praditya.kazeer.model.Product;
import com.praditya.kazeer.model.DailyTransaction;
import com.praditya.kazeer.model.Transaction;
import com.praditya.kazeer.model.TransactionSummary;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Path;

public interface Services {
    // SERVICES FOR CATEGORY
    @GET("category")
    Call<MultipleResponse<Category>> getCategory();

    @DELETE("category/{id}")
    Call<SingleResponse<Category>> deleteCategory(
        @Path("id") int id
    );

    @POST("category")
    Call<SingleResponse<Category>> createCategory(
        @Body Category category
    );

    @PUT("category/{id}")
    Call<SingleResponse<Category>> editCategory(
        @Path("id") int id,
        @Body Category category
    );

    // SERVICES FOR CUSTOMER
    @GET("customer")
    Call<MultipleResponse<Customer>> getCustomer();

    @POST("customer")
    Call<SingleResponse<Customer>> createCustomer(
        @Body Customer customer
    );

    @DELETE("customer/{id}")
    Call<SingleResponse<Customer>> deleteCustomer(
        @Path("id") int id
    );

    @PUT("customer/{id}")
    Call<SingleResponse<Customer>> editCustomer(
        @Path("id") int id,
        @Body Customer customer
    );

    // SERVICES FOR PRODUCT
    @GET("product")
    Call<MultipleResponse<Product>> getProduct();

    @GET("product/available")
    Call<MultipleResponse<Product>> getProductAvailable();

    @POST("product")
    Call<SingleResponse<Product>> createProduct(
        @Body Product product
    );

    @DELETE("product/{id}")
    Call<SingleResponse<Product>> deleteProduct(
        @Path("id") int id
    );

    @PUT("product/{id}")
    Call<SingleResponse<Product>> editProduct(
        @Path("id") int id,
        @Body Product product
    );

    // SERVICES FOR TRANSACTION
    @GET("transaction")
    Call<MultipleResponse<Transaction>> getTransaction();

    @GET("transaction/summary")
    Call<SingleResponse<TransactionSummary>> getTransactionSummary();

    @POST("transaction")
    Call<SingleResponse<Transaction>> createTransaction(
        @Body Transaction transaction
    );
}
