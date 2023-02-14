package com.example.fa_baotrieuthai_866206_android;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.example.fa_baotrieuthai_866206_android.room.Product;
import com.example.fa_baotrieuthai_866206_android.room.ProductRoomDb;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polygon;
import com.google.android.gms.maps.model.PolygonOptions;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

public class MainActivity extends AppCompatActivity implements View.OnClickListener, OnMapReadyCallback {

    private ProductRoomDb productRoomDb;
    private GoogleMap mMap;
    private static final int REQUEST_CODE = 1;
    private double lat, lng;

    EditText etName, etDescription, etPrice;

    // location with location manager and listener
    LocationManager locationManager;
    LocationListener locationListener;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        etName = findViewById(R.id.et_name);
        etDescription = findViewById(R.id.et_description);
        etPrice = findViewById(R.id.et_price);

        findViewById(R.id.btn_add_product).setOnClickListener(this);
        findViewById(R.id.tv_view_products).setOnClickListener(this);

        productRoomDb = ProductRoomDb.getInstance(this);

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
    }

    @SuppressLint("NonConstantResourceId")
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_add_product:
                addProduct();
                break;
            case R.id.tv_view_products:
                startActivity(new Intent(this, ProductActivity.class));
                break;
        }

    }

    private void addProduct() {
        String name = etName.getText().toString().trim();
        String description = etDescription.getText().toString().trim();
        String price = etPrice.getText().toString().trim();
        lat = lat;
        lng = lng;

        if (name.isEmpty()) {
            etName.setError("Name field cannot be empty!");
            etName.requestFocus();
            return;
        }
        if (description.isEmpty()) {
            etName.setError("Description field cannot be empty!");
            etName.requestFocus();
            return;
        }

        if (price.isEmpty()) {
            etPrice.setError("Price cannot be empty!");
            etPrice.requestFocus();
            return;
        }

        // Insert into room db
        Product product = new Product(name, description, Double.parseDouble(price), lat, lng);
        productRoomDb.productDao().insertProduct(product);
        Toast.makeText(this, "Product Added", Toast.LENGTH_SHORT).show();
        clearFields();
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        clearFields();
    }

    private void clearFields() {
        etName.setText("");
        etDescription.setText("");
        etPrice.setText("");
        etName.clearFocus();
        etDescription.clearFocus();
        etPrice.clearFocus();
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        locationListener = new LocationListener() {
            @Override
            public void onLocationChanged(Location location) {
                setHomeMarker(location);
            }

            @Override
            public void onProviderEnabled(String provider) {

            }

            @Override
            public void onProviderDisabled(String provider) {

            }
        };

        if (!hasLocationPermission())
            requestLocationPermission();
        else
            startUpdateLocation();


        // apply long press gesture
        mMap.setOnMapLongClickListener(new GoogleMap.OnMapLongClickListener() {
            @Override
            public void onMapLongClick(LatLng latLng) {
                // set marker
                MarkerOptions options = new MarkerOptions().position(latLng)
                        .title("Product");

                //save to db
                double latitude = latLng.latitude;
                double longitude = latLng.longitude;
            }

        });
    }

    private void startUpdateLocation() {
        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 5000, 0, locationListener);
    }

    private void requestLocationPermission() {
        ActivityCompat.requestPermissions(this, new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION}, REQUEST_CODE);
    }

    private boolean hasLocationPermission() {
        return ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED;
    }

    private void setHomeMarker(Location location) {
        LatLng userLocation = new LatLng(location.getLatitude(), location.getLongitude());
        MarkerOptions options = new MarkerOptions().position(userLocation)
                .title("Lambton College")
                .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_AZURE))
                .snippet("You are here!");
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(userLocation, 10));
    }
}