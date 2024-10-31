package com.example.yogadminapp;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.example.yogadminapp.adapter.ClassAdapter;
import com.example.yogadminapp.api.ApiService;
import com.example.yogadminapp.api.RetrofitClient;
import com.example.yogadminapp.models.Class;
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
    private EditText etDayOfWeek, etTimeOfCourse, etCapacity, etPricePerClass, etLocation;
    private Button btnSave, btnBackToList, btnAddClass;
    private Spinner spinnerTypeOfClass;
    private RecyclerView rvClasses;
    private ClassAdapter classAdapter;
    private String courseId;
    private boolean isEditMode = false;
    private String selectedDayOfWeek = null;
    private String selectedTimeOfCourse = null;
    private List<Class> selectedClasses = new ArrayList<>();
    private List<Class> originalClasses = new ArrayList<>();
    private Calendar selectedCalendar = Calendar.getInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_course_form);

        etDayOfWeek = findViewById(R.id.etDayOfWeek);
        etTimeOfCourse = findViewById(R.id.etTimeOfCourse);
        etCapacity = findViewById(R.id.etCapacity);
        etPricePerClass = findViewById(R.id.etPricePerClass);
        etLocation = findViewById(R.id.etLocation);
        btnSave = findViewById(R.id.btnSave);
        btnBackToList = findViewById(R.id.btnBackToList);
        btnAddClass = findViewById(R.id.btnAddClass);
        rvClasses = findViewById(R.id.rvClasses);
        spinnerTypeOfClass = findViewById(R.id.spinnerTypeOfClass);

        rvClasses.setLayoutManager(new LinearLayoutManager(this));
        classAdapter = new ClassAdapter(selectedClasses, new ClassAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(Class aClass) {
                Toast.makeText(CourseFormActivity.this, "Clicked: " + aClass.getClassName(), Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onEditClick(Class aClass) {
                editClass(aClass);
            }

            @Override
            public void onDeleteClick(Class aClass) {
                deleteClass(aClass);
            }
        });
        rvClasses.setAdapter(classAdapter);

        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.type_of_class_array, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerTypeOfClass.setAdapter(adapter);

        if (getIntent().hasExtra("courseId")) {
            courseId = getIntent().getStringExtra("courseId");
            isEditMode = true;
            loadCourseDetails(courseId);
        }

        updateUI();

        etDayOfWeek.setOnClickListener(v -> selectDayOfWeek());
        etTimeOfCourse.setOnClickListener(v -> selectTimeOfCourse());
        btnBackToList.setOnClickListener(v -> finish());
        btnAddClass.setOnClickListener(v -> addClass());

        btnSave.setOnClickListener(v -> {
            if (isEditMode) {
                updateCourse();
            } else {
                addCourse();
            }
            saveClass();
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

    private void selectTimeOfCourse() {
        final Calendar calendar = Calendar.getInstance();
        new TimePickerDialog(this, (view, startHour, startMinute) -> {
            calendar.set(Calendar.HOUR_OF_DAY, startHour);
            calendar.set(Calendar.MINUTE, startMinute);
            String startTime = new SimpleDateFormat("HH:mm", Locale.getDefault()).format(calendar.getTime());

            new TimePickerDialog(this, (endView, endHour, endMinute) -> {
                calendar.set(Calendar.HOUR_OF_DAY, endHour);
                calendar.set(Calendar.MINUTE, endMinute);
                String endTime = new SimpleDateFormat("HH:mm", Locale.getDefault()).format(calendar.getTime());

                etTimeOfCourse.setText(startTime + " - " + endTime);
            }, calendar.get(Calendar.HOUR_OF_DAY), calendar.get(Calendar.MINUTE), true).show();
        }, calendar.get(Calendar.HOUR_OF_DAY), calendar.get(Calendar.MINUTE), true).show();
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
                    etTimeOfCourse.setText(course.getTimeOfCourse());
                    etCapacity.setText(String.valueOf(course.getCapacity()));
                    etPricePerClass.setText(String.valueOf(course.getPricePerClass()));
                    etLocation.setText(course.getLocation());

                    selectedDayOfWeek = course.getDayOfWeek();
                    selectedTimeOfCourse = course.getTimeOfCourse();
                    selectedClasses = course.getClasses() != null ? course.getClasses() : new ArrayList<>();
                    originalClasses = new ArrayList<>(selectedClasses);
                    classAdapter.setData(selectedClasses);

                    ArrayAdapter<CharSequence> adapter = (ArrayAdapter<CharSequence>) spinnerTypeOfClass.getAdapter();
                    int position = adapter.getPosition(course.getTypeOfClass());
                    spinnerTypeOfClass.setSelection(position);
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
        selectedTimeOfCourse = etTimeOfCourse.getText().toString().trim();

        if (selectedTimeOfCourse.isEmpty()) {
            Toast.makeText(this, "Please select time for the course", Toast.LENGTH_SHORT).show();
            return;
        }

        YogaCourse newCourse = new YogaCourse(
                null,
                selectedDayOfWeek,
                selectedTimeOfCourse,
                Integer.parseInt(etCapacity.getText().toString().trim()),
                Double.parseDouble(etPricePerClass.getText().toString().trim()),
                null,
                spinnerTypeOfClass.getSelectedItem().toString(),
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

    private void updateCourse() {
        selectedTimeOfCourse = etTimeOfCourse.getText().toString().trim();

        if (selectedTimeOfCourse.isEmpty()) {
            Toast.makeText(this, "Please select time for the course", Toast.LENGTH_SHORT).show();
            return;
        }

        YogaCourse updatedCourse = new YogaCourse(
                courseId,
                etDayOfWeek.getText().toString().trim(),
                selectedTimeOfCourse,
                Integer.parseInt(etCapacity.getText().toString().trim()),
                Double.parseDouble(etPricePerClass.getText().toString().trim()),
                selectedClasses,
                spinnerTypeOfClass.getSelectedItem().toString(),
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

        final EditText inputClassName = viewInflated.findViewById(R.id.inputClassName);
        final EditText inputDescription = viewInflated.findViewById(R.id.inputDescription);
        final EditText inputTeacher = viewInflated.findViewById(R.id.inputTeacher);
        final EditText inputDuration = viewInflated.findViewById(R.id.inputDuration);
        final EditText inputDate = viewInflated.findViewById(R.id.inputDate);

        if (selectedTimeOfCourse == null || !selectedTimeOfCourse.contains(" - ")) {
            Toast.makeText(this, "Time of course is not set. Please set the course time first.", Toast.LENGTH_SHORT).show();
            return;
        }

        String[] timeRange = selectedTimeOfCourse.split(" - ");
        String[] start = timeRange[0].split(":");
        String[] end = timeRange[1].split(":");

        int startHour = Integer.parseInt(start[0]);
        int startMinute = Integer.parseInt(start[1]);
        int endHour = Integer.parseInt(end[0]);
        int endMinute = Integer.parseInt(end[1]);

        inputDate.setOnClickListener(v -> selectDateTime(inputDate, startHour, startMinute, endHour, endMinute));

        builder.setPositiveButton("Add", (dialog, which) -> {
            dialog.dismiss();
            String typeName = inputClassName.getText().toString();
            String description = inputDescription.getText().toString();
            String teacher = inputTeacher.getText().toString();
            String date = inputDate.getText().toString();
            int duration = Integer.parseInt(inputDuration.getText().toString());

            Class newClass = new Class(typeName, description, teacher, date, duration);
            selectedClasses.add(newClass);
            classAdapter.notifyDataSetChanged();
            Toast.makeText(this, "Class added to draft", Toast.LENGTH_SHORT).show();
        });

        builder.setNegativeButton("Cancel", (dialog, which) -> dialog.cancel());
        builder.show();
    }

    private void selectDateTime(EditText inputDate, int startHour, int startMinute, int endHour, int endMinute) {
        final Calendar calendar = Calendar.getInstance();

        DatePickerDialog datePickerDialog = new DatePickerDialog(this, (view, year, month, dayOfMonth) -> {
            calendar.set(year, month, dayOfMonth);

            if (isValidDateForSelectedDayOfWeek(calendar)) {
                TimePickerDialog timePickerDialog = new TimePickerDialog(this, (timeView, selectedHour, selectedMinute) -> {

                    String selectedTime = String.format(Locale.getDefault(), "%02d:%02d", selectedHour, selectedMinute);
                    boolean isTimeConflict = false;
                    for (Class selectedClass : selectedClasses) {
                        String[] timeParts = selectedClass.getDate().split("T")[1].split(":");
                        String classStartTime = timeParts[0] + ":" + timeParts[1];
                        if (classStartTime.equals(selectedTime)) {
                            isTimeConflict = true;
                            break;
                        }
                    }

                    if (isTimeConflict) {
                        Toast.makeText(this, "This time is already selected! Please choose another time.", Toast.LENGTH_SHORT).show();
                    } else if ((selectedHour > startHour || (selectedHour == startHour && selectedMinute >= startMinute))
                            && (selectedHour < endHour || (selectedHour == endHour && selectedMinute <= endMinute))) {
                        calendar.set(Calendar.HOUR_OF_DAY, selectedHour);
                        calendar.set(Calendar.MINUTE, selectedMinute);

                        String selectedDateTime = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.getDefault()).format(calendar.getTime());
                        inputDate.setText(selectedDateTime);
                    } else {
                        Toast.makeText(this, "Please select a time within " + selectedTimeOfCourse, Toast.LENGTH_SHORT).show();
                        selectDateTime(inputDate, startHour, startMinute, endHour, endMinute);
                    }
                }, startHour, startMinute, true);

                timePickerDialog.show();
            } else {
                Toast.makeText(this, "Please select a valid " + selectedDayOfWeek, Toast.LENGTH_SHORT).show();
                selectDateTime(inputDate, startHour, startMinute, endHour, endMinute);
            }
        }, calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH));

        datePickerDialog.getDatePicker().setMinDate(System.currentTimeMillis());
        datePickerDialog.show();
    }


    private void saveClass() {
        ApiService apiService = RetrofitClient.getApiService();
        boolean hasChanges = false;

        for (Class aClass : selectedClasses) {
            Class originalClass = originalClasses.stream()
                    .filter(c -> c.getId() != null && c.getId().equals(aClass.getId()))
                    .findFirst()
                    .orElse(null);

            boolean isNewOrChanged = originalClass == null ||
                    !aClass.getClassName().equals(originalClass.getClassName()) ||
                    !aClass.getDescription().equals(originalClass.getDescription()) ||
                    !aClass.getTeacher().equals(originalClass.getTeacher()) ||
                    !aClass.getDate().equals(originalClass.getDate()) ||
                    aClass.getDuration() != originalClass.getDuration();

            if (isNewOrChanged) {
                hasChanges = true;
                if (originalClass == null) {
                    apiService.addClassTypeToCourse(courseId, aClass).enqueue(new Callback<Class>() {
                        @Override
                        public void onResponse(Call<Class> call, Response<Class> response) {
                            if (response.isSuccessful()) {
                                Log.d("CourseFormActivity", "New ClassType saved: " + response.body());
                            } else {
                                handleError(response);
                            }
                        }

                        @Override
                        public void onFailure(Call<Class> call, Throwable t) {
                            Log.e("CourseFormActivity", "Error saving new ClassType: " + t.getMessage());
                        }
                    });
                } else {
                    apiService.updateClassTypeInCourse(aClass.getId(), aClass).enqueue(new Callback<Class>() {
                        @Override
                        public void onResponse(Call<Class> call, Response<Class> response) {
                            if (response.isSuccessful()) {
                                Log.d("CourseFormActivity", "ClassType updated successfully: " + response.body());
                            } else {
                                handleError(response);
                            }
                        }

                        @Override
                        public void onFailure(Call<Class> call, Throwable t) {
                            Log.e("CourseFormActivity", "Error updating ClassType: " + t.getMessage());
                        }
                    });
                }
            }
        }

        if (hasChanges) {
            Toast.makeText(this, "Changes saved successfully", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(this, "No changes to save", Toast.LENGTH_SHORT).show();
        }
    }


    private void editClass(Class aClass) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Edit Class");

        View viewInflated = getLayoutInflater().inflate(R.layout.dialog_add_class, null);
        builder.setView(viewInflated);

        final EditText inputClassName = viewInflated.findViewById(R.id.inputClassName);
        final EditText inputDescription = viewInflated.findViewById(R.id.inputDescription);
        final EditText inputTeacher = viewInflated.findViewById(R.id.inputTeacher);
        final EditText inputDuration = viewInflated.findViewById(R.id.inputDuration);
        final EditText inputDate = viewInflated.findViewById(R.id.inputDate);

        inputClassName.setText(aClass.getClassName());
        inputDescription.setText(aClass.getDescription());
        inputTeacher.setText(aClass.getTeacher());
        inputDuration.setText(String.valueOf(aClass.getDuration()));
        inputDate.setText(aClass.getDate());

        if (selectedTimeOfCourse != null && selectedTimeOfCourse.contains(" - ")) {
            String[] timeRange = selectedTimeOfCourse.split(" - ");
            String[] start = timeRange[0].split(":");
            String[] end = timeRange[1].split(":");

            int startHour = Integer.parseInt(start[0]);
            int startMinute = Integer.parseInt(start[1]);
            int endHour = Integer.parseInt(end[0]);
            int endMinute = Integer.parseInt(end[1]);

            inputDate.setOnClickListener(v -> selectDateTime(inputDate, startHour, startMinute, endHour, endMinute));
        } else {
            Toast.makeText(this, "Time of course is not set. Please set the course time first.", Toast.LENGTH_SHORT).show();
        }

        builder.setPositiveButton("Save", (dialog, which) -> {
            dialog.dismiss();
            aClass.setClassName(inputClassName.getText().toString());
            aClass.setDescription(inputDescription.getText().toString());
            aClass.setTeacher(inputTeacher.getText().toString());
            aClass.setDuration(Integer.parseInt(inputDuration.getText().toString()));
            aClass.setDate(inputDate.getText().toString());

            ApiService apiService = RetrofitClient.getApiService();
            apiService.updateClassTypeInCourse(aClass.getId(), aClass).enqueue(new Callback<Class>() {
                @Override
                public void onResponse(Call<Class> call, Response<Class> response) {
                    if (response.isSuccessful()) {
                        Log.d("CourseFormActivity", "ClassType updated successfully: " + response.body());
                        classAdapter.notifyDataSetChanged();
                        Toast.makeText(CourseFormActivity.this, "Class updated successfully", Toast.LENGTH_SHORT).show();
                    } else {
                        handleError(response);
                    }
                }

                @Override
                public void onFailure(Call<Class> call, Throwable t) {
                    Log.e("CourseFormActivity", "Error updating ClassType: " + t.getMessage());
                    Toast.makeText(CourseFormActivity.this, "Error updating class: " + t.getMessage(), Toast.LENGTH_LONG).show();
                }
            });
        });

        builder.setNegativeButton("Cancel", (dialog, which) -> dialog.cancel());
        builder.show();
    }


    private void deleteClass(Class aClass) {
        if (aClass.getId() == null || aClass.getId().isEmpty() || aClass.getId().length() != 24) {
            Log.e("CourseFormActivity", "Error: Invalid ClassType ID format - " + aClass.getId());
            Toast.makeText(this, "Cannot delete class: Invalid ClassType ID format.", Toast.LENGTH_LONG).show();
            return;
        }
        
        new AlertDialog.Builder(this)
                .setTitle("Delete Class")
                .setMessage("Are you sure you want to delete this class?")
                .setPositiveButton("Yes", (dialog, which) -> {
                    ApiService apiService = RetrofitClient.getApiService();
                    apiService.removeClassTypeFromCourse(courseId, aClass.getId()).enqueue(new Callback<Void>() {
                        @Override
                        public void onResponse(Call<Void> call, Response<Void> response) {
                            if (response.isSuccessful()) {
                                Log.d("CourseFormActivity", "Successfully deleted ClassType with ID: " + aClass.getId());
                                selectedClasses.remove(aClass);
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