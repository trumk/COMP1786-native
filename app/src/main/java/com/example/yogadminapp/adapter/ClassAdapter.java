package com.example.yogadminapp.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.example.yogadminapp.R;
import com.example.yogadminapp.models.ClassType;
import java.util.List;

public class ClassAdapter extends RecyclerView.Adapter<ClassAdapter.ClassViewHolder> {

    private final List<ClassType> classTypeList;
    private final OnItemClickListener listener;

    public interface OnItemClickListener {
        void onItemClick(ClassType classType);
        void onEditClick(ClassType classType);
        void onDeleteClick(ClassType classType);
    }

    public ClassAdapter(List<ClassType> classTypeList, OnItemClickListener listener) {
        this.classTypeList = classTypeList;
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
        ClassType classType = classTypeList.get(position);
        holder.typeNameTextView.setText(classType.getTypeName());

        holder.itemView.setOnClickListener(v -> listener.onItemClick(classType));
        holder.editButton.setOnClickListener(v -> listener.onEditClick(classType));
        holder.deleteButton.setOnClickListener(v -> listener.onDeleteClick(classType));
    }

    @Override
    public int getItemCount() {
        return classTypeList.size();
    }

    public static class ClassViewHolder extends RecyclerView.ViewHolder {
        TextView typeNameTextView;
        Button editButton, deleteButton;

        public ClassViewHolder(@NonNull View itemView) {
            super(itemView);
            typeNameTextView = itemView.findViewById(R.id.tvTypeName);
            editButton = itemView.findViewById(R.id.btnEditClass);
            deleteButton = itemView.findViewById(R.id.btnDeleteClass);
        }
    }
}
