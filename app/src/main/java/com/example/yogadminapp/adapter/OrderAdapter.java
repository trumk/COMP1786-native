package com.example.yogadminapp.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.example.yogadminapp.R;
import com.example.yogadminapp.models.Order;

import java.util.List;

public class OrderAdapter extends RecyclerView.Adapter<OrderAdapter.OrderViewHolder> {
    private Context context;
    private List<Order> orders;
    private OnItemClickListener listener;

    public OrderAdapter(Context context, List<Order> orders, OnItemClickListener listener) {
        this.context = context;
        this.orders = orders;
        this.listener = listener;
    }

    @NonNull
    @Override
    public OrderViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_order, parent, false);
        return new OrderViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull OrderViewHolder holder, int position) {
        Order order = orders.get(position);
        holder.bind(order, listener);
    }

    @Override
    public int getItemCount() {
        return orders.size();
    }

    public static class OrderViewHolder extends RecyclerView.ViewHolder {
        private TextView tvStatus, tvTotalAmount, tvCreatedAt;
        private Button btnViewDetail;

        public OrderViewHolder(@NonNull View itemView) {
            super(itemView);
            tvStatus = itemView.findViewById(R.id.tvStatus);
            tvTotalAmount = itemView.findViewById(R.id.tvTotalAmount);
            tvCreatedAt = itemView.findViewById(R.id.tvCreatedAt);
            btnViewDetail = itemView.findViewById(R.id.btnViewDetail);
        }

        public void bind(Order order, OnItemClickListener listener) {
            tvStatus.setText(order.getStatus());
            tvTotalAmount.setText(String.valueOf(order.getTotalAmount()));
            tvCreatedAt.setText(order.getCreatedAt().toString());
            btnViewDetail.setOnClickListener(v -> listener.onItemClick(order)); // Xử lý sự kiện khi bấm vào View Detail


            itemView.setOnClickListener(v -> listener.onItemClick(order));
        }
    }

    public interface OnItemClickListener {
        void onItemClick(Order order);
    }
}
