package com.example.socialbook;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

import com.bumptech.glide.Glide;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import Model.Search;
import ViewHolder.SearchViewHolder;


/**
 * A simple {@link Fragment} subclass.
 */
public class SearchFragment extends Fragment {

    public SearchFragment() {
        // Required empty public constructor
    }

    private FloatingActionButton searchButton;
    private EditText searchText;
    private RecyclerView searchList;
    private DatabaseReference searchRef;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_search, container, false);
        searchButton = view.findViewById(R.id.searchButton);
        searchText = view.findViewById(R.id.searchText);
        searchList = view.findViewById(R.id.searchList);
        searchList.setHasFixedSize(true);
        GridLayoutManager layoutManager = new GridLayoutManager(getContext(), 2);
        searchList.setLayoutManager(layoutManager);

        searchRef = FirebaseDatabase.getInstance().getReference().child("Users");
        searchButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String text = searchText.getText().toString();

                getInformation(text);
            }
        });

        return view;
    }

    private void getInformation(String text) {

        FirebaseRecyclerOptions<Search> options = new FirebaseRecyclerOptions.Builder<Search>()
                .setQuery(searchRef.orderByChild("Name").startAt(text), Search.class).build();

        FirebaseRecyclerAdapter<Search, SearchViewHolder> adapter = new FirebaseRecyclerAdapter<Search, SearchViewHolder>(options) {
            @Override
            protected void onBindViewHolder(@NonNull SearchViewHolder holder, int position, @NonNull final Search model) {

                Glide.with(holder.searchAvatarImage).load(model.getURL()).into(holder.searchAvatarImage);
                holder.searchName.setText(model.getName());
                holder.itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent(getActivity(), OthersProfileActivity.class);
                        intent.putExtra("userID", model.getUserID());
                        startActivity(intent);
                    }
                });
            }

            @NonNull
            @Override
            public SearchViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.search_layout, parent,false);
                SearchViewHolder holder = new SearchViewHolder(view);
                return holder;
            }
        };
        searchList.setAdapter(adapter);
        adapter.startListening();
    }
}
