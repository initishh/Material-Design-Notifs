package com.example.initish.hackfest;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;

import java.util.ArrayList;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class UserAdapter extends RecyclerView.Adapter<UserAdapter.myViewHolder> {

    Context context;
    List<User> users = new ArrayList<>();

    public UserAdapter(Context context, List<User> users) {
        this.context = context;
        this.users = users;
    }

    @NonNull
    @Override
    public UserAdapter.myViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {

        LayoutInflater layoutInflater = LayoutInflater.from(context);
        View view = layoutInflater.inflate(R.layout.user,viewGroup,false);

        return new UserAdapter.myViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull UserAdapter.myViewHolder myViewHolder, final int i) {
        myViewHolder.name.setText(users.get(i).getName());

        RequestOptions placeholderoption = new RequestOptions();
        placeholderoption.placeholder(R.drawable.follower);
        Glide.with(context).setDefaultRequestOptions(placeholderoption).load(users.get(i).getImage()).into(myViewHolder.image);

        final String user_id = users.get(i).userId;

        myViewHolder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent sendIntent = new Intent(context, SendActivity.class);
                sendIntent.putExtra("user_id",user_id);
                sendIntent.putExtra("user_name",users.get(i).getName());
                context.startActivity(sendIntent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return users.size();
    }

    public class myViewHolder extends RecyclerView.ViewHolder{

        TextView name;
        CircleImageView image;

        public myViewHolder(@NonNull View itemView) {
            super(itemView);

            name = itemView.findViewById(R.id.user_name);
            image = itemView.findViewById(R.id.user_img);
        }
    }

}
