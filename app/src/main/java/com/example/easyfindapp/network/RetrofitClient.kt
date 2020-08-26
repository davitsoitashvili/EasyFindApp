package com.example.easyfindapp.network

import retrofit2.Retrofit
import retrofit2.converter.scalars.ScalarsConverterFactory

private const val BASE_PATH = "http://finalprojectapi.somee.com/api/"
private val retrofit = Retrofit.Builder()
    .addConverterFactory(ScalarsConverterFactory.create())
    .baseUrl(BASE_PATH)
    .build()
val apiService: ApiService = retrofit.create(ApiService::class.java)