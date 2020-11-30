package com.csce4623.ahnelson.restclientexample.API;

import com.csce4623.ahnelson.restclientexample.Model.User;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface UserAPI {
    @GET("users/{id}/")
    Call<User> getUser(@Path("id") int userId);

    @GET("users/")
    Call<List<User>> loadUsers();
}
