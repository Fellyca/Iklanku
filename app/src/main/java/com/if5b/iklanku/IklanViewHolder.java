package com.if5b.iklanku;

import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.if5b.iklanku.Adapter.PostViewAdapter;

public class IklanViewHolder extends RecyclerView.ViewHolder {

    public TextView tvMessage,tvMes;
    public ImageView ivMessage,ivMes;

    public IklanViewHolder(@NonNull View itemView) {
        super(itemView);

        tvMes = itemView.findViewById(R.id.tv_me);
        tvMessage = itemView.findViewById(R.id.tv_m);
        ivMes = itemView.findViewById(R.id.iv_me);
        ivMessage = itemView.findViewById(R.id.iv_m);
    }
}
