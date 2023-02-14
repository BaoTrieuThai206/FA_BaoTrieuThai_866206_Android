package com.example.fa_baotrieuthai_866206_android;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.example.fa_baotrieuthai_866206_android.room.Product;
import com.example.fa_baotrieuthai_866206_android.room.ProductRoomDb;

import java.util.Arrays;
import java.util.List;

public class ProductAdapter extends ArrayAdapter {
    private static final String TAG = "ProductAdapter";
    Context context;
    int layoutRes;
    List<Product> productList;
    ProductRoomDb productRoomDb;

    public ProductAdapter(@NonNull Context context, int resource, List<Product> productList) {
        super(context, resource, productList);
        this.productList = productList;
        this.context = context;
        this.layoutRes = resource;
        productRoomDb = ProductRoomDb.getInstance(context);
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        LayoutInflater inflater = LayoutInflater.from(context);
        View v = convertView;
        if (v == null) v = inflater.inflate(layoutRes, null);
        TextView nameTV = v.findViewById(R.id.tv_name);
        TextView descriptionTV = v.findViewById(R.id.tv_description);
        TextView priceTV = v.findViewById(R.id.tv_price);
//        TextView latTV = v.findViewById(R.id.tv_lat);
//        TextView lngTV = v.findViewById(R.id.tv_lng);

        final Product product = productList.get(position);
        nameTV.setText(product.getName());
        descriptionTV.setText(product.getDescription());
        priceTV.setText("CAD " + String.valueOf(product.getPrice()));
//        latTV.setText(String.valueOf(product.getLat()));
//        lngTV.setText(String.valueOf(product.getLng()));


        v.findViewById(R.id.btn_edit).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                updateProduct(product);
            }

            private void updateProduct(final Product product) {
                AlertDialog.Builder builder = new AlertDialog.Builder(context);
                LayoutInflater layoutInflater = LayoutInflater.from(context);
                View view = layoutInflater.inflate(R.layout.dialog_update_product, null);
                builder.setView(view);
                final AlertDialog alertDialog = builder.create();
                alertDialog.show();

                final EditText etName = view.findViewById(R.id.et_name);
                final EditText etDescription = view.findViewById(R.id.et_description);
                final EditText etPrice = view.findViewById(R.id.et_price);

                etName.setText(product.getName());
                etDescription.setText(product.getDescription());
                etPrice.setText(String.valueOf(product.getPrice()));

                view.findViewById(R.id.btn_update).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        String name = etName.getText().toString().trim();
                        String description = etDescription.getText().toString().trim();
                        String price = etPrice.getText().toString().trim();
                        String lat = "0.0";
                        String lng = "0.0";

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

                        // Room
                        productRoomDb.productDao().updateProduct(
                                product.getId(),
                                name,
                                description,
                                Double.parseDouble(price),
                                Double.parseDouble(lat),
                                Double.parseDouble(lng)
                        );
                        loadProducts();
                        alertDialog.dismiss();
                    }
                });
            }
        });

        v.findViewById(R.id.btn_delete).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                deleteProduct(product);
            }

            private void deleteProduct(final Product product) {
                AlertDialog.Builder builder = new AlertDialog.Builder(context);
                builder.setTitle("Are you sure?");
                builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        productRoomDb.productDao().deleteProduct(product.getId());
                        loadProducts();
                    }
                });
                builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Toast.makeText(context, "The product (" + product.getName() + ") is not deleted", Toast.LENGTH_SHORT).show();
                    }
                });
                AlertDialog alertDialog = builder.create();
                alertDialog.show();
            }
        });
        return v;
    }

    private void loadProducts() {
        productList = productRoomDb.productDao().getAllProducts();
        notifyDataSetChanged();
    }
}
