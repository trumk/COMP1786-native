package com.example.yogadminapp.adapter;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.example.yogadminapp.CourseFormActivity;
import com.example.yogadminapp.R;
import com.example.yogadminapp.api.ApiService;
import com.example.yogadminapp.api.RetrofitClient;
import com.example.yogadminapp.models.YogaCourse;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class CourseAdapter extends RecyclerView.Adapter<CourseAdapter.CourseViewHolder> {

    private List<YogaCourse> courses;
    private Context context;

    public CourseAdapter(List<YogaCourse> courses, Context context) {
        this.courses = courses;
        this.context = context;
    }

    @NonNull
    @Override
    public CourseViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_course, parent, false);
        return new CourseViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CourseViewHolder holder, int position) {
        YogaCourse course = courses.get(position);
        holder.tvCourseName.setText(course.getClassType().getTypeName());
        holder.tvTeacherName.setText(course.getTeacherName());

        String formattedDate = formatDateString(course.getCourseTime());
        holder.tvCourseTime.setText("Date: " + formattedDate);

        // Xử lý sự kiện khi nhấn nút Edit
        holder.btnEdit.setOnClickListener(v -> {
            Intent intent = new Intent(context, CourseFormActivity.class);
            intent.putExtra("courseId", course.getId()); // Truyền ID của khóa học để chỉnh sửa
            context.startActivity(intent);
        });

        // Xử lý sự kiện khi nhấn nút Delete
        holder.btnDelete.setOnClickListener(v -> {
            new android.app.AlertDialog.Builder(context)
                    .setTitle("Confirm Delete")
                    .setMessage("Are you sure you want to delete this course?")
                    .setPositiveButton(android.R.string.yes, (dialog, which) -> deleteCourse(course.getId(), position))
                    .setNegativeButton(android.R.string.no, null)
                    .show();
        });
    }

    @Override
    public int getItemCount() {
        return courses.size();
    }

    public class CourseViewHolder extends RecyclerView.ViewHolder {
        TextView tvCourseName, tvTeacherName, tvCourseTime;
        Button btnEdit, btnDelete;

        public CourseViewHolder(@NonNull View itemView) {
            super(itemView);
            tvCourseName = itemView.findViewById(R.id.tvCourseName);
            tvTeacherName = itemView.findViewById(R.id.tvTeacherName);
            tvCourseTime = itemView.findViewById(R.id.tvCourseTime);
            btnEdit = itemView.findViewById(R.id.btnEdit);
            btnDelete = itemView.findViewById(R.id.btnDelete);
        }
    }

    private String formatDateString(String dateString) {
        SimpleDateFormat inputFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Locale.getDefault());
        SimpleDateFormat outputFormat = new SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault());

        try {
            Date date = inputFormat.parse(dateString);
            if (date != null) {
                return outputFormat.format(date);
            }
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return dateString;
    }


    private void deleteCourse(String courseId, int position) {
        ApiService apiService = RetrofitClient.getApiService();
        apiService.deleteCourse(courseId).enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                if (response.isSuccessful()) {
                    courses.remove(position);
                    notifyItemRemoved(position);
                    Toast.makeText(context, "Course deleted successfully", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(context, "Failed to delete course", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                Toast.makeText(context, "Error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
}