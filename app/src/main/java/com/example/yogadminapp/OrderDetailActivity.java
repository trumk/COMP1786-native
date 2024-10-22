package com.example.yogadminapp;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import android.content.Intent;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.example.yogadminapp.api.ApiService;
import com.example.yogadminapp.api.RetrofitClient;
import com.example.yogadminapp.models.Order;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class OrderDetailActivity extends AppCompatActivity {
    private TextView tvOrderId, tvUserId, tvTotalAmount, tvCreatedAt;
    private Spinner spinnerStatus;
    private Button btnBackToHome, btnUpdateStatus;
    private Order currentOrder;
    private ApiService apiService;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order_detail);

        tvOrderId = findViewById(R.id.tvOrderId);
        tvUserId = findViewById(R.id.tvUserId);
        spinnerStatus = findViewById(R.id.spinnerStatus);
        tvTotalAmount = findViewById(R.id.tvTotalAmount);
        tvCreatedAt = findViewById(R.id.tvCreatedAt);
        btnBackToHome = findViewById(R.id.btnBackToHome);
        btnUpdateStatus = findViewById(R.id.btnUpdateStatus);

        apiService = RetrofitClient.getApiService();

        if (getIntent() != null && getIntent().hasExtra("order")) {
            currentOrder = (Order) getIntent().getSerializableExtra("order");
            if (currentOrder != null) {
                Log.d("OrderDetailActivity", "Received Order ID: " + currentOrder.getId());
                tvOrderId.setText("Order ID: " + currentOrder.getId());
                tvUserId.setText("User ID: " + currentOrder.getUser());
                tvTotalAmount.setText("Total Amount: $" + currentOrder.getTotalAmount());

                String formattedDate = formatDate(currentOrder.getCreatedAt());
                tvCreatedAt.setText("Created At: " + formattedDate);

                ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                        R.array.order_status_array, android.R.layout.simple_spinner_item);
                adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                spinnerStatus.setAdapter(adapter);

                int spinnerPosition = adapter.getPosition(currentOrder.getStatus());
                spinnerStatus.setSelection(spinnerPosition);
            }
        }

        btnBackToHome.setOnClickListener(v -> {
            finish();
        });

        btnUpdateStatus.setOnClickListener(v -> {
            if (currentOrder != null) {
                updateOrderStatus();
            }
        });
    }

    private String formatDate(Date date) {
        SimpleDateFormat dateFormat = new SimpleDateFormat("EEEE, d/MM/yyyy HH:mm", Locale.getDefault());
        return dateFormat.format(date);
    }

    private void updateOrderStatus() {
        String selectedStatus = spinnerStatus.getSelectedItem().toString();
        Order.UpdateOrderRequest request = new Order.UpdateOrderRequest(currentOrder.getId(), selectedStatus);

        Call<Order> call = apiService.updateOrderStatus(request);
        call.enqueue(new Callback<Order>() {
            @Override
            public void onResponse(Call<Order> call, Response<Order> response) {
                if (response.isSuccessful() && response.body() != null) {
                    currentOrder = response.body();
                    Toast.makeText(OrderDetailActivity.this, "Status updated successfully", Toast.LENGTH_SHORT).show();

                    Intent intent = new Intent(OrderDetailActivity.this, OrderListActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
                    startActivity(intent);
                    finish();
                } else {
                    Log.e("OrderDetailActivity", "Failed to update status: " + response.message());
                    Log.e("OrderDetailActivity", "Response code: " + response.code());
                    if (response.errorBody() != null) {
                        try {
                            Log.e("OrderDetailActivity", "Error body: " + response.errorBody().string());
                        } catch (Exception e) {
                            Log.e("OrderDetailActivity", "Failed to read error body", e);
                        }
                    }
                    Toast.makeText(OrderDetailActivity.this, "Failed to update status", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<Order> call, Throwable t) {
                Log.e("OrderDetailActivity", "Error: " + t.getMessage(), t);
                Toast.makeText(OrderDetailActivity.this, "Error updating status", Toast.LENGTH_SHORT).show();
            }
        });
    }
}

