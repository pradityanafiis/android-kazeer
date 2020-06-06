package com.praditya.kazeer.view.ui;

import androidx.appcompat.app.AppCompatActivity;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import android.os.Bundle;

import com.praditya.kazeer.R;

public class TransactionActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_transaction);
        AppBarConfiguration appBarConfiguration = new AppBarConfiguration.Builder(R.id.navigation_choose_product, R.id.navigation_choose_customer).build();
        NavController navController = Navigation.findNavController(this, R.id.transaction_fragment);
        NavigationUI.setupActionBarWithNavController(TransactionActivity.this, navController, appBarConfiguration);
    }
}
