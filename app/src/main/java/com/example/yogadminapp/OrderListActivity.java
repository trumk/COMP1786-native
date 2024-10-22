package com.example.yogadminapp;

import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.Toast;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.example.yogadminapp.adapter.OrderAdapter;
import com.example.yogadminapp.api.ApiService;
import com.example.yogadminapp.api.RetrofitClient;
import com.example.yogadminapp.models.Order;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import android.content.Intent;

public class OrderListActivity extends AppCompatActivity {
    private RecyclerView recyclerView;
    private OrderAdapter orderAdapter;
    private Button btnBackToHome;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order_list);

        btnBackToHome = findViewById(R.id.btnBackToHome);
        recyclerView = findViewById(R.id.recyclerViewOrders);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        btnBackToHome.setOnClickListener(v -> finish());

        loadOrders();
    }

    private void loadOrders() {
        ApiService apiService = RetrofitClient.getApiService();
        apiService.getOrders().enqueue(new Callback<List<Order>>() {
            @Override
            public void onResponse(Call<List<Order>> call, Response<List<Order>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    List<Order> orders = response.body();
                    Log.d("OrderListActivity", "Orders: " + orders.toString());

                    orderAdapter = new OrderAdapter(OrderListActivity.this, orders, order -> {
                        Log.d("OrderListActivity", "Selected Order ID: " + order.getId());
                        Intent intent = new Intent(OrderListActivity.this, OrderDetailActivity.class);
                        intent.putExtra("order", order);
                        startActivity(intent);
                    });

                    recyclerView.setAdapter(orderAdapter);
                } else {
                    Log.e("OrderListActivity", "Failed to load orders: " + response.message());
                    Toast.makeText(OrderListActivity.this, "Failed to load orders: " + response.message(), Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<List<Order>> call, Throwable t) {
                Log.e("OrderListActivity", "Error: " + t.getMessage());
                Toast.makeText(OrderListActivity.this, "Error loading orders", Toast.LENGTH_SHORT).show();
            }
        });
    }

    public String formatDate(Date date) {
        SimpleDateFormat dateFormat = new SimpleDateFormat("EEEE, d/MM/yyyy HH:mm", Locale.getDefault());
        return dateFormat.format(date);
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadOrders();
    }
}
