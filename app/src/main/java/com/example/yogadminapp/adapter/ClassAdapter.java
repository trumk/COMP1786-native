package com.example.yogadminapp.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.example.yogadminapp.R;
import com.example.yogadminapp.models.Class;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class ClassAdapter extends RecyclerView.Adapter<ClassAdapter.ClassViewHolder> {

    private final List<Class> classList;
    private final OnItemClickListener listener;
    private final SimpleDateFormat inputFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.getDefault());
    private final SimpleDateFormat outputFormat = new SimpleDateFormat("EEEE, dd MMM yyyy - HH:mm", Locale.getDefault());

    public interface OnItemClickListener {
        void onItemClick(Class aClass);
        void onEditClick(Class aClass);
        void onDeleteClick(Class aClass);
    }

    public ClassAdapter(List<Class> classList, OnItemClickListener listener) {
        this.classList = classList;
        this.listener = listener;
    }

    @NonNull
    @Override
    public ClassViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_class, parent, false);
        return new ClassViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ClassViewHolder holder, int position) {
        Class aClass = classList.get(position);
        holder.typeNameTextView.setText(aClass.getClassName());
        holder.descriptionTextView.setText(aClass.getDescription());
        holder.teacherTextView.setText(aClass.getTeacher());

        String formattedDate = formatDate(aClass.getDate());
        holder.dateTextView.setText(formattedDate);

        holder.durationTextView.setText("Duration: " + aClass.getDuration() + " minutes");

        holder.itemView.setOnClickListener(v -> listener.onItemClick(aClass));
        holder.editButton.setOnClickListener(v -> listener.onEditClick(aClass));
        holder.deleteButton.setOnClickListener(v -> listener.onDeleteClick(aClass));
    }

    @Override
    public int getItemCount() {
        return classList.size();
    }

    private String formatDate(String dateString) {
        try {
            Date date = inputFormat.parse(dateString);
            return outputFormat.format(date);
        } catch (ParseException e) {
            e.printStackTrace();
            return dateString;
        }
    }

    public void setData(List<Class> newClasses) {
        classList.clear();
        classList.addAll(newClasses);
        notifyDataSetChanged();
    }

    public static class ClassViewHolder extends RecyclerView.ViewHolder {
        TextView typeNameTextView, descriptionTextView, teacherTextView, dateTextView, durationTextView;
        Button editButton, deleteButton;

        public ClassViewHolder(@NonNull View itemView) {
            super(itemView);
            typeNameTextView = itemView.findViewById(R.id.tvTypeName);
            descriptionTextView = itemView.findViewById(R.id.tvDescription);
            teacherTextView = itemView.findViewById(R.id.tvTeacher);
            dateTextView = itemView.findViewById(R.id.tvClassDate);
            durationTextView = itemView.findViewById(R.id.tvDuration);
            editButton = itemView.findViewById(R.id.btnEditClass);
            deleteButton = itemView.findViewById(R.id.btnDeleteClass);
        }
    }
}
