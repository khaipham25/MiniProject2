package com.example.lab2.database;

import android.content.Context;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;

import com.example.lab2.dao.ShoppingDao;
import com.example.lab2.entity.Category;
import com.example.lab2.entity.Order;
import com.example.lab2.entity.OrderDetail;
import com.example.lab2.entity.Product;
import com.example.lab2.entity.User;

@Database(entities = {User.class, Category.class, Product.class, Order.class, OrderDetail.class}, version = 1)
public abstract class AppDatabase extends RoomDatabase {
    private static AppDatabase instance;

    public abstract ShoppingDao shoppingDao();

    public static synchronized AppDatabase getInstance(Context context) {
        if (instance == null) {
            instance = Room.databaseBuilder(context.getApplicationContext(),
                            AppDatabase.class, "shopping_db")
                    .fallbackToDestructiveMigration()
                    .allowMainThreadQueries() // For simplicity in this lab, normally use background threads
                    .build();
        }
        return instance;
    }
}