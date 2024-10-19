package com.example.yogadminapp;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.yogadminapp.api.ApiService;
import com.example.yogadminapp.api.RetrofitClient;
import com.example.yogadminapp.models.ClassType;
import com.example.yogadminapp.models.User;
import com.example.yogadminapp.models.YogaCourse;
import com.google.android.material.card.MaterialCardView;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class AdminHomeActivity extends AppCompatActivity {

    private Button btnManageCourses;
    private Button btnManageClasses;
    private TextView tvUserCount;
    private TextView tvClassCount;
    private TextView tvCourseCount;
    private MaterialCardView cardUsers; // Khai báo cardUsers

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_home);

        btnManageCourses = findViewById(R.id.btnManageCourses);
        btnManageClasses = findViewById(R.id.btnManageClasses);
        tvUserCount = findViewById(R.id.tvUserCount);
        tvClassCount = findViewById(R.id.tvClassCount);
        tvCourseCount = findViewById(R.id.tvCourseCount);
        cardUsers = findViewById(R.id.cardUsers); // Khởi tạo cardUsers

        if (cardUsers != null) {
            cardUsers.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(AdminHomeActivity.this, UserListActivity.class);
                    startActivity(intent);
                }
            });
        } else {
            Log.e("AdminHomeActivity", "cardUsers is null");
        }

        btnManageCourses.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(AdminHomeActivity.this, CourseListActivity.class);
                startActivity(intent);
            }
        });

        btnManageClasses.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(AdminHomeActivity.this, ClassListActivity.class);
                startActivity(intent);
            }
        });

        loadUserCount();
        loadClassCount();
        loadCourseCount();
    }

    private void loadUserCount() {
        ApiService apiService = RetrofitClient.getApiService();
        apiService.getAllUsers().enqueue(new Callback<List<User>>() {
            @Override
            public void onResponse(@NonNull Call<List<User>> call, @NonNull Response<List<User>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    int userCount = response.body().size();
                    tvUserCount.setText(String.valueOf(userCount));
                } else {
                    Log.e("AdminHomeActivity", "Failed to get users: " + response.message());
                    Toast.makeText(AdminHomeActivity.this, "Failed to get user count", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(@NonNull Call<List<User>> call, @NonNull Throwable t) {
                Log.e("AdminHomeActivity", "Error: " + t.getMessage());
                Toast.makeText(AdminHomeActivity.this, "Error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void loadClassCount() {
        ApiService apiService = RetrofitClient.getApiService();
        apiService.getAllClassTypes().enqueue(new Callback<List<ClassType>>() {
            @Override
            public void onResponse(@NonNull Call<List<ClassType>> call, @NonNull Response<List<ClassType>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    int classCount = response.body().size();
                    tvClassCount.setText(String.valueOf(classCount));
                } else {
                    Log.e("AdminHomeActivity", "Failed to get class types: " + response.message());
                    Toast.makeText(AdminHomeActivity.this, "Failed to get class count", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(@NonNull Call<List<ClassType>> call, @NonNull Throwable t) {
                Log.e("AdminHomeActivity", "Error: " + t.getMessage());
                Toast.makeText(AdminHomeActivity.this, "Error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void loadCourseCount() {
        ApiService apiService = RetrofitClient.getApiService();
        apiService.getAllCourses().enqueue(new Callback<List<YogaCourse>>() {
            @Override
            public void onResponse(@NonNull Call<List<YogaCourse>> call, @NonNull Response<List<YogaCourse>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    int courseCount = response.body().size();
                    tvCourseCount.setText(String.valueOf(courseCount));
                } else {
                    Log.e("AdminHomeActivity", "Failed to get courses: " + response.message());
                    Toast.makeText(AdminHomeActivity.this, "Failed to get course count", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(@NonNull Call<List<YogaCourse>> call, @NonNull Throwable t) {
                Log.e("AdminHomeActivity", "Error: " + t.getMessage());
                Toast.makeText(AdminHomeActivity.this, "Error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
}
