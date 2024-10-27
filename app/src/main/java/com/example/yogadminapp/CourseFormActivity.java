package com.example.yogadminapp;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.example.yogadminapp.adapter.ClassAdapter;
import com.example.yogadminapp.api.ApiService;
import com.example.yogadminapp.api.RetrofitClient;
import com.example.yogadminapp.models.ClassType;
import com.example.yogadminapp.models.YogaCourse;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

public class CourseFormActivity extends AppCompatActivity {
    private EditText etDayOfWeek, etCapacity, etPricePerClass, etLocation;
    private Button btnSave, btnBackToList, btnAddClass;
    private RecyclerView rvClasses;
    private ClassAdapter classAdapter;
    private String courseId;
    private boolean isEditMode = false;
    private String selectedDayOfWeek = null;
    private List<ClassType> selectedClassTypes = new ArrayList<>();
    private List<ClassType> originalClassTypes = new ArrayList<>();
    private Calendar selectedCalendar = Calendar.getInstance();

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
        btnAddClass = findViewById(R.id.btnAddClass);
        rvClasses = findViewById(R.id.rvClasses);

        rvClasses.setLayoutManager(new LinearLayoutManager(this));
        classAdapter = new ClassAdapter(selectedClassTypes, new ClassAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(ClassType classType) {
                Toast.makeText(CourseFormActivity.this, "Clicked: " + classType.getTypeName(), Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onEditClick(ClassType classType) {
                editClass(classType);
            }

            @Override
            public void onDeleteClick(ClassType classType) {
                deleteClass(classType);
            }
        });

        rvClasses.setAdapter(classAdapter);

        if (getIntent().hasExtra("courseId")) {
            courseId = getIntent().getStringExtra("courseId");
            isEditMode = true;
            loadCourseDetails(courseId);
        }

        updateUI();

        etDayOfWeek.setOnClickListener(v -> selectDayOfWeek());
        btnBackToList.setOnClickListener(v -> finish());
        btnAddClass.setOnClickListener(v -> addClass());

        btnSave.setOnClickListener(v -> {
            if (courseId == null) {
                addCourse();
            } else {
                saveClassTypes();
            }
        });
    }

    private void updateUI() {
        if (isEditMode) {
            btnAddClass.setVisibility(View.VISIBLE);
            rvClasses.setVisibility(View.VISIBLE);
        } else {
            btnAddClass.setVisibility(View.GONE);
            rvClasses.setVisibility(View.GONE);
        }
    }

    private void selectDayOfWeek() {
        String[] daysOfWeek = getDynamicDayOfWeek();
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Select Day of the Week");
        builder.setItems(daysOfWeek, (dialog, which) -> {
            etDayOfWeek.setText(daysOfWeek[which]);
            selectedDayOfWeek = daysOfWeek[which];
        });
        builder.show();
    }

    private String[] getDynamicDayOfWeek() {
        String[] allDays = {"Sunday", "Monday", "Tuesday", "Wednesday", "Thursday", "Friday", "Saturday"};
        int todayIndex = selectedCalendar.get(Calendar.DAY_OF_WEEK) - 1;
        String[] dynamicDays = new String[7];

        for (int i = 0; i < 7; i++) {
            dynamicDays[i] = allDays[(todayIndex + i) % 7];
        }
        return dynamicDays;
    }

    private void selectDate(EditText inputDate) {
        final Calendar calendar = Calendar.getInstance();
        DatePickerDialog datePickerDialog = new DatePickerDialog(this, (view, year, month, dayOfMonth) -> {
            calendar.set(year, month, dayOfMonth);

            if (isValidDateForSelectedDayOfWeek(calendar)) {
                new TimePickerDialog(this, (timeView, hourOfDay, minute) -> {
                    calendar.set(Calendar.HOUR_OF_DAY, hourOfDay);
                    calendar.set(Calendar.MINUTE, minute);

                    String selectedDateTime = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.getDefault()).format(calendar.getTime());
                    inputDate.setText(selectedDateTime);
                }, calendar.get(Calendar.HOUR_OF_DAY), calendar.get(Calendar.MINUTE), true).show();
            } else {
                Toast.makeText(this, "Please select a valid " + selectedDayOfWeek, Toast.LENGTH_SHORT).show();
                selectDate(inputDate); // Reopen the DatePickerDialog if the date is invalid
            }
        }, calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH));

        datePickerDialog.getDatePicker().setMinDate(System.currentTimeMillis());
        datePickerDialog.show();
    }

    private boolean isValidDateForSelectedDayOfWeek(Calendar date) {
        if (selectedDayOfWeek == null) return false;

        int dayOfWeek = date.get(Calendar.DAY_OF_WEEK);
        String[] days = {"Sunday", "Monday", "Tuesday", "Wednesday", "Thursday", "Friday", "Saturday"};
        return days[dayOfWeek - 1].equalsIgnoreCase(selectedDayOfWeek);
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
                    selectedClassTypes = course.getClassTypes() != null ? course.getClassTypes() : new ArrayList<>();

                    for (ClassType ct : selectedClassTypes) {
                        if (ct.getId() == null || ct.getId().isEmpty()) {
                            Log.e("CourseFormActivity", "Error: Missing ID for ClassType: " + ct.getTypeName());
                        } else {
                            Log.d("CourseFormActivity", "Loaded ClassType with ID: " + ct.getId());
                        }
                    }

                    originalClassTypes = new ArrayList<>(selectedClassTypes);
                    classAdapter.setData(selectedClassTypes);
                } else {
                    handleError(response);
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
                null,
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
                    handleError(response);
                }
            }

            @Override
            public void onFailure(Call<YogaCourse> call, Throwable t) {
                Log.e("CourseFormActivity", "Error: " + t.getMessage());
                Toast.makeText(CourseFormActivity.this, "Error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void addClass() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Add Class");

        View viewInflated = getLayoutInflater().inflate(R.layout.dialog_add_class, null);
        builder.setView(viewInflated);

        final EditText inputTypeName = viewInflated.findViewById(R.id.inputTypeName);
        final EditText inputDescription = viewInflated.findViewById(R.id.inputDescription);
        final EditText inputTeacher = viewInflated.findViewById(R.id.inputTeacher);
        final EditText inputDuration = viewInflated.findViewById(R.id.inputDuration);
        final EditText inputDate = viewInflated.findViewById(R.id.inputDate);

        inputDate.setOnClickListener(v -> selectDate(inputDate));

        builder.setPositiveButton("Add", (dialog, which) -> {
            dialog.dismiss();
            String typeName = inputTypeName.getText().toString();
            String description = inputDescription.getText().toString();
            String teacher = inputTeacher.getText().toString();
            String date = inputDate.getText().toString();
            int duration = Integer.parseInt(inputDuration.getText().toString());

            ClassType newClassType = new ClassType(typeName, description, teacher, date, duration);
            selectedClassTypes.add(newClassType);
            classAdapter.notifyDataSetChanged();
            Toast.makeText(this, "Class added to draft", Toast.LENGTH_SHORT).show();
        });

        builder.setNegativeButton("Cancel", (dialog, which) -> dialog.cancel());
        builder.show();
    }

    private void saveClassTypes() {
        ApiService apiService = RetrofitClient.getApiService();
        boolean hasNewClass = false;

        for (ClassType classType : selectedClassTypes) {
            boolean isNewClass = classType.getId() == null ||
                    originalClassTypes.stream().noneMatch(c -> c.getId().equals(classType.getId()));

            if (isNewClass) {
                hasNewClass = true;
                apiService.addClassTypeToCourse(courseId, classType).enqueue(new Callback<ClassType>() {
                    @Override
                    public void onResponse(Call<ClassType> call, Response<ClassType> response) {
                        if (response.isSuccessful()) {
                            Log.d("CourseFormActivity", "New ClassType saved: " + response.body());
                        } else {
                            handleError(response);
                        }
                    }

                    @Override
                    public void onFailure(Call<ClassType> call, Throwable t) {
                        Log.e("CourseFormActivity", "Error saving new ClassType: " + t.getMessage());
                    }
                });
            }
        }

        if (hasNewClass) {
            Toast.makeText(this, "New classes saved to course", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(this, "No new classes to save", Toast.LENGTH_SHORT).show();
        }
    }

    private void editClass(ClassType classType) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Edit Class");

        View viewInflated = getLayoutInflater().inflate(R.layout.dialog_add_class, null);
        builder.setView(viewInflated);

        final EditText inputTypeName = viewInflated.findViewById(R.id.inputTypeName);
        final EditText inputDescription = viewInflated.findViewById(R.id.inputDescription);
        final EditText inputTeacher = viewInflated.findViewById(R.id.inputTeacher);
        final EditText inputDuration = viewInflated.findViewById(R.id.inputDuration);
        final EditText inputDate = viewInflated.findViewById(R.id.inputDate);

        inputTypeName.setText(classType.getTypeName());
        inputDescription.setText(classType.getDescription());
        inputTeacher.setText(classType.getTeacher());
        inputDuration.setText(String.valueOf(classType.getDuration()));
        inputDate.setText(classType.getDate());

        inputDate.setOnClickListener(v -> selectDate(inputDate));

        builder.setPositiveButton("Save", (dialog, which) -> {
            dialog.dismiss();
            classType.setTypeName(inputTypeName.getText().toString());
            classType.setDescription(inputDescription.getText().toString());
            classType.setTeacher(inputTeacher.getText().toString());
            classType.setDuration(Integer.parseInt(inputDuration.getText().toString()));
            classType.setDate(inputDate.getText().toString());

            ApiService apiService = RetrofitClient.getApiService();
            apiService.updateClassTypeInCourse(classType.getId(), classType).enqueue(new Callback<ClassType>() {
                @Override
                public void onResponse(Call<ClassType> call, Response<ClassType> response) {
                    if (response.isSuccessful()) {
                        Log.d("CourseFormActivity", "ClassType updated successfully: " + response.body());
                        classAdapter.notifyDataSetChanged();
                        Toast.makeText(CourseFormActivity.this, "Class updated successfully", Toast.LENGTH_SHORT).show();
                    } else {
                        handleError(response);
                    }
                }

                @Override
                public void onFailure(Call<ClassType> call, Throwable t) {
                    Log.e("CourseFormActivity", "Error updating ClassType: " + t.getMessage());
                    Toast.makeText(CourseFormActivity.this, "Error updating class: " + t.getMessage(), Toast.LENGTH_LONG).show();
                }
            });
        });

        builder.setNegativeButton("Cancel", (dialog, which) -> dialog.cancel());
        builder.show();
    }


    private void deleteClass(ClassType classType) {
        if (classType.getId() == null || classType.getId().isEmpty() || classType.getId().length() != 24) {
            Log.e("CourseFormActivity", "Error: Invalid ClassType ID format - " + classType.getId());
            Toast.makeText(this, "Cannot delete class: Invalid ClassType ID format.", Toast.LENGTH_LONG).show();
            return;
        }
        
        new AlertDialog.Builder(this)
                .setTitle("Delete Class")
                .setMessage("Are you sure you want to delete this class?")
                .setPositiveButton("Yes", (dialog, which) -> {
                    ApiService apiService = RetrofitClient.getApiService();
                    apiService.removeClassTypeFromCourse(courseId, classType.getId()).enqueue(new Callback<Void>() {
                        @Override
                        public void onResponse(Call<Void> call, Response<Void> response) {
                            if (response.isSuccessful()) {
                                Log.d("CourseFormActivity", "Successfully deleted ClassType with ID: " + classType.getId());
                                selectedClassTypes.remove(classType);
                                classAdapter.notifyDataSetChanged();
                                Toast.makeText(CourseFormActivity.this, "Class deleted successfully", Toast.LENGTH_SHORT).show();
                            } else {
                                try {
                                    Log.e("CourseFormActivity", "Failed to delete class - Server Response: " + response.errorBody().string());
                                } catch (IOException e) {
                                    e.printStackTrace();
                                }
                                handleError(response);
                            }
                        }

                        @Override
                        public void onFailure(Call<Void> call, Throwable t) {
                            Log.e("CourseFormActivity", "Error deleting ClassType: " + t.getMessage());
                            Toast.makeText(CourseFormActivity.this, "Error deleting class: " + t.getMessage(), Toast.LENGTH_LONG).show();
                        }
                    });
                })
                .setNegativeButton("No", (dialog, which) -> dialog.dismiss())
                .show();
    }

    private void handleError(Response<?> response) {
        try {
            if (response.errorBody() != null) {
                String errorBody = response.errorBody().string();
                Log.e("CourseFormActivity", "Failed: " + errorBody);
                Toast.makeText(CourseFormActivity.this, "Error: " + errorBody, Toast.LENGTH_LONG).show();
            } else {
                Toast.makeText(CourseFormActivity.this, "Unknown error occurred", Toast.LENGTH_LONG).show();
            }
        } catch (IOException e) {
            Log.e("CourseFormActivity", "Error parsing error body: " + e.getMessage());
            Toast.makeText(CourseFormActivity.this, "Error: Could not parse error details", Toast.LENGTH_LONG).show();
        }
    }

}
