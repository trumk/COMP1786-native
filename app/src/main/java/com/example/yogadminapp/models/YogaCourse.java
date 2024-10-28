package com.example.yogadminapp.models;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.List;

public class YogaCourse implements Serializable {

    @SerializedName("_id")
    private String id;

    @SerializedName("dayOfWeek")
    private String dayOfWeek;

    @SerializedName("timeOfCourse")
    private String timeOfCourse;

    @SerializedName("capacity")
    private int capacity;

    @SerializedName("pricePerClass")
    private double pricePerClass;

    @SerializedName("classType")
    private List<ClassType> classTypes;

    @SerializedName("location")
    private String location;

    @SerializedName("participants")
    private List<String> participants;

    // Hàm khởi tạo mặc định
    public YogaCourse() {}

    // Hàm khởi tạo đầy đủ (với danh sách ClassType)
    public YogaCourse(String id, String dayOfWeek, String timeOfCourse, int capacity,
                      double pricePerClass, List<ClassType> classTypes, String location,
                      List<String> participants) {
        this.id = id;
        this.dayOfWeek = dayOfWeek;
        this.timeOfCourse = timeOfCourse;
        this.capacity = capacity;
        this.pricePerClass = pricePerClass;
        this.classTypes = classTypes;
        this.location = location;
        this.participants = participants;
    }

    // Hàm khởi tạo cho updateCourse (với danh sách ID của ClassType)
    public YogaCourse(String id, String dayOfWeek, String timeOfCourse, int capacity,
                      double pricePerClass, List<String> classTypeIds, String location) {
        this.id = id;
        this.dayOfWeek = dayOfWeek;
        this.timeOfCourse = timeOfCourse;
        this.capacity = capacity;
        this.pricePerClass = pricePerClass;
        this.location = location;

        // Chuyển đổi danh sách ID thành đối tượng ClassType
        this.classTypes = null; // Hoặc có thể xử lý theo yêu cầu khác, nếu cần
    }

    // Getters và setters cho các trường...
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getDayOfWeek() {
        return dayOfWeek;
    }

    public void setDayOfWeek(String dayOfWeek) {
        this.dayOfWeek = dayOfWeek;
    }

    public String getTimeOfCourse() {
        return timeOfCourse;
    }

    public void setTimeOfCourse(String timeOfCourse) {
        this.timeOfCourse = timeOfCourse;
    }

    public int getCapacity() {
        return capacity;
    }

    public void setCapacity(int capacity) {
        this.capacity = capacity;
    }

    public double getPricePerClass() {
        return pricePerClass;
    }

    public void setPricePerClass(double pricePerClass) {
        this.pricePerClass = pricePerClass;
    }

    public List<ClassType> getClassTypes() {
        return classTypes;
    }

    public void setClassTypes(List<ClassType> classTypes) {
        this.classTypes = classTypes;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public List<String> getParticipants() {
        return participants;
    }

    public void setParticipants(List<String> participants) {
        this.participants = participants;
    }
}
