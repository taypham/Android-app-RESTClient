package com.csce4623.ahnelson.restclientexample.API;

/**
 * Created by ahnelson on 11/13/2017.
 */


import com.csce4623.ahnelson.restclientexample.Model.Comment;
import com.csce4623.ahnelson.restclientexample.Model.Post;
import com.csce4623.ahnelson.restclientexample.Model.User;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface PostAPI {
    @GET("posts/")
    Call<List<Post>> loadPosts();

    @GET("users/")
    Call<List<User>> loadUsers();

    @GET("posts/")
    Call<List<Post>> loadPostsByUserId(@Query("userId") int userId);


}
