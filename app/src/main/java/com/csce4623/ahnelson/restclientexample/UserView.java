package com.csce4623.ahnelson.restclientexample;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.os.Debug;
import android.util.Log;
import android.widget.TextView;

import com.csce4623.ahnelson.restclientexample.API.PostAPI;
import com.csce4623.ahnelson.restclientexample.API.UserAPI;
import com.csce4623.ahnelson.restclientexample.Model.Post;
import com.csce4623.ahnelson.restclientexample.Model.User;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.text.BreakIterator;
import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;


public class UserView extends AppCompatActivity {

    static final String BASE_URL = "https://jsonplaceholder.typicode.com/";
    ArrayList<User> myUserList;
    User user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_view);



        String name =  this.getIntent().getStringExtra("userName");
        int id = Integer.parseInt(this.getIntent().getStringExtra("userId"));
        TextView tvName = (TextView)findViewById(R.id.tvName);
        TextView tvUsername = (TextView)findViewById(R.id.tvUserName);
        TextView tvEmail = (TextView)findViewById(R.id.tvUserEmail);
        TextView tvPhone= (TextView)findViewById(R.id.tvPhone);

        tvName.setText(id +". "+ name );
        startQuery(id);

       // String email = myUserList.get(id).getEmail();
        //tvEmail.setText(email);



    }
    public void startQuery(int id) {
        Gson gson = new GsonBuilder()
                .setLenient()
                .create();

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addConverterFactory(GsonConverterFactory.create(gson))
                .build();

        UserAPI userAPI = retrofit.create(UserAPI.class);
        Call<User> call = userAPI.getUser(id);
        call.enqueue(new Callback<User>() {
            @Override
            public void onResponse(Call<User> call, Response<User> response) {
                if(response.isSuccessful()) {
                    user = (response.body());
                    Log.d("UserActivity","ID: " + user.getId());

                } else {
                    System.out.println(response.errorBody());
                }
                Debug.stopMethodTracing();
            }

            @Override
            public void onFailure(Call<User> call, Throwable t) {

            }
        });
    }

    @Override
    protected void onDestroy(){
        super.onDestroy();

    }



}