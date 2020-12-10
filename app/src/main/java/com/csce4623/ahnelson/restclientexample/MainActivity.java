package com.csce4623.ahnelson.restclientexample;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Debug;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.csce4623.ahnelson.restclientexample.API.CommentAPI;
import com.csce4623.ahnelson.restclientexample.API.PostAPI;
import com.csce4623.ahnelson.restclientexample.API.UserAPI;
import com.csce4623.ahnelson.restclientexample.Model.Comment;
import com.csce4623.ahnelson.restclientexample.Model.Post;
import com.csce4623.ahnelson.restclientexample.Model.User;
import com.csce4623.ahnelson.restclientexample.View.PostView;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class MainActivity extends Activity  {

    ArrayList<Post> myPostList;
    ArrayList<User> myUserList;
    ListView lvPostVList;
    PostAdapter myPostAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        lvPostVList = (ListView)findViewById(R.id.lvPostList);
        lvPostVList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                itemClicked(parent, view, position,id);
            }
        });
        startQueryPosts();
        startQueryUsers();

    }

    @Override
    protected void onDestroy(){
        super.onDestroy();

    }


    void itemClicked(AdapterView<?> parent, View view, int position, long id){

        Intent myIntent = new Intent(this, PostView.class);
        myIntent.putExtra("postId",myPostList.get(position).getId());
        myIntent.putExtra("postTitle",myPostList.get(position).getTitle());
        myIntent.putExtra("postBody",myPostList.get(position).getBody());
        myIntent.putExtra("userName",myUserList.get(myPostList.get(position).getUserId()-1).getName());
        myIntent.putExtra("userId",Integer.toString(myUserList.get(myPostList.get(position).getUserId()-1).getId()));
        myIntent.putExtra("lat",String.valueOf(myUserList.get(myPostList.get(position).getUserId()-1).getAddress().getGeo().getLat()));
        myIntent.putExtra("lng",String.valueOf(myUserList.get(myPostList.get(position).getUserId()-1).getAddress().getGeo().getLng()));
        startActivity(myIntent);
    }



    static final String BASE_URL = "https://jsonplaceholder.typicode.com/";
    public void startQueryUsers(){
        Gson gson = new GsonBuilder()
                .setLenient()
                .create();

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addConverterFactory(GsonConverterFactory.create(gson))
                .build();

        UserAPI userAPI = retrofit.create(UserAPI.class);
        Call<List<User>> call = userAPI.loadUsers();
        call.enqueue(new Callback<List<User>>() {
            @Override
            public void onResponse(Call<List<User>> call, Response<List<User>> response) {

                if(response.isSuccessful()) {
                    myUserList = new ArrayList<User>(response.body());
                    for (User user:myUserList) {
                        Log.d("MainActivity","ID: " + user.getId());
                        Log.d("MainActivity","Lat: " + user.getAddress().getGeo().getLat());
                    }
                } else {
                    System.out.println(response.errorBody());
                }
                Debug.stopMethodTracing();

            }

            @Override
            public void onFailure(Call<List<User>> call, Throwable t) {
                t.printStackTrace();
            }
        });
    }

    public void startQueryPosts() {

        Debug.startMethodTracing("test");

        Gson gson = new GsonBuilder()
                .setLenient()
                .create();

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addConverterFactory(GsonConverterFactory.create(gson))
                .build();

        PostAPI postAPI = retrofit.create(PostAPI.class);


        Call<List<Post>> call = postAPI.loadPosts();
        call.enqueue(new Callback<List<Post>>() {
            @Override
            public void onResponse(Call<List<Post>> call, Response<List<Post>> response) {
                if(response.isSuccessful()) {
                    myPostList = new ArrayList<Post>(response.body());
                    myPostAdapter = new PostAdapter(getApplicationContext(),myPostList);
                    lvPostVList.setAdapter(myPostAdapter);
                    for (Post post:myPostList) {
                        Log.d("MainActivity","ID: " + post.getId());
                    }
                } else {
                    System.out.println(response.errorBody());
                }
                Debug.stopMethodTracing();
            }

            @Override
            public void onFailure(Call<List<Post>> call, Throwable t) {
                t.printStackTrace();
            }
        });
    }

    protected class PostAdapter extends ArrayAdapter<Post>{
        public PostAdapter(Context context, ArrayList<Post> posts) {
            super(context, 0, posts);
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            // Get the data item for this position
            Post post = getItem(position);
            // Check if an existing view is being reused, otherwise inflate the view
            if (convertView == null) {
                convertView = LayoutInflater.from(getContext()).inflate(R.layout.post_layout, parent, false);
            }
            // Lookup view for data population
            TextView tvId = (TextView) convertView.findViewById(R.id.tvId);
            TextView tvTitle = (TextView) convertView.findViewById(R.id.tvTitle);
            // Populate the data into the template view using the data object
            tvTitle.setText(post.getTitle());
            tvId.setText(Integer.toString(post.getId()));
            // Return the completed view to render on screen
            return convertView;
        }
    }
}
