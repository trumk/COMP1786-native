package com.example.yogadminapp;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import com.example.yogadminapp.api.ApiService;
import com.example.yogadminapp.api.RetrofitClient;
import com.example.yogadminapp.models.ClassType;
import com.example.yogadminapp.models.YogaCourse;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class CourseFormActivity extends AppCompatActivity {
    private EditText etDayOfWeek, etCapacity, etPricePerClass, etLocation;
    private Button btnSave, btnBackToList, btnSelectClasses;
    private String courseId;
    private String selectedDayOfWeek = null;
    private List<ClassType> classTypesList = new ArrayList<>();
    private List<ClassType> selectedClassTypes = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_course_form);

        etDayOfWeek = findViewById(R.id.etDayOfWeek);
        etCapacity = findViewById(R.id.etCapacity);
        etPricePerClass = findViewById(R.id.etPricePerClass);
        etLocation = findViewById(R.id.etLocation);
        btnSave = findViewById(R.id.btnSave);
        btnBackToList = findViewById(R.id.btnBackToList);
        btnSelectClasses = findViewById(R.id.btnSelectClasses);

        if (getIntent().hasExtra("courseId")) {
            courseId = getIntent().getStringExtra("courseId");
            loadCourseDetails(courseId);
        }

        etDayOfWeek.setOnClickListener(v -> selectDayOfWeek());
        btnSelectClasses.setOnClickListener(v -> selectClasses());

        btnBackToList.setOnClickListener(v -> finish());

        btnSave.setOnClickListener(v -> {
            if (courseId == null) {
                addCourse();
            } else {
                updateCourse();
            }
        });

        loadClassTypes();
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (courseId != null) {
            loadCourseDetails(courseId);
        }
        loadClassTypes();
    }

    private void selectDayOfWeek() {
        String[] allDaysOfWeek = {"Sunday", "Monday", "Tuesday", "Wednesday", "Thursday", "Friday", "Saturday"};
        int today = Calendar.getInstance().get(Calendar.DAY_OF_WEEK) - 1;

        String[] sortedDaysOfWeek = new String[7];
        for (int i = 0; i < 7; i++) {
            sortedDaysOfWeek[i] = allDaysOfWeek[(today + i) % 7];
        }

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Select Day of the Week");
        builder.setItems(sortedDaysOfWeek, (dialog, which) -> {
            etDayOfWeek.setText(sortedDaysOfWeek[which]);
            selectedDayOfWeek = sortedDaysOfWeek[which];
        });
        builder.show();
    }

    private void selectClasses() {
        if (selectedDayOfWeek == null) {
            Toast.makeText(this, "Please select a day of the week first.", Toast.LENGTH_SHORT).show();
            return;
        }

        // Lọc danh sách lớp học theo ngày đã chọn
        List<ClassType> filteredClassTypes = new ArrayList<>();
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.getDefault());
        SimpleDateFormat dayFormat = new SimpleDateFormat("EEEE", Locale.getDefault());

        for (ClassType classType : classTypesList) {
            try {
                // Chuyển đổi trường date của lớp học sang ngày trong tuần
                Date classDate = dateFormat.parse(classType.getDate());
                Calendar classCalendar = Calendar.getInstance();
                classCalendar.setTime(classDate);
                String classDayOfWeek = dayFormat.format(classCalendar.getTime());

                // So sánh với ngày đã chọn
                if (classDayOfWeek.equalsIgnoreCase(selectedDayOfWeek)) {
                    filteredClassTypes.add(classType);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        if (filteredClassTypes.isEmpty()) {
            Toast.makeText(this, "No classes available for the selected day.", Toast.LENGTH_SHORT).show();
            return;
        }

        // Tạo danh sách các tên lớp học để hiển thị trong hộp thoại
        CharSequence[] items = new CharSequence[filteredClassTypes.size()];
        boolean[] selectedItems = new boolean[filteredClassTypes.size()];
        for (int i = 0; i < filteredClassTypes.size(); i++) {
            items[i] = filteredClassTypes.get(i).getTypeName();
            selectedItems[i] = selectedClassTypes.contains(filteredClassTypes.get(i));
        }

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Select Classes")
                .setMultiChoiceItems(items, selectedItems, (dialog, which, isChecked) -> {
                    if (isChecked) {
                        selectedClassTypes.add(filteredClassTypes.get(which));
                    } else {
                        selectedClassTypes.remove(filteredClassTypes.get(which));
                    }
                })
                .setPositiveButton("OK", null)
                .show();
    }

    private void loadClassTypes() {
        ApiService apiService = RetrofitClient.getApiService();
        apiService.getAllClassTypes().enqueue(new Callback<List<ClassType>>() {
            @Override
            public void onResponse(Call<List<ClassType>> call, Response<List<ClassType>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    classTypesList = response.body();
                    Log.d("CourseFormActivity", "Class types loaded: " + classTypesList.size());
                } else {
                    Log.e("CourseFormActivity", "Failed to load class types: " + response.message());
                }
            }

            @Override
            public void onFailure(Call<List<ClassType>> call, Throwable t) {
                Log.e("CourseFormActivity", "Error: " + t.getMessage());
            }
        });
    }

    private void loadCourseDetails(String courseId) {
        ApiService apiService = RetrofitClient.getApiService();
        apiService.getCourseById(courseId).enqueue(new Callback<YogaCourse>() {
            @Override
            public void onResponse(Call<YogaCourse> call, Response<YogaCourse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    YogaCourse course = response.body();
                    etDayOfWeek.setText(course.getDayOfWeek());
                    etCapacity.setText(String.valueOf(course.getCapacity()));
                    etPricePerClass.setText(String.valueOf(course.getPricePerClass()));
                    etLocation.setText(course.getLocation());

                    selectedDayOfWeek = course.getDayOfWeek();

                    List<ClassType> classTypes = course.getClassTypes();
                    if (classTypes != null && !classTypes.isEmpty()) {
                        selectedClassTypes = classTypes;
                    } else {
                        selectedClassTypes.clear();
                    }

                    Log.d("CourseFormActivity", "Class types loaded: " + selectedClassTypes.size());
                } else {
                    Log.e("CourseFormActivity", "Failed to load course details: " + response.message());
                    Toast.makeText(CourseFormActivity.this, "Failed to load course details", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<YogaCourse> call, Throwable t) {
                Log.e("CourseFormActivity", "Error: " + t.getMessage());
                Toast.makeText(CourseFormActivity.this, "Error loading course details", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void addCourse() {
        YogaCourse newCourse = new YogaCourse(
                null,
                selectedDayOfWeek,
                Integer.parseInt(etCapacity.getText().toString().trim()),
                Double.parseDouble(etPricePerClass.getText().toString().trim()),
                selectedClassTypes, // Sử dụng danh sách các đối tượng ClassType
                etLocation.getText().toString().trim(),
                null
        );

        ApiService apiService = RetrofitClient.getApiService();
        apiService.createCourse(newCourse).enqueue(new Callback<YogaCourse>() {
            @Override
            public void onResponse(Call<YogaCourse> call, Response<YogaCourse> response) {
                if (response.isSuccessful()) {
                    Toast.makeText(CourseFormActivity.this, "Course added successfully", Toast.LENGTH_SHORT).show();
                    finish();
                } else {
                    try {
                        String errorBody = response.errorBody() != null ? response.errorBody().string() : "Unknown error";
                        Log.e("CourseFormActivity", "Failed to add course: " + response.message() + ", Error body: " + errorBody);
                        Toast.makeText(CourseFormActivity.this, errorBody, Toast.LENGTH_LONG).show();
                    } catch (Exception e) {
                        Log.e("CourseFormActivity", "Failed to add course: " + response.message());
                    }
                }
            }

            @Override
            public void onFailure(Call<YogaCourse> call, Throwable t) {
                Log.e("CourseFormActivity", "Error: " + t.getMessage());
                Toast.makeText(CourseFormActivity.this, "Error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void updateCourse() {
        YogaCourse updatedCourse = new YogaCourse(
                courseId,
                selectedDayOfWeek,
                Integer.parseInt(etCapacity.getText().toString().trim()),
                Double.parseDouble(etPricePerClass.getText().toString().trim()),
                selectedClassTypes, // Sử dụng danh sách các đối tượng ClassType
                etLocation.getText().toString().trim(),
                null
        );

        ApiService apiService = RetrofitClient.getApiService();
        apiService.updateCourse(courseId, updatedCourse).enqueue(new Callback<YogaCourse>() {
            @Override
            public void onResponse(Call<YogaCourse> call, Response<YogaCourse> response) {
                if (response.isSuccessful()) {
                    Toast.makeText(CourseFormActivity.this, "Course updated successfully", Toast.LENGTH_SHORT).show();
                    finish();
                } else {
                    Log.e("CourseFormActivity", "Failed to update course: " + response.message());
                    if (response.errorBody() != null) {
                        try {
                            String errorBody = response.errorBody().string();
                            Log.e("CourseFormActivity", "Error body: " + errorBody);
                            Toast.makeText(CourseFormActivity.this, errorBody, Toast.LENGTH_LONG).show(); // Hiển thị thông báo lỗi cụ thể
                        } catch (Exception e) {
                            Log.e("CourseFormActivity", "Failed to read error body");
                        }
                    }
                }
            }

            @Override
            public void onFailure(Call<YogaCourse> call, Throwable t) {
                Log.e("CourseFormActivity", "Error: " + t.getMessage());
            }
        });
    }

}
