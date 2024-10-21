package com.example.yogadminapp;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.example.yogadminapp.models.Order;

public class OrderDetailActivity extends AppCompatActivity {
    private TextView tvOrderId, tvStatus, tvTotalAmount, tvCreatedAt;
    private Button btnBackToHome;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order_detail);

        tvOrderId = findViewById(R.id.tvOrderId);
        tvStatus = findViewById(R.id.tvStatus);
        tvTotalAmount = findViewById(R.id.tvTotalAmount);
        tvCreatedAt = findViewById(R.id.tvCreatedAt);
        btnBackToHome = findViewById(R.id.btnBackToHome);

        // Nhận dữ liệu từ Intent
        Intent intent = getIntent();
        if (intent != null && intent.hasExtra("order")) {
            Order order = (Order) intent.getSerializableExtra("order");
            if (order != null) {
                tvOrderId.setText(order.getId());
                tvStatus.setText(order.getStatus());
                tvTotalAmount.setText(String.valueOf(order.getTotalAmount()));
                tvCreatedAt.setText(order.getCreatedAt().toString());
            }
        }

        // Xử lý sự kiện bấm nút quay về Home
        btnBackToHome.setOnClickListener(v -> {
            Intent homeIntent = new Intent(OrderDetailActivity.this, OrderListActivity.class);
            homeIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
            startActivity(homeIntent);
            finish();
        });
    }
}
