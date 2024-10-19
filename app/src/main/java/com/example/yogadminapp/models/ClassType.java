package com.example.yogadminapp.models;

import com.google.gson.annotations.SerializedName;

public class ClassType {
    @SerializedName("_id")
    private String id;

    @SerializedName("typeName")
    private String typeName;

    public ClassType() {}

    public ClassType(String id, String typeName) {
        this.id = id;
        this.typeName = typeName;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getTypeName() {
        return typeName;
    }

    public void setTypeName(String typeName) {
        this.typeName = typeName;
    }

    @Override
    public String toString() {
        return typeName; // Display the type name in the spinner
    }
}
