package com.if5b.iklanku.Adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;


import com.bumptech.glide.Glide;
import com.if5b.iklanku.Model.Post;
import com.if5b.iklanku.R;

import java.util.ArrayList;
import java.util.List;

public class PostViewAdapter extends RecyclerView.Adapter<PostViewAdapter.ViewHolder> {
    private List<Post> data = new ArrayList<>();
    private OnItemLongClickListener mOnItemLongClickListener;

    public void setData(List<Post> data, OnItemLongClickListener mOnItemLongClickListener) {
        this.data = data;
        this.mOnItemLongClickListener = mOnItemLongClickListener;
        notifyDataSetChanged();

    }
    @NonNull
    @Override
    public PostViewAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.activity_post, parent, false);
        return new ViewHolder(view, mOnItemLongClickListener);
    }

    @Override
    public void onBindViewHolder(@NonNull PostViewAdapter.ViewHolder holder, int position) {
        Post post = data.get(position);
        int pos = holder.getAdapterPosition();
        holder.tvUsername.setText(post.getUsername());
        holder.tvJudul.setText(post.getJudul());
        Glide.with(holder.itemView.getContext())
                .load(data.get(pos).getImage())
                .placeholder(R.drawable.ic_broken_image_24)
                .into(holder.ivImage);
        System.out.println("gambar: "+data.get(pos).getImage());

        holder.tvDate.setText(post.getCreatedDate());
    }

    @Override
    public int getItemCount() {
        return data.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnLongClickListener {
        private TextView tvUsername, tvJudul, tvDate;
        private ImageView ivImage;
        public OnItemLongClickListener onItemLongClickListener;

        public ViewHolder(@NonNull View itemView, OnItemLongClickListener onItemLongClickListener) {
            super(itemView);
            tvUsername = itemView.findViewById(R.id.tv_username);
            ivImage = itemView.findViewById(R.id.iv_image);
            tvJudul = itemView.findViewById(R.id.tv_judul);
            tvDate = itemView.findViewById(R.id.tv_date);
            this.onItemLongClickListener = onItemLongClickListener;

            itemView.setOnLongClickListener(this);
        }
        @Override
        public boolean onLongClick(View v) {
            onItemLongClickListener.onItemLongClick(v, getAdapterPosition());
            return false;
        }
    }
    public interface OnItemLongClickListener{
        void onItemLongClick(View v, int position);
    }
}
