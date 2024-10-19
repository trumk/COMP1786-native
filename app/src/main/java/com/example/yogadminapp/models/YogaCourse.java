package com.example.yogadminapp.models;

import com.google.gson.annotations.SerializedName;
import java.util.List;

public class YogaCourse {

    @SerializedName("_id")
    private String id;

    @SerializedName("dayOfWeek")
    private String dayOfWeek;

    @SerializedName("courseTime")
    private String courseTime;

    @SerializedName("capacity")
    private int capacity;

    @SerializedName("duration")
    private int duration;

    @SerializedName("pricePerClass")
    private double pricePerClass;

    @SerializedName("classType")
    private ClassType classType;

    @SerializedName("description")
    private String description;

    @SerializedName("teacherName")
    private String teacherName;

    @SerializedName("location")
    private String location;

    @SerializedName("participants")
    private List<String> participants;

    public YogaCourse() {
    }

    public YogaCourse(String id, String dayOfWeek, String courseTime, int capacity, int duration, double pricePerClass, ClassType classType, String description, String teacherName, String location, List<String> participants) {
        this.id = id;
        this.dayOfWeek = dayOfWeek;
        this.courseTime = courseTime;
        this.capacity = capacity;
        this.duration = duration;
        this.pricePerClass = pricePerClass;
        this.classType = classType;
        this.description = description;
        this.teacherName = teacherName;
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

    public String getCourseTime() {
        return courseTime;
    }

    public void setCourseTime(String courseTime) {
        this.courseTime = courseTime;
    }

    public int getCapacity() {
        return capacity;
    }

    public void setCapacity(int capacity) {
        this.capacity = capacity;
    }

    public int getDuration() {
        return duration;
    }

    public void setDuration(int duration) {
        this.duration = duration;
    }

    public double getPricePerClass() {
        return pricePerClass;
    }

    public void setPricePerClass(double pricePerClass) {
        this.pricePerClass = pricePerClass;
    }

    public ClassType getClassType() {
        return classType;
    }

    public void setClassType(ClassType classType) {
        this.classType = classType;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getTeacherName() {
        return teacherName;
    }

    public void setTeacherName(String teacherName) {
        this.teacherName = teacherName;
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