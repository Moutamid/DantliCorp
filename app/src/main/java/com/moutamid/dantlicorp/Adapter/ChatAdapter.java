package com.moutamid.dantlicorp.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;


import com.bumptech.glide.Glide;
import com.fxn.stash.Stash;
import com.moutamid.dantlicorp.Model.ChatModel;
import com.moutamid.dantlicorp.Model.UserModel;
import com.moutamid.dantlicorp.R;
import com.moutamid.dantlicorp.helper.Config;
import com.moutamid.dantlicorp.helper.Constants;

import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;

public class ChatAdapter extends RecyclerView.Adapter<ChatAdapter.ChatVH> {
    private Context context;
    private ArrayList<ChatModel> list;

    private static final int MSG_TYPE_LEFT = 0;
    private static final int MSG_TYPE_RIGHT = 1;

    public ChatAdapter(Context context, ArrayList<ChatModel> list) {
        this.context = context;
        this.list = list;
    }

    @NonNull
    @Override
    public ChatVH onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view;
        if (viewType == MSG_TYPE_LEFT) {
            view = LayoutInflater.from(context).inflate(R.layout.left_user_message, parent, false);
        } else {
            view = LayoutInflater.from(context).inflate(R.layout.right_user_message, parent, false);
        }
        return new ChatVH(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ChatVH holder, int position) {
        ChatModel model = list.get(holder.getAdapterPosition());
        UserModel userNew = (UserModel) Stash.getObject("UserDetails", UserModel.class);

        holder.message.setText(model.getMessage());
        holder.time.setText(Config.getFormatedTime(model.getTimestamps()));
        Glide.with(context).load(userNew.image_url).placeholder(R.drawable.profile_icon).into(holder.imageView);

    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    @Override
    public int getItemViewType(int position) {
        //get currently signed in user
        UserModel   userModel = (UserModel) Stash.getObject("UserDetails", UserModel.class);

        return userModel.id.equals(list.get(position).getSenderID()) ? MSG_TYPE_RIGHT : MSG_TYPE_LEFT;
    }


    public class ChatVH extends RecyclerView.ViewHolder{
        CircleImageView imageView;
        TextView message, time;
        public ChatVH(@NonNull View itemView) {
            super(itemView);

            imageView = itemView.findViewById(R.id.profile);
            time = itemView.findViewById(R.id.date);
            message = itemView.findViewById(R.id.message);

        }
    }

}
