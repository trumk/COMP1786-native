package com.example.yogadminapp.models;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class Class implements Serializable {
    @SerializedName("_id")
    private String id;

    @SerializedName("className")
    private String className;

    @SerializedName("description")
    private String description;

    @SerializedName("teacher")
    private String teacher;

    @SerializedName("date")
    private String date;

    @SerializedName("duration")
    private int duration;

    @SerializedName("__v")
    private int version;

    public Class() {}

    public Class(String className, String description, String teacher, String date, int duration) {
        this.className = className;
        this.description = description;
        this.teacher = teacher;
        this.date = date;
        this.duration = duration;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getTypeName() {
        return className;
    }

    public void setTypeName(String typeName) {
        this.className = className;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getTeacher() {
        return teacher;
    }

    public void setTeacher(String teacher) {
        this.teacher = teacher;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public int getDuration() {
        return duration;
    }

    public void setDuration(int duration) {
        this.duration = duration;
    }

    public int getVersion() {
        return version;
    }

    public void setVersion(int version) {
        this.version = version;
    }

    @Override
    public String toString() {
        return "Class Type: " + className + "\n" +
                "Description: " + description + "\n" +
                "Teacher: " + teacher + "\n" +
                "Date: " + date + "\n" +
                "Duration: " + duration + " minutes";
    }
}