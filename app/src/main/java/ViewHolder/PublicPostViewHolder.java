package ViewHolder;

import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.socialbook.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import de.hdodenhof.circleimageview.CircleImageView;

public class PublicPostViewHolder extends RecyclerView.ViewHolder {

    public CircleImageView avatarImage;
    public TextView personName, postDate, publicPostTitle;
    private TextView likesCount;
    public ImageView postImage, comment, like;
    private DatabaseReference likeRef;
    private String User;
    int countLikes;
    public PublicPostViewHolder(@NonNull View itemView) {
        super(itemView);

        avatarImage = itemView.findViewById(R.id.avatarImage);
        personName = itemView.findViewById(R.id.personName);
        postDate = itemView.findViewById(R.id.personDate);
        postImage = itemView.findViewById(R.id.postImage);
        publicPostTitle = itemView.findViewById(R.id.publicPostTitle);
        comment = itemView.findViewById(R.id.comment);
        likesCount = itemView.findViewById(R.id.likesCount);
        like = itemView.findViewById(R.id.like);
        likeRef = FirebaseDatabase.getInstance().getReference().child("Likes");
        User = FirebaseAuth.getInstance().getCurrentUser().getUid();
    }

    public void trackLikes(final String postKey) {
        likeRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                if (dataSnapshot.child(postKey).hasChild(User)){

                       countLikes = (int) dataSnapshot.child(postKey).getChildrenCount();
                       like.setImageResource(R.drawable.ic_liked);
                       likesCount.setText((Integer.toString(countLikes))+" Likes");
                }
                else {
                    countLikes = (int) dataSnapshot.child(postKey).getChildrenCount();
                    like.setImageResource(R.drawable.ic_favorite);
                    likesCount.setText((Integer.toString(countLikes))+" Likes");
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }
}
