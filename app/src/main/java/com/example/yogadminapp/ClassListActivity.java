package com.example.yogadminapp;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
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
    private Button btnBackToHome, btnAddClass;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_class_list);

        recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        btnBackToHome = findViewById(R.id.btnBackToHome);
        btnBackToHome.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ClassListActivity.this, AdminHomeActivity.class);
                startActivity(intent);
                finish();
            }
        });

        // Thêm nút "Add Class"
        btnAddClass = findViewById(R.id.btnAddClass);
        btnAddClass.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ClassListActivity.this, ClassFormActivity.class);
                startActivity(intent);
            }
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
                    Log.d("ClassListActivity", "Response body: " + response.body());
                    if (classTypes != null && !classTypes.isEmpty()) {
                        Log.d("ClassListActivity", "Number of ClassTypes: " + classTypes.size());
                        adapter = new ClassAdapter(classTypes, new ClassAdapter.OnItemClickListener() {
                            @Override
                            public void onItemClick(ClassType classType) {
                                Intent intent = new Intent(ClassListActivity.this, ClassFormActivity.class);
                                intent.putExtra("classTypeId", classType.getId());
                                startActivity(intent);
                            }

                            @Override
                            public void onEditClick(ClassType classType) {
                                Intent intent = new Intent(ClassListActivity.this, ClassFormActivity.class);
                                intent.putExtra("classTypeId", classType.getId());
                                startActivity(intent);
                            }

                            @Override
                            public void onDeleteClick(ClassType classType) {
                                new AlertDialog.Builder(ClassListActivity.this)
                                        .setTitle("Delete Confirmation")
                                        .setMessage("Are you sure you want to delete this class?")
                                        .setPositiveButton("Yes", (dialog, which) -> {
                                            ApiService apiService = RetrofitClient.getApiService();
                                            apiService.deleteClassType(classType.getId()).enqueue(new Callback<Void>() {
                                                @Override
                                                public void onResponse(@NonNull Call<Void> call, @NonNull Response<Void> response) {
                                                    if (response.isSuccessful()) {
                                                        loadClassTypes();
                                                        Toast.makeText(ClassListActivity.this, "Class deleted successfully", Toast.LENGTH_SHORT).show();
                                                    } else {
                                                        Log.e("ClassListActivity", "Failed to delete class: " + response.message());
                                                        Toast.makeText(ClassListActivity.this, "Failed to delete class", Toast.LENGTH_SHORT).show();
                                                    }
                                                }

                                                @Override
                                                public void onFailure(@NonNull Call<Void> call, @NonNull Throwable t) {
                                                    Log.e("ClassListActivity", "Error: " + t.getMessage());
                                                    Toast.makeText(ClassListActivity.this, "Error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
                                                }
                                            });
                                        })
                                        .setNegativeButton("No", (dialog, which) -> dialog.dismiss())
                                        .show();
                            }
                        });
                        recyclerView.setAdapter(adapter);
                    } else {
                        Log.e("ClassListActivity", "ClassTypes list is empty or null");
                    }
                } else {
                    Log.e("ClassListActivity", "Failed to load class types: " + response.message());
                    Log.e("ClassListActivity", "Response code: " + response.code());
                }
            }

            @Override
            public void onFailure(@NonNull Call<List<ClassType>> call, @NonNull Throwable t) {
                Log.e("ClassListActivity", "Error: " + t.getMessage());
            }

        });
    }
}
