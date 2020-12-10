package com.csce4623.ahnelson.restclientexample;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Debug;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import com.csce4623.ahnelson.restclientexample.API.PostAPI;
import com.csce4623.ahnelson.restclientexample.API.UserAPI;
import com.csce4623.ahnelson.restclientexample.Model.Comment;
import com.csce4623.ahnelson.restclientexample.Model.Post;
import com.csce4623.ahnelson.restclientexample.Model.User;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
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


public class UserView extends AppCompatActivity implements OnMapReadyCallback {

    static final String BASE_URL = "https://jsonplaceholder.typicode.com/";
    ArrayList<User> myUserList;
    User user;
    PostAdapter myPostAdapter;
    ArrayList<Post> myPostList;
    ListView lvUserPosts;

    private TextView tvName;
    private TextView tvUsername;
    private TextView tvEmail;
    private TextView tvPhone;
    private TextView tvWebsite;
    private TextView tvGeo;
    private TextView title;


    private GoogleMap mMap;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_view);

        String name =  this.getIntent().getStringExtra("userName");
        final int id = Integer.parseInt(this.getIntent().getStringExtra("userId"));
        String lat =  (this.getIntent().getStringExtra("lat"));
        String lng =  (this.getIntent().getStringExtra("lng"));
        tvName = (TextView)findViewById(R.id.tvName);
        tvUsername = (TextView)findViewById(R.id.tvUserName);
        tvEmail = (TextView)findViewById(R.id.tvUserEmail);
        tvPhone= (TextView)findViewById(R.id.tvPhone);
        tvWebsite= (TextView)findViewById(R.id.tvWebsite);
        tvGeo= (TextView)findViewById(R.id.tvGeo);
        title = (TextView)findViewById(R.id.postTitle);
        lvUserPosts = (ListView)findViewById(R.id.lvUserPosts);

        tvName.setText(name);
        tvGeo.setText("Location: "+lat+", "+lng);
        title.setText("Others posts from "+name);

        startQueryUser(id);
        startQueryPosts(id);

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.mapView);
        mapFragment.getMapAsync(this);


    }

    @Override
    protected void onDestroy(){
        super.onDestroy();
    }


    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        String name =  this.getIntent().getStringExtra("userName");
        double lat =  Double.parseDouble(this.getIntent().getStringExtra("lat"));
        double lng =  Double.parseDouble(this.getIntent().getStringExtra("lng"));
        // Add a marker in User location and move the camera
        LatLng user = new LatLng(lat, lng);
        mMap.addMarker(new MarkerOptions().position(user).title(name+": "+lat+", "+lng ));
        mMap.moveCamera(CameraUpdateFactory.newLatLng(user));

    }
    public void startQueryUser(int id){

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
                    tvUsername.setText("username: "+user.getUsername());
                    tvEmail.setText("Email: "+user.getEmail());
                    tvPhone.setText("Phone: "+user.getPhone());
                    tvWebsite.setText("Website: "+user.getWebsite());

                } else {
                    System.out.println(response.errorBody());
                }
                Debug.stopMethodTracing();
            }

            @Override
            public void onFailure(Call<User> call, Throwable t) {
                t.printStackTrace();
            }
        });
    }

    public void startQueryPosts(int id){
        Gson gson = new GsonBuilder()
                .setLenient()
                .create();

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addConverterFactory(GsonConverterFactory.create(gson))
                .build();
        PostAPI postAPI = retrofit.create(PostAPI.class);
        Call<List<Post>> call2 = postAPI.loadPostsByUserId(id);
        call2.enqueue(new Callback<List<Post>>() {
            @Override
            public void onResponse(Call<List<Post>> call, Response<List<Post>> response) {
                if(response.isSuccessful()) {
                    myPostList = new ArrayList<Post>(response.body());
                    myPostAdapter = new PostAdapter(getApplicationContext(),myPostList);
                    lvUserPosts.setAdapter(myPostAdapter);
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

            }
        });
    }

    protected class PostAdapter extends ArrayAdapter<Post> {
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