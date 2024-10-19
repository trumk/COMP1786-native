package com.example.yogadminapp;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.*;

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
import java.util.*;

public class CourseFormActivity extends AppCompatActivity {
    private EditText etDayOfWeek, etCapacity, etPricePerClass, etTeacherName, etLocation, etDescription;
    private TimePicker timePickerCourseTime;
    private Spinner spinnerClassType, spinnerDuration;
    private Button btnSave, btnBackToList;
    private String courseId;
    private List<ClassType> classTypesList = new ArrayList<>();
    private Calendar selectedDate = Calendar.getInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_course_form);

        etDayOfWeek = findViewById(R.id.etDayOfWeek);
        timePickerCourseTime = findViewById(R.id.timePickerCourseTime);
        etCapacity = findViewById(R.id.etCapacity);
        spinnerDuration = findViewById(R.id.spinnerDuration);
        etPricePerClass = findViewById(R.id.etPricePerClass);
        etTeacherName = findViewById(R.id.etTeacherName);
        etLocation = findViewById(R.id.etLocation);
        etDescription = findViewById(R.id.etDescription);
        btnSave = findViewById(R.id.btnSave);
        btnBackToList = findViewById(R.id.btnBackToList);
        spinnerClassType = findViewById(R.id.spinnerClassType);

        timePickerCourseTime.setIs24HourView(true);

        loadClassTypes();

        ArrayAdapter<CharSequence> durationAdapter = ArrayAdapter.createFromResource(this,
                R.array.duration_array, android.R.layout.simple_spinner_item);
        durationAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerDuration.setAdapter(durationAdapter);

        courseId = getIntent().getStringExtra("courseId");
        if (courseId != null) {
            loadCourseDetails(courseId);
        }

        // Chọn ngày của tuần
        etDayOfWeek.setOnClickListener(v -> selectDayOfWeek());

        // Sự kiện nút quay lại
        btnBackToList.setOnClickListener(v -> finish());

        // Sự kiện nút lưu
        btnSave.setOnClickListener(v -> {
            if (courseId != null) {
                updateCourse();
            } else {
                addCourse();
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
                    spinnerDuration.setSelection(getDurationPosition(course.getDuration()));
                    etPricePerClass.setText(String.valueOf(course.getPricePerClass()));
                    etTeacherName.setText(course.getTeacherName());
                    etLocation.setText(course.getLocation());
                    etDescription.setText(course.getDescription());
                    timePickerCourseTime.setHour(selectedDate.get(Calendar.HOUR_OF_DAY));
                    timePickerCourseTime.setMinute(selectedDate.get(Calendar.MINUTE));
                    for (int i = 0; i < classTypesList.size(); i++) {
                        if (classTypesList.get(i).getId().equals(course.getClassType().getId())) {
                            spinnerClassType.setSelection(i);
                            break;
                        }
                    }
                } else {
                    Log.e("CourseFormActivity", "Failed to load course details");
                }
            }

            @Override
            public void onFailure(Call<YogaCourse> call, Throwable t) {
                Log.e("CourseFormActivity", "Error: " + t.getMessage());
            }
        });
    }

    private int getDurationPosition(int duration) {
        String[] durations = getResources().getStringArray(R.array.duration_array);
        for (int i = 0; i < durations.length; i++) {
            if (Integer.parseInt(durations[i]) == duration) {
                return i;
            }
        }
        return 0;
    }

    private void updateCourse() {
        YogaCourse updatedCourse = getCourseFromForm();
        ApiService apiService = RetrofitClient.getApiService();
        apiService.updateCourse(courseId, updatedCourse).enqueue(new Callback<YogaCourse>() {
            @Override
            public void onResponse(Call<YogaCourse> call, Response<YogaCourse> response) {
                if (response.isSuccessful()) {
                    Toast.makeText(CourseFormActivity.this, "Course updated successfully", Toast.LENGTH_SHORT).show();
                    finish();
                } else {
                    Log.e("CourseFormActivity", "Failed to update course: " + response.message());
                    Toast.makeText(CourseFormActivity.this, "Failed to update course", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<YogaCourse> call, Throwable t) {
                Log.e("CourseFormActivity", "Error: " + t.getMessage());
                Toast.makeText(CourseFormActivity.this, "Error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void loadClassTypes() {
        ApiService apiService = RetrofitClient.getApiService();
        apiService.getAllClassTypes().enqueue(new Callback<List<ClassType>>() {
            @Override
            public void onResponse(Call<List<ClassType>> call, Response<List<ClassType>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    classTypesList = response.body();
                    ArrayAdapter<ClassType> adapter = new ArrayAdapter<>(CourseFormActivity.this,
                            android.R.layout.simple_spinner_item, classTypesList);
                    adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                    spinnerClassType.setAdapter(adapter);
                } else {
                    Log.e("CourseFormActivity", "Failed to load class types");
                }
            }

            @Override
            public void onFailure(Call<List<ClassType>> call, Throwable t) {
                Log.e("CourseFormActivity", "Error: " + t.getMessage());
            }
        });
    }


    private void selectDayOfWeek() {
        String[] daysOfWeek = {"Sunday", "Monday", "Tuesday", "Wednesday", "Thursday", "Friday", "Saturday"};
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Select Day of the Week");
        builder.setItems(daysOfWeek, (dialog, which) -> {
            etDayOfWeek.setText(daysOfWeek[which]);

            int currentDayOfWeek = selectedDate.get(Calendar.DAY_OF_WEEK);
            int targetDayOfWeek = which + 1;
            int daysUntilTarget = (targetDayOfWeek - currentDayOfWeek + 7) % 7;
            if (daysUntilTarget == 0) {
                daysUntilTarget = 7;
            }
            selectedDate.add(Calendar.DAY_OF_MONTH, daysUntilTarget);
        });
        builder.show();
    }

    private void addCourse() {
        YogaCourse newCourse = getCourseFromForm();
        ApiService apiService = RetrofitClient.getApiService();
        apiService.createCourse(newCourse).enqueue(new Callback<YogaCourse>() {
            @Override
            public void onResponse(Call<YogaCourse> call, Response<YogaCourse> response) {
                if (response.isSuccessful()) {
                    Toast.makeText(CourseFormActivity.this, "Course added successfully", Toast.LENGTH_SHORT).show();
                    finish();
                } else {
                    Log.e("CourseFormActivity", "Failed to add course: " + response.message());
                    Toast.makeText(CourseFormActivity.this, "Failed to add course", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<YogaCourse> call, Throwable t) {
                Log.e("CourseFormActivity", "Error: " + t.getMessage());
                Toast.makeText(CourseFormActivity.this, "Error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private YogaCourse getCourseFromForm() {
        String dayOfWeek = etDayOfWeek.getText().toString().trim();
        int capacity = Integer.parseInt(etCapacity.getText().toString().trim());
        int duration = Integer.parseInt(spinnerDuration.getSelectedItem().toString());
        double pricePerClass = Double.parseDouble(etPricePerClass.getText().toString().trim());
        String teacherName = etTeacherName.getText().toString().trim();
        String location = etLocation.getText().toString().trim();
        String description = etDescription.getText().toString().trim();

        ClassType classType = (ClassType) spinnerClassType.getSelectedItem();

        int hour = timePickerCourseTime.getHour();
        int minute = timePickerCourseTime.getMinute();
        selectedDate.set(Calendar.HOUR_OF_DAY, hour);
        selectedDate.set(Calendar.MINUTE, minute);

        String courseTime = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Locale.getDefault())
                .format(selectedDate.getTime());

        return new YogaCourse(null, dayOfWeek, courseTime, capacity, duration, pricePerClass, classType, description, teacherName, location, null);
    }
}
