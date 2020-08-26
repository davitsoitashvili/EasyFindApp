package com.example.easyfindapp.network

interface ResponseCallback {
    fun onSuccess(response : String)
    fun onFailure(response : String)
    fun onError(response: String)
}