package ViewHolder;

import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.socialbook.R;

public class CommentViewHolder extends RecyclerView.ViewHolder {

    public TextView txtName, txtDate, txtTime, txtComment;
    public ImageView profileImg;

    public CommentViewHolder(@NonNull View itemView) {
        super(itemView);

        profileImg = itemView.findViewById(R.id.avatar);
        txtName = itemView.findViewById(R.id.name);
        txtDate = itemView.findViewById(R.id.date);
        txtComment = itemView.findViewById(R.id.message);
    }
}
