package com.example.lab2;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.example.lab2.database.AppDatabase;
import com.example.lab2.entity.Order;
import com.example.lab2.entity.OrderDetail;
import com.example.lab2.entity.Product;

import java.util.Date;

public class ProductDetailActivity extends AppCompatActivity {
    private TextView tvName, tvPrice;
    private EditText etQuantity;
    private Button btnAddToCart, btnBackDetail;
    private AppDatabase db;
    private int productId;
    private Product product;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_product_detail);

        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setTitle("Chi tiết sản phẩm");
        }

        db = AppDatabase.getInstance(this);
        productId = getIntent().getIntExtra("productId", -1);
        product = db.shoppingDao().getProductById(productId);

        tvName = findViewById(R.id.tvDetailName);
        tvPrice = findViewById(R.id.tvDetailPrice);
        etQuantity = findViewById(R.id.etQuantity);
        btnAddToCart = findViewById(R.id.btnAddToCart);
        btnBackDetail = findViewById(R.id.btnBackDetail);

        if (product != null) {
            tvName.setText(product.productName);
            tvPrice.setText("$" + product.price);
        }

        btnAddToCart.setOnClickListener(v -> {
            checkLoginAndAddToCart();
        });

        btnBackDetail.setOnClickListener(v -> finish());
    }

    private void checkLoginAndAddToCart() {
        SharedPreferences pref = getSharedPreferences("UserPrefs", MODE_PRIVATE);
        int userId = pref.getInt("userId", -1);

        if (userId == -1) {
            Toast.makeText(this, "Vui lòng đăng nhập để nhặt hàng", Toast.LENGTH_SHORT).show();
            startActivity(new Intent(this, LoginActivity.class));
            return;
        }

        addToCart(userId);
    }

    private void addToCart(int userId) {
        String qtyStr = etQuantity.getText().toString();
        if (qtyStr.isEmpty()) {
            Toast.makeText(this, "Nhập số lượng", Toast.LENGTH_SHORT).show();
            return;
        }
        int qty = Integer.parseInt(qtyStr);

        // 1. Kiểm tra và Tạo hóa đơn (Order) nếu chưa có
        Order order = db.shoppingDao().getPendingOrderByUser(userId);
        if (order == null) {
            order = new Order();
            order.userId = userId;
            order.status = "Pending";
            order.orderDate = new Date().toString();
            // Lưu Order vào CSDL và lấy lại ID mới tạo
            long newOrderId = db.shoppingDao().insertOrder(order);
            order.orderId = (int) newOrderId;
        }

        // 2. Tạo hoặc cập nhật Chi tiết hóa đơn (OrderDetail)
        OrderDetail existingDetail = db.shoppingDao().getDetailByProductAndOrder(order.orderId, productId);
        
        if (existingDetail != null) {
            // Nếu đã có sản phẩm này trong giỏ, chỉ cần cộng dồn số lượng
            existingDetail.quantity += qty;
            db.shoppingDao().updateOrderDetail(existingDetail);
        } else {
            // Nếu chưa có, tạo mới Chi tiết hóa đơn
            OrderDetail detail = new OrderDetail();
            detail.orderId = order.orderId;
            detail.productId = productId;
            detail.quantity = qty;
            detail.unitPrice = product.price;
            db.shoppingDao().insertOrderDetail(detail);
        }

        Toast.makeText(this, "Đã thêm vào giỏ hàng", Toast.LENGTH_SHORT).show();
        showContinueShoppingDialog();
    }

    private void showContinueShoppingDialog() {
        new AlertDialog.Builder(this)
                .setTitle("Tiếp tục mua sắm?")
                .setMessage("Bạn có muốn tiếp tục chọn thêm hay đi thanh toán?")
                .setPositiveButton("Tiếp tục", (dialog, which) -> {
                    Intent intent = new Intent(this, MainActivity.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    startActivity(intent);
                })
                .setNegativeButton("Thanh toán", (dialog, which) -> {
                    startActivity(new Intent(this, CheckoutActivity.class));
                })
                .show();
    }

    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return true;
    }
}