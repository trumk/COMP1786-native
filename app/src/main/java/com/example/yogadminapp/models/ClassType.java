package com.example.yogadminapp.models;

import com.google.gson.annotations.SerializedName;

public class ClassType {
    @SerializedName("_id")
    private String id;

    @SerializedName("typeName")
    private String typeName;

    @SerializedName("__v")
    private int version; // Thêm trường để ánh xạ __v

    public ClassType() {}

    // Constructor hai tham số (id và typeName)
    public ClassType(String id, String typeName) {
        this.id = id;
        this.typeName = typeName;
    }

    // Constructor ba tham số (id, typeName, version)
    public ClassType(String id, String typeName, int version) {
        this.id = id;
        this.typeName = typeName;
        this.version = version;
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

    public int getVersion() {
        return version;
    }

    public void setVersion(int version) {
        this.version = version;
    }

    @Override
    public String toString() {
        return typeName; // Hiển thị typeName trong Spinner
    }
}
