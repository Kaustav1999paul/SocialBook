package com.example.socialbook;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.service.notification.StatusBarNotification;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;

import Model.Comments;
import Model.PublicPost;
import ViewHolder.CommentViewHolder;

public class CommentActivity extends AppCompatActivity {

    private ImageView image;
    private RecyclerView list;
    private String postID = "";
    private EditText textComment;
    private FloatingActionButton postComment;
    private DatabaseReference UserRef, PostRef;
    private FirebaseAuth mAuth;
    private String saveCurrentDate, saveCurrentTime, RandomKey;
    String uida;
    private TextView noComments;

    @Override
    protected void onStart() {
        super.onStart();
        ViewComments();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_comment);

        mAuth = FirebaseAuth.getInstance();
        image = findViewById(R.id.postImage);
        list = findViewById(R.id.commentList);
        textComment = findViewById(R.id.textComment);
        postComment = findViewById(R.id.postComment);
        noComments = findViewById(R.id.noComments);
        postID = getIntent().getStringExtra("PostKey");
        list.setHasFixedSize(true);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        layoutManager.setStackFromEnd(true);
        layoutManager.setReverseLayout(true);
        list.setLayoutManager(layoutManager);

        UserRef = FirebaseDatabase.getInstance().getReference().child("Users");
        PostRef = FirebaseDatabase.getInstance().getReference().child("Posts").child(postID).child("comments");
        FirebaseUser user = mAuth.getCurrentUser();
        uida = user.getUid();

        getPostDetails(postID);

        postComment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                UserRef.child(uida).addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                        if (dataSnapshot.exists()){
                            String userName = dataSnapshot.child("Name").getValue().toString();
                            String avatar = dataSnapshot.child("URL").getValue().toString();
                            validateUser(userName,avatar);
                            textComment.setText("");
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });
            }
        });
    }

    private void validateUser(String userName, String avatar) {

        String comment = textComment.getText().toString();
        if (comment.isEmpty()){
            Toast.makeText(this, "No comments written yet!", Toast.LENGTH_SHORT).show();
        }else{
            Calendar cal = Calendar.getInstance();
            SimpleDateFormat currDate = new SimpleDateFormat("dd-MM-YYY");
            saveCurrentDate = currDate.format(cal.getTime());

            Calendar time = Calendar.getInstance();
            SimpleDateFormat currTime = new SimpleDateFormat("HH:mm:ss a");
            saveCurrentTime = currTime.format(time.getTime());

            RandomKey = uida + saveCurrentDate +saveCurrentTime;

            HashMap<String , Object> userdataMap = new HashMap<>();
            userdataMap.put("name" , userName);
            userdataMap.put("comment" , comment);
            userdataMap.put("profileurl" , avatar);
            userdataMap.put("date" , saveCurrentDate);
            userdataMap.put("time" , saveCurrentTime);

            PostRef.child(RandomKey).updateChildren(userdataMap).addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    Toast.makeText(CommentActivity.this, "Comment added", Toast.LENGTH_SHORT).show();
                }
            });
        }
    }

    private void getPostDetails(String postID) {
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference().child("Posts");
        reference.child(postID).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                PublicPost p = dataSnapshot.getValue(PublicPost.class);
                Glide.with(image).load(p.getPostimage()).into(image);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void ViewComments() {

        FirebaseRecyclerOptions<Comments> options = new FirebaseRecyclerOptions.Builder<Comments>()
                .setQuery(PostRef, Comments.class).build();

        FirebaseRecyclerAdapter<Comments, CommentViewHolder> adapter = new FirebaseRecyclerAdapter<Comments, CommentViewHolder>(options) {
            @Override
            protected void onBindViewHolder(@NonNull CommentViewHolder holder, int position, @NonNull Comments model) {

                holder.txtDate.setText(model.getDate());
                holder.txtComment.setText(model.getComment());
                holder.txtName.setText(model.getName());
                noComments.setVisibility(View.GONE);
                Glide.with(holder.profileImg).load(model.getProfileurl()).into(holder.profileImg);
            }

            @NonNull
            @Override
            public CommentViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.comment_layout, parent,false);
                CommentViewHolder holder = new CommentViewHolder(view);
                return holder;
            }
        };
        list.setAdapter(adapter);
        adapter.startListening();
    }
}
