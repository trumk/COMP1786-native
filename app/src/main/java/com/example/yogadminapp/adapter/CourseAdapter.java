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
import com.example.yogadminapp.models.ClassType;
import com.example.yogadminapp.models.YogaCourse;
import java.util.List;
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

        // Hiển thị thông tin tên khóa học với số thứ tự
        holder.tvNo.setText("Course " + (position + 1)); // position bắt đầu từ 0, cộng thêm 1 để bắt đầu từ 1

        // Hiển thị thông tin địa điểm
        holder.tvLocation.setText("Location: " + course.getLocation());

        // Hiển thị thông tin ngày
        holder.tvCourseTime.setText("Day: " + course.getDayOfWeek());

        // Hiển thị thông tin capacity
        holder.tvCapacity.setText("Capacity: " + course.getCapacity());

        // Xử lý sự kiện khi nhấn nút Edit
        holder.btnEdit.setOnClickListener(v -> {
            Intent intent = new Intent(context, CourseFormActivity.class);
            intent.putExtra("courseId", course.getId());
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
        TextView tvNo, tvLocation, tvCourseTime, tvCapacity;
        Button btnEdit, btnDelete;

        public CourseViewHolder(@NonNull View itemView) {
            super(itemView);
            tvNo = itemView.findViewById(R.id.tvNo); // Đổi từ tvCourseName sang tvTeacher
            tvLocation = itemView.findViewById(R.id.tvLocation);
            tvCourseTime = itemView.findViewById(R.id.tvCourseTime);
            tvCapacity = itemView.findViewById(R.id.tvCapacity);
            btnEdit = itemView.findViewById(R.id.btnEdit);
            btnDelete = itemView.findViewById(R.id.btnDelete);
        }
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