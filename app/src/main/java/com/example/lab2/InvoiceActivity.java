package com.example.lab2;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.lab2.adapter.OrderDetailAdapter;
import com.example.lab2.database.AppDatabase;
import com.example.lab2.entity.Order;
import com.example.lab2.entity.OrderDetail;

import java.util.List;

public class InvoiceActivity extends AppCompatActivity {
    private TextView tvUser, tvId, tvDate, tvTotal;
    private RecyclerView rvItems;
    private Button btnHome;
    private AppDatabase db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_invoice);

        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle("Hóa Đơn");
        }

        db = AppDatabase.getInstance(this);
        int orderId = getIntent().getIntExtra("orderId", -1);
        Order order = db.shoppingDao().getOrderById(orderId);

        tvUser = findViewById(R.id.tvInvoiceUser);
        tvId = findViewById(R.id.tvInvoiceId);
        tvDate = findViewById(R.id.tvInvoiceDate);
        tvTotal = findViewById(R.id.tvInvoiceTotal);
        rvItems = findViewById(R.id.rvInvoiceItems);
        btnHome = findViewById(R.id.btnInvoiceHome);

        if (order != null) {
            SharedPreferences pref = getSharedPreferences("UserPrefs", MODE_PRIVATE);
            String fullName = pref.getString("fullName", "Unknown");

            tvUser.setText("Khách hàng: " + fullName);
            tvId.setText("Mã hóa đơn: #" + order.orderId);
            tvDate.setText("Ngày đặt: " + order.orderDate);

            List<OrderDetail> details = db.shoppingDao().getOrderDetailsByOrder(order.orderId);
            rvItems.setLayoutManager(new LinearLayoutManager(this));
            rvItems.setAdapter(new OrderDetailAdapter(details, db));

            double total = 0;
            for (OrderDetail d : details) {
                total += d.unitPrice * d.quantity;
            }
            tvTotal.setText("Tổng cộng: $" + total);
        }

        btnHome.setOnClickListener(v -> {
            Intent intent = new Intent(this, MainActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(intent);
        });
    }
}