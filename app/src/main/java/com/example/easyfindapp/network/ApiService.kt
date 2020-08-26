package com.example.easyfindapp.network

import com.google.gson.JsonObject
import okhttp3.RequestBody
import org.json.JSONObject
import retrofit2.Call
import retrofit2.http.*

interface ApiService {
    @GET("{path}/{id}")
    fun requestGetWithID(@Path("path") path: String, @Path("id") id: String): Call<String>

    @GET("{path}")
    fun requestGet(@Path("path") path: String): Call<String>

    @FormUrlEncoded
    @POST("{path}")
    fun requestPOST(
        @Path("path") path: String,
        @FieldMap parameters: MutableMap<String, String>
    ): Call<String>
}