package com.example.socialbook;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.util.Calendar;

import Model.PersonalPosts;
import Model.Search;
import ViewHolder.PersonalPostViewHolder;
import de.hdodenhof.circleimageview.CircleImageView;

public class OthersProfileActivity extends AppCompatActivity {

    private CircleImageView avatar;
    private TextView name, Email;
    private RecyclerView postList;
    private String id ="";
    private DatabaseReference UserReff;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_others_profile);

        UserReff = FirebaseDatabase.getInstance().getReference().child("Users");
        id = getIntent().getStringExtra("userID");
        avatar  = findViewById(R.id.avatar);
        name = findViewById(R.id.avatar_name);
        Email = findViewById(R.id.email);
        postList = findViewById(R.id.PostList);
        postList.setHasFixedSize(true);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL,false);
        postList.setLayoutManager(layoutManager);

        getPosts(id);
        getDetails(id);


    }


    private void getPosts(String id) {
        DatabaseReference postRef = FirebaseDatabase.getInstance().getReference().child("Users").child(id).child("Posts");
        FirebaseRecyclerOptions<PersonalPosts> options = new FirebaseRecyclerOptions.Builder<PersonalPosts>()
                .setQuery(postRef, PersonalPosts.class).build();

        FirebaseRecyclerAdapter<PersonalPosts, PersonalPostViewHolder> adapter = new FirebaseRecyclerAdapter<PersonalPosts, PersonalPostViewHolder>(options) {
            @Override
            protected void onBindViewHolder(@NonNull PersonalPostViewHolder holder, int position, @NonNull PersonalPosts model) {

                Glide.with(holder.personalPostImage).load(model.getPostimage()).into(holder.personalPostImage);
            }

            @NonNull
            @Override
            public PersonalPostViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.personal_post, parent,false);
                PersonalPostViewHolder holder = new PersonalPostViewHolder(view);
                return holder;
            }
        };
        postList.setAdapter(adapter);
        adapter.startListening();
    }

    private void getDetails(final String id) {
        UserReff.child(id).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                if (dataSnapshot.exists()){
                    Search model = dataSnapshot.getValue(Search.class);

                    name.setText(model.getName());
                    Email.setText(model.getEmail());
                    Glide.with(avatar).load(model.getURL()).into(avatar);


                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }
}
