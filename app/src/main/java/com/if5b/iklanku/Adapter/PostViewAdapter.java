package com.if5b.iklanku.Adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.if5b.iklanku.Model.Post;
import com.if5b.iklanku.R;

import java.util.ArrayList;
import java.util.List;

public class PostViewAdapter extends RecyclerView.Adapter<PostViewAdapter.ViewHolder> {
    private List<Post> data = new ArrayList<>();
    private AdapterView.OnItemLongClickListener<Post> itemLongClickListener;

    public void setData(List<Post> data) {
        this.data = data;
        this.itemLongClickListener = itemLongClickListener;
        notifyDataSetChanged();

    }
    @NonNull
    @Override
    public PostViewAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.activity_post, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull PostViewAdapter.ViewHolder holder, int position) {
        Post post = data.get(position);
        int pos = holder.getAdapterPosition();
        holder.tvUsername.setText(post.getUsername());
        holder.tvContent.setText(post.getContent());
        holder.tvDate.setText(post.getCreatedDate());
        holder.itemView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                itemLongClickListener.onItemLongClick(view, post, pos);
                return false;
            }
        });
    }

    @Override
    public int getItemCount() {
        return data.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        private TextView tvUsername, tvContent, tvDate;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvUsername = itemView.findViewById(R.id.tv_username);
            tvContent = itemView.findViewById(R.id.tv_content);
            tvDate = itemView.findViewById(R.id.tv_date);
        }
    }
}
