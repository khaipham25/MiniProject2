package com.example.lab2.entity;

import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.PrimaryKey;

@Entity(tableName = "order_details",
        foreignKeys = {
                @ForeignKey(entity = Order.class,
                        parentColumns = "orderId",
                        childColumns = "orderId",
                        onDelete = ForeignKey.CASCADE),
                @ForeignKey(entity = Product.class,
                        parentColumns = "productId",
                        childColumns = "productId",
                        onDelete = ForeignKey.CASCADE)
        })
public class OrderDetail {
    @PrimaryKey(autoGenerate = true)
    public int orderDetailId;
    public int orderId;
    public int productId;
    public int quantity;
    public double unitPrice;
}