package com.example.yogadminapp;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.example.yogadminapp.adapter.ClassAdapter;
import com.example.yogadminapp.api.ApiService;
import com.example.yogadminapp.api.RetrofitClient;
import com.example.yogadminapp.models.ClassType;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import java.util.List;

public class ClassListActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private ClassAdapter adapter;
    private Button btnBackToHome;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_class_list);

        recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        btnBackToHome = findViewById(R.id.btnBackToHome);
        btnBackToHome.setOnClickListener(v -> {
            Intent intent = new Intent(ClassListActivity.this, AdminHomeActivity.class);
            startActivity(intent);
            finish();
        });

        loadClassTypes();
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadClassTypes();
    }

    private void loadClassTypes() {
        ApiService apiService = RetrofitClient.getApiService();
        apiService.getAllClassTypes().enqueue(new Callback<List<ClassType>>() {
            @Override
            public void onResponse(@NonNull Call<List<ClassType>> call, @NonNull Response<List<ClassType>> response) {
                if (response.isSuccessful()) {
                    List<ClassType> classTypes = response.body();
                    if (classTypes != null && !classTypes.isEmpty()) {
                        Log.d("ClassListActivity", "Number of ClassTypes: " + classTypes.size());
                        adapter = new ClassAdapter(classTypes, null); // No click listener provided
                        recyclerView.setAdapter(adapter);
                    } else {
                        Log.e("ClassListActivity", "ClassTypes list is empty or null");
                    }
                } else {
                    Log.e("ClassListActivity", "Failed to load class types: " + response.message());
                    Toast.makeText(ClassListActivity.this, "Failed to load class types", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(@NonNull Call<List<ClassType>> call, @NonNull Throwable t) {
                Log.e("ClassListActivity", "Error: " + t.getMessage());
                Toast.makeText(ClassListActivity.this, "Error loading class types", Toast.LENGTH_SHORT).show();
            }
        });
    }
}
