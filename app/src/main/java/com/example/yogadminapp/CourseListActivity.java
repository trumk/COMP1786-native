package com.example.yogadminapp;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import com.example.yogadminapp.adapter.CourseAdapter;
import com.example.yogadminapp.api.ApiService;
import com.example.yogadminapp.api.RetrofitClient;
import com.example.yogadminapp.models.YogaCourse;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import java.util.List;

public class CourseListActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private CourseAdapter adapter;
    private Button btnAddCourse, btnBackToHome, btnSearch;
    private EditText etSearch;
    private Spinner spinnerDayOfWeek;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_course_list);

        recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        btnBackToHome = findViewById(R.id.btnBackToHome);
        btnBackToHome.setOnClickListener(v -> {
            Intent intent = new Intent(CourseListActivity.this, AdminHomeActivity.class);
            startActivity(intent);
            finish();
        });

        btnAddCourse = findViewById(R.id.btnAddCourse);
        btnAddCourse.setOnClickListener(v -> {
            Intent intent = new Intent(CourseListActivity.this, CourseFormActivity.class);
            startActivity(intent);
        });

        etSearch = findViewById(R.id.etSearch);
        btnSearch = findViewById(R.id.btnSearch);
        btnSearch.setOnClickListener(v -> searchCourses());

        // Setup Spinner for days of the week
        spinnerDayOfWeek = findViewById(R.id.spinnerDayOfWeek);
        setupDayOfWeekSpinner();

        loadCourses();
    }

    private void setupDayOfWeekSpinner() {
        String[] daysOfWeek = {"Choose day","Monday", "Tuesday", "Wednesday", "Thursday", "Friday", "Saturday", "Sunday"};
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, daysOfWeek);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerDayOfWeek.setAdapter(adapter);
    }

    private void loadCourses() {
        ApiService apiService = RetrofitClient.getApiService();
        apiService.getAllCourses().enqueue(new Callback<List<YogaCourse>>() {
            @Override
            public void onResponse(Call<List<YogaCourse>> call, Response<List<YogaCourse>> response) {
                if (response.isSuccessful()) {
                    List<YogaCourse> courses = response.body();
                    Log.d("CourseListActivity", "Number of courses: " + (courses != null ? courses.size() : 0));
                    adapter = new CourseAdapter(courses, CourseListActivity.this);
                    recyclerView.setAdapter(adapter);
                } else {
                    Log.e("CourseListActivity", "Failed to load courses: " + response.message());
                    Toast.makeText(CourseListActivity.this, "Failed to load courses", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<List<YogaCourse>> call, Throwable t) {
                Log.e("CourseListActivity", "Error: " + t.getMessage());
                Toast.makeText(CourseListActivity.this, "Error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void searchCourses() {
        String query = etSearch.getText().toString().trim();
        String dayOfWeek = spinnerDayOfWeek.getSelectedItem().toString(); // Get selected day

        // Log thông tin tìm kiếm
        Log.d("CourseListActivity", "Searching for: " + query + ", Day: " + dayOfWeek);

        ApiService apiService = RetrofitClient.getApiService();
        boolean isDaySelected = !dayOfWeek.equals("Choose day");

        if (query.isEmpty() && !isDaySelected) {
            Toast.makeText(this, "Please enter a search term or select a day", Toast.LENGTH_SHORT).show();
            return;
        }

        if (isDaySelected) {
            // Nếu ngày được chọn, gọi API tìm kiếm với từ khóa và ngày
            apiService.searchCourses(query.isEmpty() ? null : query, dayOfWeek).enqueue(new Callback<List<YogaCourse>>() {
                @Override
                public void onResponse(Call<List<YogaCourse>> call, Response<List<YogaCourse>> response) {
                    if (response.isSuccessful()) {
                        List<YogaCourse> courses = response.body();
                        // Log kết quả tìm kiếm
                        Log.d("CourseListActivity", "Search results: " + (courses != null ? courses.size() : 0));
                        adapter = new CourseAdapter(courses, CourseListActivity.this);
                        recyclerView.setAdapter(adapter);
                    } else {
                        Log.e("CourseListActivity", "Failed to search courses: " + response.message());
                        Toast.makeText(CourseListActivity.this, "Failed to search courses", Toast.LENGTH_SHORT).show();
                    }
                }

                @Override
                public void onFailure(Call<List<YogaCourse>> call, Throwable t) {
                    Log.e("CourseListActivity", "Error: " + t.getMessage());
                    Toast.makeText(CourseListActivity.this, "Error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
                }
            });
        } else {
            // Nếu không có ngày cụ thể, chỉ tìm kiếm theo từ khóa
            apiService.searchCourses(query.isEmpty() ? null : query, null).enqueue(new Callback<List<YogaCourse>>() {
                @Override
                public void onResponse(Call<List<YogaCourse>> call, Response<List<YogaCourse>> response) {
                    if (response.isSuccessful()) {
                        List<YogaCourse> courses = response.body();
                        // Log kết quả tìm kiếm
                        Log.d("CourseListActivity", "Search results: " + (courses != null ? courses.size() : 0));
                        adapter = new CourseAdapter(courses, CourseListActivity.this);
                        recyclerView.setAdapter(adapter);
                    } else {
                        Log.e("CourseListActivity", "Failed to search courses: " + response.message());
                        Toast.makeText(CourseListActivity.this, "Failed to search courses", Toast.LENGTH_SHORT).show();
                    }
                }

                @Override
                public void onFailure(Call<List<YogaCourse>> call, Throwable t) {
                    Log.e("CourseListActivity", "Error: " + t.getMessage());
                    Toast.makeText(CourseListActivity.this, "Error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
                }
            });
        }
    }


    @Override
    protected void onResume() {
        super.onResume();
        loadCourses();
    }
}
