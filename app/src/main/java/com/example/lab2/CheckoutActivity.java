package com.example.lab2;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.lab2.adapter.OrderDetailAdapter;
import com.example.lab2.database.AppDatabase;
import com.example.lab2.entity.Order;
import com.example.lab2.entity.OrderDetail;

import java.util.List;

public class CheckoutActivity extends AppCompatActivity {
    private RecyclerView rvOrderItems;
    private TextView tvTotal;
    private Button btnPay, btnBackCheckout;
    private AppDatabase db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_checkout);

        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setTitle("Thanh toán");
        }

        db = AppDatabase.getInstance(this);
        rvOrderItems = findViewById(R.id.rvOrderItems);
        tvTotal = findViewById(R.id.tvTotal);
        btnPay = findViewById(R.id.btnPay);
        btnBackCheckout = findViewById(R.id.btnBackCheckout);

        SharedPreferences pref = getSharedPreferences("UserPrefs", MODE_PRIVATE);
        int userId = pref.getInt("userId", -1);

        Order order = db.shoppingDao().getPendingOrderByUser(userId);
        if (order == null) {
            Toast.makeText(this, "Không có đơn hàng chờ", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        List<OrderDetail> details = db.shoppingDao().getOrderDetailsByOrder(order.orderId);
        rvOrderItems.setLayoutManager(new LinearLayoutManager(this));
        rvOrderItems.setAdapter(new OrderDetailAdapter(details, db));

        double total = 0;
        for (OrderDetail detail : details) {
            total += detail.unitPrice * detail.quantity;
        }
        tvTotal.setText("Tổng tiền: $" + total);

        btnPay.setOnClickListener(v -> {
            showConfirmPaymentDialog(order);
        });

        btnBackCheckout.setOnClickListener(v -> finish());
    }

    private void showConfirmPaymentDialog(Order order) {
        new AlertDialog.Builder(this)
                .setTitle("Xác nhận thanh toán")
                .setMessage("Bạn có chắc chắn muốn thanh toán đơn hàng này không?")
                .setPositiveButton("Thanh toán", (dialog, which) -> {
                    // Cập nhật trạng thái Order (Paid)
                    order.status = "Paid";
                    db.shoppingDao().updateOrder(order);
                    
                    Toast.makeText(this, "Thanh toán thành công!", Toast.LENGTH_SHORT).show();
                    
                    // Chuyển sang màn hình Invoice
                    Intent intent = new Intent(this, InvoiceActivity.class);
                    intent.putExtra("orderId", order.orderId);
                    startActivity(intent);
                    finish();
                })
                .setNegativeButton("Hủy", (dialog, which) -> dialog.dismiss())
                .show();
    }

    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return true;
    }
}