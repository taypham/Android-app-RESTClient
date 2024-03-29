package com.csce4623.ahnelson.restclientexample.View;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.csce4623.ahnelson.restclientexample.API.CommentAPI;
import com.csce4623.ahnelson.restclientexample.Model.Comment;
import com.csce4623.ahnelson.restclientexample.Model.User;
import com.csce4623.ahnelson.restclientexample.R;
import com.csce4623.ahnelson.restclientexample.UserView;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class PostView extends Activity implements Callback<List<Comment>> {

    ArrayList<Comment> myCommentsList;
    CommentAdapter myCommentsAdapter;
    ListView lvComments;
    Comment comment;
    private EditText cmtId, cmtName, cmtEmail,cmtBody;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        User user;
        setContentView(R.layout.activity_post_view);
        TextView tvPostTitle = (TextView)findViewById(R.id.tvPostTitle);
        TextView tvPostBody = (TextView)findViewById(R.id.tvPostBody);
        TextView tvUserName = (TextView)findViewById(R.id.tvUserName);

        cmtId = (EditText) findViewById(R.id.cmtId);
        cmtName = (EditText)findViewById(R.id.cmtName);
        cmtEmail = (EditText)findViewById(R.id.cmtEmail);
        cmtBody = (EditText)findViewById(R.id.cmtBody);

        final String userName = this.getIntent().getStringExtra("userName");
        final String userId =  this.getIntent().getStringExtra("userId");

        final String lat = this.getIntent().getStringExtra("lat");
        final String lng =  this.getIntent().getStringExtra("lng");


        tvPostBody.setText(this.getIntent().getStringExtra("postBody"));
        tvPostTitle.setText(this.getIntent().getStringExtra("postTitle"));
        tvUserName.setText(this.getIntent().getStringExtra("userName"));

        findViewById(R.id.tvUserName).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(getApplicationContext(), "UserName clicked", Toast.LENGTH_SHORT).show();
                Intent myIntent = new Intent(getApplicationContext(), UserView.class);
                myIntent.putExtra("userId",userId);
                myIntent.putExtra("userName",userName);
                myIntent.putExtra("lat",lat);
                myIntent.putExtra("lng",lng);
                startActivity(myIntent);
            }
        });
        findViewById(R.id.btnMakeComment).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                makeNewComment();
            }
        });
        startQuery();

    }

    static final String BASE_URL = "https://jsonplaceholder.typicode.com/";

    public void startQuery() {

        Gson gson = new GsonBuilder()
                .setLenient()
                .create();

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addConverterFactory(GsonConverterFactory.create(gson))
                .build();
        lvComments = (ListView)findViewById(R.id.lvComments);
        CommentAPI commentAPI = retrofit.create(CommentAPI.class);
        Call<List<Comment>> call = commentAPI.loadCommentByPostId(getIntent().getIntExtra("postId",0));
        call.enqueue(this);
    }

    public void makeNewComment(){
        Integer id = Integer.parseInt(cmtId.getText().toString());
        String name = cmtName.getText().toString();
        String email = (cmtEmail.getText().toString());
        String body = (cmtBody.getText().toString());

        Gson gson = new GsonBuilder()
                .setLenient()
                .create();

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addConverterFactory(GsonConverterFactory.create(gson))
                .build();
        CommentAPI commentAPI = retrofit.create(CommentAPI.class);
        Call<Comment> call = commentAPI.addCommentToPost(id,name,email,body);
        call.enqueue(new Callback<Comment>() {
            @Override
            public void onResponse(Call<Comment> call, Response<Comment> response) {
                Comment myComment = response.body();
                //myCommentsList.add(myComment);
                //myCommentsAdapter = new PostView.CommentAdapter(getApplicationContext(),myCommentsList);
                //lvComments.setAdapter(myCommentsAdapter);

                Log.d("PostView","Post Created Successfully at id: " + myComment.getId()+ myComment.getName());
            }

            @Override
            public void onFailure(Call<Comment> call, Throwable t) {
                Log.d("PostView","Post Not Created");
            }
        });
    }

    @Override
    public void onResponse(Call<List<Comment>> call, Response<List<Comment>> response) {
        if(response.isSuccessful()) {
            myCommentsList = new ArrayList<Comment>(response.body());
            myCommentsAdapter = new PostView.CommentAdapter(this,myCommentsList);
            lvComments.setAdapter(myCommentsAdapter);
            for (Comment comment:myCommentsList) {
                Log.d("MainActivity","ID: " + comment.getId());
            }
        } else {
            System.out.println(response.errorBody());
        }
    }

    @Override
    public void onFailure(Call<List<Comment>> call, Throwable t) {
        t.printStackTrace();
    }

    protected class CommentAdapter extends ArrayAdapter<Comment>{
        public CommentAdapter(Context context, ArrayList<Comment> posts) {
            super(context, 0, posts);
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            // Get the data item for this position
            Comment comment = getItem(position);

            // Check if an existing view is being reused, otherwise inflate the view
            if (convertView == null) {
                convertView = LayoutInflater.from(getContext()).inflate(R.layout.comment_layout, parent, false);
            }
            // Lookup view for data population
            TextView tvCommentId = (TextView) convertView.findViewById(R.id.tvCommentId);
            TextView tvCommentBody = (TextView) convertView.findViewById(R.id.tvCommentBody);
            TextView tvCommentTitle = (TextView) convertView.findViewById(R.id.tvCommentTitle);
            TextView tvCommentEmail = (TextView) convertView.findViewById(R.id.tvCommentEmail);

            // Populate the data into the template view using the data object
            tvCommentId.setText(Integer.toString(comment.getId()));
            tvCommentBody.setText(comment.getBody());
            tvCommentTitle.setText(comment.getName());
            tvCommentEmail.setText("Email:"+comment.getEmail());

            // Return the completed view to render on screen
            return convertView;
        }
    }
}
