package com.example.yogadminapp.models;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.List;

public class YogaCourse implements Serializable {

    @SerializedName("_id")
    private String id;

    @SerializedName("dayOfWeek")
    private String dayOfWeek;

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

    public YogaCourse() {
    }

    // Constructor với các tham số
    public YogaCourse(String id, String dayOfWeek, int capacity, double pricePerClass, List<ClassType> classTypes, String location, List<String> participants) {
        this.id = id;
        this.dayOfWeek = dayOfWeek;
        this.capacity = capacity;
        this.pricePerClass = pricePerClass;
        this.classTypes = classTypes;
        this.location = location;
        this.participants = participants;
    }

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
