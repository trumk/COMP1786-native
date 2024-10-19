package com.example.yogadminapp.api;

import com.example.yogadminapp.models.ClassType;
import com.example.yogadminapp.models.User;
import com.example.yogadminapp.models.YogaCourse;

import java.util.List;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Path;

public interface ApiService {
    @GET("class")
    Call<List<ClassType>> getAllClassTypes();

    @GET("class/{id}")
    Call<ClassType> getClassTypeById(@Path("id") String id);

    @POST("class")
    Call<ClassType> createClassType(@Body ClassType classType);

    @PUT("class/{id}")
    Call<ClassType> updateClassType(@Path("id") String id, @Body ClassType classType);

    @DELETE("class/{id}")
    Call<Void> deleteClassType(@Path("id") String id);

    @GET("auth/allUser")
    Call<List<User>> getAllUsers();

    @GET("admin/courses")
    Call<List<YogaCourse>> getAllCourses();

    @GET("admin/courses/not/{id}")
    Call<YogaCourse> getCourseById(@Path("id") String id);

    @POST("admin/courses")
    Call<YogaCourse> createCourse(@Body YogaCourse yogaCourse);

    @PUT("admin/courses/{id}")
    Call<YogaCourse> updateCourse(@Path("id") String id, @Body YogaCourse yogaCourse);

    @DELETE("admin/courses/{id}")
    Call<Void> deleteCourse(@Path("id") String id);
}
