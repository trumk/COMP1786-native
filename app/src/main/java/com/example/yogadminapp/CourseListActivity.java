package com.example.yogadminapp;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
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
    private Button btnAddCourse, btnBackToHome;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_course_list);

        recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        btnBackToHome = findViewById(R.id.btnBackToHome);
        btnBackToHome.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(CourseListActivity.this, AdminHomeActivity.class);
                startActivity(intent);
                finish();
            }
        });

        btnAddCourse = findViewById(R.id.btnAddCourse);
        btnAddCourse.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(CourseListActivity.this, CourseFormActivity.class);
                startActivity(intent);
            }
        });

        loadCourses();
    }

    private void loadCourses() {
        ApiService apiService = RetrofitClient.getApiService();
        apiService.getAllCourses().enqueue(new Callback<List<YogaCourse>>() {
            @Override
            public void onResponse(Call<List<YogaCourse>> call, Response<List<YogaCourse>> response) {
                if (response.isSuccessful()) {
                    List<YogaCourse> courses = response.body();
                    Log.d("CourseListActivity", "Number of courses: " + (courses != null ? courses.size() : 0));
                    if (courses != null) {
                        for (YogaCourse course : courses) {
                            Log.d("CourseListActivity", "Course: " + course.toString());
                        }
                    }
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
    @Override
    protected void onResume() {
        super.onResume();
        loadCourses();
    }


}
