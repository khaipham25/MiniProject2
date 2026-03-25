package com.example.lab2;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.lab2.adapter.CategoryAdapter;
import com.example.lab2.adapter.ProductAdapter;
import com.example.lab2.database.AppDatabase;
import com.example.lab2.entity.Category;
import com.example.lab2.entity.Product;
import com.example.lab2.entity.User;

import java.util.List;

public class MainActivity extends AppCompatActivity {
    private TextView tvWelcome;
    private Button btnLoginMain, btnViewCart;
    private RecyclerView rvProducts, rvCategories;
    private AppDatabase db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        db = AppDatabase.getInstance(this);
        tvWelcome = findViewById(R.id.tvWelcome);
        btnLoginMain = findViewById(R.id.btnLoginMain);
        btnViewCart = findViewById(R.id.btnViewCart);
        rvProducts = findViewById(R.id.rvProducts);
        rvCategories = findViewById(R.id.rvCategories);

        prefillData();

        // 1. Setup Categories (Xem Categories)
        rvCategories.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
        loadCategories();

        // 2. Setup Products (Xem danh sách Products)
        rvProducts.setLayoutManager(new LinearLayoutManager(this));
        loadProducts();

        btnViewCart.setOnClickListener(v -> {
            startActivity(new Intent(MainActivity.this, CheckoutActivity.class));
        });

        checkLoginStatus();
    }

    private void checkLoginStatus() {
        SharedPreferences pref = getSharedPreferences("UserPrefs", MODE_PRIVATE);
        int userId = pref.getInt("userId", -1);
        String fullName = pref.getString("fullName", null);

        if (userId != -1) {
            tvWelcome.setText("Xin chào, " + fullName + "!");
            btnLoginMain.setText("Đăng xuất");
            btnViewCart.setVisibility(View.VISIBLE);
            
            btnLoginMain.setOnClickListener(v -> {
                pref.edit().clear().apply();
                checkLoginStatus();
            });
        } else {
            tvWelcome.setText("Xin chào!");
            btnLoginMain.setText("Đăng nhập");
            btnViewCart.setVisibility(View.GONE);
            
            btnLoginMain.setOnClickListener(v -> {
                startActivity(new Intent(MainActivity.this, LoginActivity.class));
            });
        }
    }

    private void loadCategories() {
        List<Category> categories = db.shoppingDao().getAllCategories();
        CategoryAdapter adapter = new CategoryAdapter(categories, category -> {
            if (category.categoryName.equals("Tất cả")) {
                loadProducts();
            } else {
                // Lọc sản phẩm theo danh mục
                List<Product> filteredProducts = db.shoppingDao().getProductsByCategory(category.categoryId);
                updateProductList(filteredProducts);
            }
            Toast.makeText(this, "Danh mục: " + category.categoryName, Toast.LENGTH_SHORT).show();
        });
        rvCategories.setAdapter(adapter);
    }

    private void loadProducts() {
        List<Product> products = db.shoppingDao().getAllProducts();
        updateProductList(products);
    }

    private void updateProductList(List<Product> products) {
        ProductAdapter adapter = new ProductAdapter(products, product -> {
            // 3. Xem chi tiết Product
            Intent intent = new Intent(MainActivity.this, ProductDetailActivity.class);
            intent.putExtra("productId", product.productId);
            startActivity(intent);
        });
        rvProducts.setAdapter(adapter);
    }

    private void prefillData() {
        if (db.shoppingDao().getAllCategories().isEmpty()) {
            // Mock Categories
            String[] catNames = {"Tất cả", "Điện tử", "Gia dụng", "Thời trang", "Sách", "Thực phẩm", "Đồ chơi", "Sức khỏe", "Thể thao", "Làm đẹp", "Nội thất"};
            for (String name : catNames) {
                Category cat = new Category();
                cat.categoryName = name;
                db.shoppingDao().insertCategory(cat);
            }

            // Mock Products
            Object[][] productData = {
                // Category 2: Điện tử
                {"iPhone 15 Pro", 1099.0, 2},
                {"Samsung Galaxy S24", 899.0, 2},
                {"MacBook Pro M3", 1999.0, 2},
                {"iPad Air 5", 599.0, 2},
                {"Sony WH-1000XM5", 350.0, 2},
                {"Apple Watch Ultra", 799.0, 2},
                {"Loa JBL Flip 6", 110.0, 2},
                // Category 3: Gia dụng
                {"Nồi chiên không dầu Philips", 180.0, 3},
                {"Máy hút bụi Dyson", 450.0, 3},
                {"Máy pha cà phê", 120.0, 3},
                {"Lò vi sóng Sharp", 90.0, 3},
                {"Máy lọc không khí", 150.0, 3},
                // Category 4: Thời trang
                {"Áo sơ mi Oxford", 35.0, 4},
                {"Quần Jean Levi's", 65.0, 4},
                {"Giày Nike Air Max", 120.0, 4},
                {"Túi xách Da", 85.0, 4},
                {"Mũ bảo hiểm 3/4", 50.0, 4},
                // Category 5: Sách
                {"Lập trình Android với Java", 40.0, 5},
                {"Clean Code", 45.0, 5},
                {"Sapiens: Lược sử loài người", 20.0, 5},
                {"Kỹ năng tư duy thiết kế", 25.0, 5},
                {"Harry Potter tập 1", 18.0, 5},
                // Category 6: Thực phẩm
                {"Sữa tươi TH True Milk", 2.5, 6},
                {"Bánh mì gối", 1.8, 6},
                {"Trứng gà sạch (10 quả)", 3.0, 6},
                {"Thịt bò Mỹ 500g", 15.0, 6},
                {"Dầu ăn Neptune 1L", 2.2, 6},
                // Category 7: Đồ chơi
                {"Bộ Lego City", 55.0, 7},
                {"Búp bê Barbie", 25.0, 7},
                {"Xe điều khiển từ xa", 40.0, 7},
                // Category 8: Sức khỏe
                {"Máy đo huyết áp", 35.0, 8},
                {"Vitamin C tổng hợp", 15.0, 8},
                // Category 9: Thể thao
                {"Thảm tập Yoga", 20.0, 9},
                {"Quả bóng đá", 30.0, 9},
                {"Vợt cầu lông Yonex", 80.0, 9},
                // Category 10: Làm đẹp
                {"Sữa rửa mặt Cetaphil", 12.0, 10},
                {"Kem chống nắng La Roche-Posay", 22.0, 10},
                // Category 11: Nội thất
                {"Đèn học chống cận", 18.0, 11},
                {"Gối tựa lưng", 10.0, 11}
            };

            for (Object[] p : productData) {
                Product product = new Product();
                product.productName = (String) p[0];
                product.price = (Double) p[1];
                product.categoryId = (Integer) p[2];
                db.shoppingDao().insertProduct(product);
            }

            // Default User
            User user = new User();
            user.username = "admin";
            user.password = "123";
            user.fullName = "Quản trị viên";
            db.shoppingDao().insertUser(user);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        checkLoginStatus();
    }
}