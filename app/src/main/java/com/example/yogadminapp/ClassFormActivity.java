package com.example.yogadminapp;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.example.yogadminapp.api.ApiService;
import com.example.yogadminapp.api.RetrofitClient;
import com.example.yogadminapp.models.ClassType;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ClassFormActivity extends AppCompatActivity {
    private EditText etClassName;
    private Button btnSave, btnBackToList;
    private String classId = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_class_form);

        etClassName = findViewById(R.id.etClassName);
        btnSave = findViewById(R.id.btnSave);
        btnBackToList = findViewById(R.id.btnBackToList);

        // Kiểm tra xem có truyền id của lớp học qua Intent không
        if (getIntent().hasExtra("classTypeId")) {
            classId = getIntent().getStringExtra("classTypeId");
            loadClassDetails(classId); // Nếu có, tải chi tiết lớp học
        }

        // Xử lý sự kiện nút quay lại danh sách
        btnBackToList.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish(); // Trở về danh sách các lớp
            }
        });

        // Xử lý sự kiện nút lưu lớp yoga mới hoặc chỉnh sửa
        btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (classId == null) {
                    saveClass(); // Thêm mới lớp học
                } else {
                    updateClass(); // Cập nhật lớp học
                }
            }
        });
    }

    private void loadClassDetails(String id) {
        ApiService apiService = RetrofitClient.getApiService();
        apiService.getClassTypeById(id).enqueue(new Callback<ClassType>() {
            @Override
            public void onResponse(Call<ClassType> call, Response<ClassType> response) {
                if (response.isSuccessful() && response.body() != null) {
                    // Hiển thị thông tin lớp học trên form
                    etClassName.setText(response.body().getTypeName());
                } else {
                    Toast.makeText(ClassFormActivity.this, "Failed to load class details", Toast.LENGTH_SHORT).show();
                    Log.e("ClassFormActivity", "Failed to load class details: " + response.message());
                }
            }

            @Override
            public void onFailure(Call<ClassType> call, Throwable t) {
                Toast.makeText(ClassFormActivity.this, "Error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
                Log.e("ClassFormActivity", "Error: " + t.getMessage());
            }
        });
    }

    private void saveClass() {
        String className = etClassName.getText().toString().trim();

        if (className.isEmpty()) {
            Toast.makeText(ClassFormActivity.this, "Class name is required", Toast.LENGTH_SHORT).show();
            return;
        }

        ClassType newClass = new ClassType(null, className);

        ApiService apiService = RetrofitClient.getApiService();
        apiService.createClassType(newClass).enqueue(new Callback<ClassType>() {
            @Override
            public void onResponse(Call<ClassType> call, Response<ClassType> response) {
                if (response.isSuccessful()) {
                    Toast.makeText(ClassFormActivity.this, "Class added successfully", Toast.LENGTH_SHORT).show();
                    finish(); // Trở về danh sách các lớp
                } else {
                    Log.e("ClassFormActivity", "Failed to add class: " + response.message());
                    Toast.makeText(ClassFormActivity.this, "Failed to add class", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<ClassType> call, Throwable t) {
                Log.e("ClassFormActivity", "Error: " + t.getMessage());
                Toast.makeText(ClassFormActivity.this, "Error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void updateClass() {
        String className = etClassName.getText().toString().trim();

        if (className.isEmpty()) {
            Toast.makeText(ClassFormActivity.this, "Class name is required", Toast.LENGTH_SHORT).show();
            return;
        }

        ClassType updatedClass = new ClassType(classId, className);

        ApiService apiService = RetrofitClient.getApiService();
        apiService.updateClassType(classId, updatedClass).enqueue(new Callback<ClassType>() {
            @Override
            public void onResponse(Call<ClassType> call, Response<ClassType> response) {
                if (response.isSuccessful()) {
                    Toast.makeText(ClassFormActivity.this, "Class updated successfully", Toast.LENGTH_SHORT).show();
                    finish(); // Trở về danh sách các lớp
                } else {
                    Log.e("ClassFormActivity", "Failed to update class: " + response.message());
                    Toast.makeText(ClassFormActivity.this, "Failed to update class", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<ClassType> call, Throwable t) {
                Log.e("ClassFormActivity", "Error: " + t.getMessage());
                Toast.makeText(ClassFormActivity.this, "Error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
}
