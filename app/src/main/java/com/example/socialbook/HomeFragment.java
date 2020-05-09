package com.example.socialbook;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.bumptech.glide.Glide;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import Model.PublicPost;
import ViewHolder.PersonalPostViewHolder;
import ViewHolder.PublicPostViewHolder;
import pl.droidsonroids.gif.GifImageView;


/**
 * A simple {@link Fragment} subclass.
 */
public class HomeFragment extends Fragment {

    public HomeFragment() {
        // Required empty public constructor
    }

    private RecyclerView publicPostList;
    private DatabaseReference postReff, LikeReff;
    private GifImageView loading;
    private Boolean isLiked = false;
    private FirebaseUser user;
    String user_uid;



    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_home, container, false);
        publicPostList = view.findViewById(R.id.publicPostList);

        user = FirebaseAuth.getInstance().getCurrentUser();
        user_uid = user.getUid();
        loading = view.findViewById(R.id.loading);
        publicPostList.setHasFixedSize(true);
        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext());
        layoutManager.setStackFromEnd(true);
        layoutManager.setReverseLayout(true);
        publicPostList.setLayoutManager(layoutManager);
        postReff = FirebaseDatabase.getInstance().getReference().child("Posts");
        LikeReff = FirebaseDatabase.getInstance().getReference().child("Likes");
        return view;
    }

    @Override
    public void onStart() {
        super.onStart();

        FirebaseRecyclerOptions<PublicPost> options = new FirebaseRecyclerOptions.Builder<PublicPost>()
                .setQuery(postReff, PublicPost.class).build();

        FirebaseRecyclerAdapter<PublicPost, PublicPostViewHolder> adapter = new FirebaseRecyclerAdapter<PublicPost, PublicPostViewHolder>(options) {
            @Override
            protected void onBindViewHolder(@NonNull PublicPostViewHolder holder, int position, @NonNull PublicPost model) {

                final String postKey =getRef(position).getKey();

                holder.postDate.setText(model.getDate());
                holder.personName.setText(model.getName());
                holder.publicPostTitle.setText(model.getTitle());
                Glide.with(holder.avatarImage).load(model.getProfileurl()).into(holder.avatarImage);
                Glide.with(holder.postImage).load(model.getPostimage()).into(holder.postImage);
                loading.setVisibility(View.GONE);
                holder.comment.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent po = new Intent(getContext(), CommentActivity.class);
                        po.putExtra("PostKey", postKey);
                        startActivity(po);
                    }
                });

                holder.trackLikes(postKey);
                holder.like.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        isLiked = true;
                        LikeReff.addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                                if (isLiked.equals(true)){
                                    if (dataSnapshot.child(postKey).hasChild(user_uid)){
                                        LikeReff.child(postKey).child(user_uid).removeValue();
                                        isLiked = false;
                                    }else {
                                        LikeReff.child(postKey).child(user_uid).setValue(true);
                                        isLiked = false;
                                    }
                                }
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError databaseError) {

                            }
                        });
                    }
                });
            }

            @NonNull
            @Override
            public PublicPostViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.public_post_layout, parent,false);
                PublicPostViewHolder holder = new PublicPostViewHolder(view);
                return holder;
            }
        };
        publicPostList.setAdapter(adapter);
        adapter.startListening();
    }
}
