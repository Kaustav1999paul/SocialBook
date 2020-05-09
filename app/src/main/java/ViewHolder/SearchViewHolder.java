package ViewHolder;

import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.socialbook.R;

import de.hdodenhof.circleimageview.CircleImageView;


public class SearchViewHolder extends RecyclerView.ViewHolder {

    public CircleImageView searchAvatarImage;
    public TextView searchName;


    public SearchViewHolder(@NonNull View itemView) {
        super(itemView);
        searchName = itemView.findViewById(R.id.searchName);
        searchAvatarImage = itemView.findViewById(R.id.searchAvatarImage);
    }
}
