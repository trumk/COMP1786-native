package com.example.yogadminapp.adapter;

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
import com.example.yogadminapp.OrderListActivity;
import java.util.List;

public class OrderAdapter extends RecyclerView.Adapter<OrderAdapter.OrderViewHolder> {
    private Context context;
    private List<Order> orders;
    private OnItemClickListener listener;
    private OrderListActivity orderListActivity;

    public OrderAdapter(Context context, List<Order> orders, OnItemClickListener listener) {
        this.context = context;
        this.orders = orders;
        this.listener = listener;
        this.orderListActivity = (OrderListActivity) context;
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
        holder.bind(order, listener, orderListActivity);
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

        public void bind(Order order, OnItemClickListener listener, OrderListActivity orderListActivity) {
            tvStatus.setText(order.getStatus());
            tvTotalAmount.setText(String.format("$%.2f", order.getTotalAmount()));

            String formattedDate = orderListActivity.formatDate(order.getCreatedAt());
            tvCreatedAt.setText(formattedDate);

            btnViewDetail.setOnClickListener(v -> listener.onItemClick(order));
            itemView.setOnClickListener(v -> listener.onItemClick(order));
        }
    }

    public interface OnItemClickListener {
        void onItemClick(Order order);
    }
}
