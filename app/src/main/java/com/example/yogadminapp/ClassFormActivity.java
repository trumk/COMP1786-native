package com.example.yogadminapp;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
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

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.text.ParseException;

public class ClassFormActivity extends AppCompatActivity {
    private EditText etClassName, etDescription, etTeacher, etDate, etDuration;
    private Button btnSave, btnBackToList;
    private String classId = null;
    private Calendar calendar;
    private final SimpleDateFormat inputFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.getDefault());
    private final SimpleDateFormat outputFormat = new SimpleDateFormat("EEEE, dd MMM yyyy - HH:mm", Locale.getDefault());

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_class_form);

        etClassName = findViewById(R.id.etClassName);
        etDescription = findViewById(R.id.etDescription);
        etTeacher = findViewById(R.id.etTeacher);
        etDate = findViewById(R.id.etDate);
        etDuration = findViewById(R.id.etDuration);
        btnSave = findViewById(R.id.btnSave);
        btnBackToList = findViewById(R.id.btnBackToList);
        calendar = Calendar.getInstance();

        if (getIntent().hasExtra("classTypeId")) {
            classId = getIntent().getStringExtra("classTypeId");
            loadClassDetails(classId);
        }

        etDate.setOnClickListener(v -> showDateTimePicker());

        btnBackToList.setOnClickListener(v -> finish());

        btnSave.setOnClickListener(v -> {
            if (classId == null) {
                saveClass();
            } else {
                updateClass();
            }
        });
    }

    private void showDateTimePicker() {
        DatePickerDialog datePickerDialog = new DatePickerDialog(
                this,
                (view, year, monthOfYear, dayOfMonth) -> {
                    calendar.set(Calendar.YEAR, year);
                    calendar.set(Calendar.MONTH, monthOfYear);
                    calendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);

                    showTimePicker();
                },
                calendar.get(Calendar.YEAR),
                calendar.get(Calendar.MONTH),
                calendar.get(Calendar.DAY_OF_MONTH)
        );
        datePickerDialog.getDatePicker().setMinDate(System.currentTimeMillis() - 1000);
        datePickerDialog.show();
    }


    private void showTimePicker() {
        TimePickerDialog timePickerDialog = new TimePickerDialog(
                this,
                (view, hourOfDay, minute) -> {
                    // Đặt giờ và phút vào Calendar
                    calendar.set(Calendar.HOUR_OF_DAY, hourOfDay);
                    calendar.set(Calendar.MINUTE, minute);

                    // Cập nhật EditText cho ngày và giờ đã chọn
                    updateDateTime();
                },
                calendar.get(Calendar.HOUR_OF_DAY),
                calendar.get(Calendar.MINUTE),
                true
        );
        timePickerDialog.show();
    }

    private void updateDateTime() {
        SimpleDateFormat isoFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.getDefault());
        String selectedDate = isoFormat.format(calendar.getTime());
        etDate.setText(selectedDate);

        Log.d("ClassFormActivity", "Selected date: " + selectedDate);
    }


    private void loadClassDetails(String id) {
        ApiService apiService = RetrofitClient.getApiService();
        apiService.getClassTypeById(id).enqueue(new Callback<ClassType>() {
            @Override
            public void onResponse(Call<ClassType> call, Response<ClassType> response) {
                if (response.isSuccessful() && response.body() != null) {
                    ClassType classType = response.body();
                    etClassName.setText(classType.getTypeName());
                    etDescription.setText(classType.getDescription());
                    etTeacher.setText(classType.getTeacher());
                    etDuration.setText(String.valueOf(classType.getDuration()));

                    try {
                        Date date = inputFormat.parse(classType.getDate());
                        calendar.setTime(date);
                        String formattedDate = outputFormat.format(date);
                        etDate.setText(formattedDate);
                    } catch (ParseException e) {
                        Log.e("ClassFormActivity", "Date parsing error: " + e.getMessage());
                    }
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
        String description = etDescription.getText().toString().trim();
        String teacher = etTeacher.getText().toString().trim();
        String date = etDate.getText().toString().trim();
        int duration = Integer.parseInt(etDuration.getText().toString().trim());

        if (className.isEmpty()) {
            Toast.makeText(ClassFormActivity.this, "Class name is required", Toast.LENGTH_SHORT).show();
            return;
        }

        if (date.isEmpty()) {
            Toast.makeText(ClassFormActivity.this, "Date is required", Toast.LENGTH_SHORT).show();
            return;
        }

        Log.d("ClassFormActivity", "Saving class with details: " +
                "Name: " + className + ", " +
                "Description: " + description + ", " +
                "Teacher: " + teacher + ", " +
                "Date: " + date);

        ClassType newClass = new ClassType(null, className, description, teacher, date, duration, 0);
        ApiService apiService = RetrofitClient.getApiService();
        apiService.createClassType(newClass).enqueue(new Callback<ClassType>() {
            @Override
            public void onResponse(Call<ClassType> call, Response<ClassType> response) {
                if (response.isSuccessful()) {
                    Toast.makeText(ClassFormActivity.this, "Class added successfully", Toast.LENGTH_SHORT).show();
                    finish();
                } else {
                    String errorMessage = "Failed to add class. Response code: " + response.code();
                    if (response.errorBody() != null) {
                        try {
                            errorMessage += ", Error body: " + response.errorBody().string();
                        } catch (Exception e) {
                            Log.e("ClassFormActivity", "Error reading errorBody: " + e.getMessage());
                        }
                    }
                    Log.e("ClassFormActivity", errorMessage);
                    Toast.makeText(ClassFormActivity.this, "Failed to add class", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<ClassType> call, Throwable t) {
                // Log chi tiết lỗi khi không thể gọi API
                Log.e("ClassFormActivity", "API call failed: " + t.getMessage(), t);
                Toast.makeText(ClassFormActivity.this, "Error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void updateClass() {
        String className = etClassName.getText().toString().trim();
        String description = etDescription.getText().toString().trim();
        String teacher = etTeacher.getText().toString().trim();
        int duration = Integer.parseInt(etDuration.getText().toString().trim());

        if (className.isEmpty()) {
            Toast.makeText(ClassFormActivity.this, "Class name is required", Toast.LENGTH_SHORT).show();
            return;
        }

        SimpleDateFormat isoFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.getDefault());
        String date = isoFormat.format(calendar.getTime());

        if (date.isEmpty()) {
            Toast.makeText(ClassFormActivity.this, "Date is required", Toast.LENGTH_SHORT).show();
            return;
        }

        ClassType updatedClass = new ClassType(classId, className, description, teacher,date, duration,0);
        ApiService apiService = RetrofitClient.getApiService();
        apiService.updateClassType(classId, updatedClass).enqueue(new Callback<ClassType>() {
            @Override
            public void onResponse(Call<ClassType> call, Response<ClassType> response) {
                if (response.isSuccessful()) {
                    Toast.makeText(ClassFormActivity.this, "Class updated successfully", Toast.LENGTH_SHORT).show();
                    finish();
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
