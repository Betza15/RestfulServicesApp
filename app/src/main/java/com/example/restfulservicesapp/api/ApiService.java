
package com.example.restfulservicesapp.api;

import com.example.restfulservicesapp.model.Report;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;

public interface ApiService {

    // GET: consultar todas las publicaciones
    @GET("posts")
    Call<List<Report>> getAllReports();

    // GET: consultar una publicación por su ID
    @GET("posts/{id}")
    Call<Report> getReportById(
            @retrofit2.http.Path("id") int reportId
    );

    // POST: crear una publicación de prueba
    @POST("posts")
    Call<Report> createReport(@Body Report report);
}
