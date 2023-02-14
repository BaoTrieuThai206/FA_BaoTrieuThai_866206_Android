package com.example.fa_baotrieuthai_866206_android.room;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;

import java.util.List;
@Dao
public interface ProductDao {
    @Insert
    void insertProduct(Product product);

    @Query("DELETE FROM product")
    void deleteAllProducts();

    @Query("DELETE FROM product WHERE id = :id" )
    int deleteProduct(int id);

    @Query("UPDATE product SET name = :name, description = :description, price = :price, lat = :lat, lng = :lng WHERE id = :id")
    int updateProduct(int id, String name, String description, double price, double lat, double lng);

    @Query("SELECT * FROM product ORDER BY id")
    List<Product> getAllProducts();
}
