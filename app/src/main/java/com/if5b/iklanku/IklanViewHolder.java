package com.if5b.iklanku;

import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.if5b.iklanku.Adapter.PostViewAdapter;

public class IklanViewHolder extends RecyclerView.ViewHolder {

    public LinearLayout llReceiver, llSender;
    public TextView tvMessageReceiver,tvReceiver,tvSender,tvMessageSender;
    public ImageView ivMessageReceiver,ivReceiver,ivSender,ivMessageSender;

    public IklanViewHolder(@NonNull View itemView) {
        super(itemView);

        llReceiver = itemView.findViewById(R.id.ll_receiver);
        tvReceiver = itemView.findViewById(R.id.tv_me);
        tvMessageReceiver = itemView.findViewById(R.id.tv_m);
        ivReceiver = itemView.findViewById(R.id.iv_me);
        ivMessageReceiver = itemView.findViewById(R.id.iv_m);

        llSender = itemView.findViewById(R.id.ll_sender);
        tvSender = itemView.findViewById(R.id.tv_sender);
        tvMessageSender = itemView.findViewById(R.id.tv_message_sender);
        ivSender = itemView.findViewById(R.id.iv_sender);
        ivMessageSender = itemView.findViewById(R.id.iv_message_sender);
    }
}
