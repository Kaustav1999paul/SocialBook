package com.example.socialbook;

import android.app.Dialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;

import Model.PersonalPosts;
import ViewHolder.PersonalPostViewHolder;
import de.hdodenhof.circleimageview.CircleImageView;
import pl.droidsonroids.gif.GifImageView;

import static android.app.Activity.RESULT_OK;


public class AccountFragment extends Fragment {

    public AccountFragment() {
        // Required empty public constructor
    }

    private CircleImageView avatar;
    private TextView name, Email, emptyPost;
    private FloatingActionButton logout;
    FirebaseAuth mAuth;
    FirebaseAuth.AuthStateListener mAuthListener;
    private Button newPost;
    private Dialog accountDialog;
    private DatabaseReference databaseReference, postReference;
    private StorageReference storageReference;
    private String saveCurrentDate, saveCurrentTime, randomPostName, downloadUrl, title;
    private static final int abc = 1;
    private Uri imageUri;
    private ImageView a;
    private GifImageView loading;
    private EditText c;
    private RecyclerView personalPostList;
    String key;

    @Override
    public void onStart() {
        super.onStart();
        mAuth.addAuthStateListener(mAuthListener);

        FirebaseRecyclerOptions<PersonalPosts> options = new FirebaseRecyclerOptions.Builder<PersonalPosts>()
                .setQuery(postReference, PersonalPosts.class).build();

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
        personalPostList.setAdapter(adapter);
        adapter.startListening();

    }


    private void Addphoto() {
        Intent gallerIntent = new Intent();
        gallerIntent.setAction(Intent.ACTION_GET_CONTENT);
        gallerIntent.setType("image/");
        startActivityForResult(gallerIntent, abc);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == abc && resultCode == RESULT_OK && data != null){
            imageUri = data.getData();
            a.setImageURI(imageUri);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_account, container, false);

        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        avatar  = view.findViewById(R.id.avatar);
        name = view.findViewById(R.id.avatar_name);
        logout = view.findViewById(R.id.logout);
        Email = view.findViewById(R.id.email);
        mAuth = FirebaseAuth.getInstance();
        newPost = view.findViewById(R.id.newPost);
        accountDialog = new Dialog(getContext());
        personalPostList = view.findViewById(R.id.personalPostList);
        databaseReference = FirebaseDatabase.getInstance().getReference().child("Posts");
        storageReference = FirebaseStorage.getInstance().getReference().child("PostImages");



        personalPostList.setHasFixedSize(true);
        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL,false);
        layoutManager.setStackFromEnd(true);
        layoutManager.setReverseLayout(true);
        personalPostList.setLayoutManager(layoutManager);



        newPost.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDialog();
            }
        });


        if (user != null) {
            // Name, email address, and profile photo Url
            String user_name = user.getDisplayName();
            String email = user.getEmail();
            Uri photoUrl = user.getPhotoUrl();
            String user_uid = user.getUid();

            name.setText(user_name);
            Glide.with(avatar).load(photoUrl).into(avatar);
            Email.setText(email);

            postReference = FirebaseDatabase.getInstance().getReference().child("Users").child(user_uid).child("Posts");
        }

        mAuthListener= new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                if (firebaseAuth.getCurrentUser() == null){
                    Intent intent = new Intent(getActivity(), Register.class);
                    startActivity(intent);
                }
            }
        };
        logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mAuth.signOut();
            }
        });
        return view;
    }

    private void showDialog() {
        accountDialog.setContentView(R.layout.add_new_post_layout);
        accountDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        accountDialog.setCanceledOnTouchOutside(true);
        accountDialog.show();

        loading = accountDialog.findViewById(R.id.loading);
        a = accountDialog.findViewById(R.id.addPhoto);
        FloatingActionButton b = accountDialog.findViewById(R.id.post);
        c = accountDialog.findViewById(R.id.postTitle);

        b.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addPost();
            }
        });


        a.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Addphoto();
            }
        });
    }

    private void addPost() {
        title = c.getText().toString();
        loading.setVisibility(View.VISIBLE);
        accountDialog.setCanceledOnTouchOutside(false);
        Calendar cal = Calendar.getInstance();
        SimpleDateFormat currDate = new SimpleDateFormat("dd-MM-YYY");
        saveCurrentDate = currDate.format(cal.getTime());
        Calendar time = Calendar.getInstance();
        SimpleDateFormat currTime = new SimpleDateFormat("HH:mm:ss a");
        saveCurrentTime = currTime.format(time.getTime());
        randomPostName = saveCurrentDate+saveCurrentTime;



        final StorageReference filePath = storageReference.child(imageUri.getLastPathSegment()+randomPostName+ ".jpg");
        final UploadTask uploadTask = filePath.putFile(imageUri);
        uploadTask.addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
                Task<Uri> urlTask = uploadTask.continueWithTask(new Continuation<UploadTask.TaskSnapshot, Task<Uri>>() {
                    @Override
                    public Task<Uri> then(@NonNull Task<UploadTask.TaskSnapshot> task) throws Exception {
                        downloadUrl = filePath.getDownloadUrl().toString();
                        return filePath.getDownloadUrl();
                    }
                }).addOnCompleteListener(new OnCompleteListener<Uri>() {
                    @Override
                    public void onComplete(@NonNull Task<Uri> task) {
                        if (task.isSuccessful()){
                            downloadUrl = task.getResult().toString();


                            storeData(title);
                        }else {
                            String m = task.getException().getMessage();
                            Toast.makeText(getContext(), "Error: "+ m, Toast.LENGTH_SHORT).show();
                        }
                    }
                });
            }
        });
    }

    private void storeData(final String title) {

        FirebaseUser user = mAuth.getCurrentUser();
        String namea = user.getDisplayName();
        Uri photoUrla = user.getPhotoUrl();
        String profileUrl = photoUrla.toString();
        final String uida = user.getUid();

        key = uida+randomPostName;
        HashMap<String , Object> userdataMap = new HashMap<>();
        userdataMap.put("name" , namea);
        userdataMap.put("postimage" , downloadUrl);
        userdataMap.put("profileurl" , profileUrl);
        userdataMap.put("date" , saveCurrentDate);
        userdataMap.put("time" , saveCurrentTime);
        userdataMap.put("title" , title);

        databaseReference.child(key).updateChildren(userdataMap).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()){
                    storeDatainUser(downloadUrl, uida, title, key);
                    loading.setVisibility(View.INVISIBLE);
                    accountDialog.dismiss();
                }else {
                    String mess = task.getException().getMessage();
                    Toast.makeText(getContext(), "Error: "+mess, Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void storeDatainUser(String downloadUrl, String uida, String title, String key) {

        final DatabaseReference ref = FirebaseDatabase.getInstance().getReference().child("Users").child(uida);
        HashMap<String , Object> user = new HashMap<>();
        user.put("postimage", downloadUrl);
        user.put("title", title);

        ref.child("Posts").child(key).updateChildren(user);
    }


}
