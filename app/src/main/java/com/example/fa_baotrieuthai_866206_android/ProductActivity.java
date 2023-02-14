package com.example.fa_baotrieuthai_866206_android;

import android.os.Bundle;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.fa_baotrieuthai_866206_android.room.Product;
import com.example.fa_baotrieuthai_866206_android.room.ProductRoomDb;

import java.util.ArrayList;
import java.util.List;

public class ProductActivity extends AppCompatActivity {

    // Room db instance
    private ProductRoomDb productRoomDb;

    List<Product> productList;
    ListView productsListView;
    TextView totalTV;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_product);

        totalTV = findViewById(R.id.tv_total);

        productsListView = findViewById(R.id.lv_products);
        productList = new ArrayList<>();

        productRoomDb = ProductRoomDb.getInstance(this);
        loadProducts();

        productsListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
                // handle the click event here, for example:
                Product clickedProduct = productList.get(position);
                Toast.makeText(ProductActivity.this, "Clicked on product " + clickedProduct.getName(), Toast.LENGTH_SHORT).show();
            }
        });


    }

    private void loadProducts() {
        productList = productRoomDb.productDao().getAllProducts();
        totalTV.setText("Total " + productList.size() + " products");
        ProductAdapter productAdapter = new ProductAdapter(this, R.layout.list_layout_product, productList);
        productsListView.setAdapter(productAdapter);
    }

}
