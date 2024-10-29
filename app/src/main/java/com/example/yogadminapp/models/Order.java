package com.example.yogadminapp.models;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

public class Order implements Serializable {
    @SerializedName("_id")
    private String id;
    private String user;
    private List<OrderItem> items;
    private double totalAmount;
    private String status;
    private Date createdAt;

    public Order(String id, String user, List<OrderItem> items, double totalAmount, String status, Date createdAt) {
        this.id = id;
        this.user = user;
        this.items = items;
        this.totalAmount = totalAmount;
        this.status = status;
        this.createdAt = createdAt;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getUser() {
        return user;
    }

    public void setUser(String user) {
        this.user = user;
    }

    public List<OrderItem> getItems() {
        return items;
    }

    public void setItems(List<OrderItem> items) {
        this.items = items;
    }

    public double getTotalAmount() {
        return totalAmount;
    }

    public void setTotalAmount(double totalAmount) {
        this.totalAmount = totalAmount;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public Date getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Date createdAt) {
        this.createdAt = createdAt;
    }

    public static class OrderItem implements Serializable {
        private ClassType classType;
        private YogaCourse yogaCourse;

        public OrderItem(ClassType classType, YogaCourse yogaCourse) {
            this.classType = classType;
            this.yogaCourse = yogaCourse;
        }

        public ClassType getClassType() {
            return classType;
        }

        public void setClassType(ClassType classType) {
            this.classType = classType;
        }

        public YogaCourse getYogaCourse() {
            return yogaCourse;
        }

        public void setYogaCourse(YogaCourse yogaCourse) {
            this.yogaCourse = yogaCourse;
        }
    }

    public static class UpdateOrderRequest implements Serializable {
        private String orderId;
        private String status;

        public UpdateOrderRequest(String orderId, String status) {
            this.orderId = orderId;
            this.status = status;
        }

        public String getOrderId() {
            return orderId;
        }

        public void setOrderId(String orderId) {
            this.orderId = orderId;
        }

        public String getStatus() {
            return status;
        }

        public void setStatus(String status) {
            this.status = status;
        }
    }
}
