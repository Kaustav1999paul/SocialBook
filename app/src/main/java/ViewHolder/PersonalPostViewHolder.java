package ViewHolder;

import android.view.View;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.socialbook.R;

public class PersonalPostViewHolder extends RecyclerView.ViewHolder{

    public ImageView personalPostImage;

    public PersonalPostViewHolder(@NonNull View itemView) {
        super(itemView);
        personalPostImage = itemView.findViewById(R.id.personalImage);
    }
}

