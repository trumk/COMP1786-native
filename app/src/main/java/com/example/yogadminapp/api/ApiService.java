package com.example.yogadminapp.api;

import com.example.yogadminapp.models.Class;
import com.example.yogadminapp.models.Order;
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
import retrofit2.http.Query;

public interface ApiService {
    @GET("admin/courses/class")
    Call<List<Class>> getAllClassTypes();

    @POST("admin/courses/{courseId}/class")
    Call<Class> addClassTypeToCourse(@Path("courseId") String courseId, @Body Class aClass);

    @PUT("admin/courses/class/{id}")
    Call<Class> updateClassTypeInCourse(@Path("id") String id, @Body Class aClass);

    @DELETE("admin/courses/{courseId}/class/{id}")
    Call<Void> removeClassTypeFromCourse(@Path("courseId") String courseId, @Path("id") String classTypeId);

    @GET("auth/allUser")
    Call<List<User>> getAllUsers();

    @GET("admin/courses/search")
    Call<List<YogaCourse>> searchCourses(@Query("teacherName") String teacherName, @Query("dayOfWeek") String dayOfWeek);

    @GET("admin/courses")
    Call<List<YogaCourse>> getAllCourses();

    @GET("admin/courses/ad/{id}")
    Call<YogaCourse> getCourseById(@Path("id") String id);

    @POST("admin/courses")
    Call<YogaCourse> createCourse(@Body YogaCourse yogaCourse);

    @PUT("admin/courses/{id}")
    Call<YogaCourse> updateCourse(@Path("id") String id, @Body YogaCourse yogaCourse);

    @DELETE("admin/courses/{id}")
    Call<Void> deleteCourse(@Path("id") String id);

    @GET("order")
    Call<List<Order>> getOrders();

    @PUT("order/update-status")
    Call<Order> updateOrderStatus(@Body Order.UpdateOrderRequest request);
}
