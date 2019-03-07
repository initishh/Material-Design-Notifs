package com.example.initish.hackfest;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.HashMap;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;
import io.grpc.Context;

/**
 * A simple {@link Fragment} subclass.
 */

public class ProfileFragment extends Fragment {

    Button logout_btn;
    FirebaseAuth mAuth;
    FirebaseFirestore mFirestore;
    StorageReference mStorageRef;
    TextView user_name;
    CircleImageView user_img;
    String userID;

    public ProfileFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, final ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view= inflater.inflate(R.layout.fragment_profile, container, false);

        mAuth = FirebaseAuth.getInstance();
        mFirestore = FirebaseFirestore.getInstance();
        mStorageRef = FirebaseStorage.getInstance().getReference();
        logout_btn = view.findViewById(R.id.logout_btn);
        user_img = view.findViewById(R.id.user_img);
        user_name = view.findViewById(R.id.user_name);
        userID = mAuth.getCurrentUser().getUid();

        mFirestore.collection("users").document(userID).get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {

                final String name = documentSnapshot.getString("name");
                String image_url = documentSnapshot.getString("image");

                user_name.setText(name);
                RequestOptions placeholderoption = new RequestOptions();
                placeholderoption.placeholder(R.drawable.follower);
                Glide.with(container.getContext()).setDefaultRequestOptions(placeholderoption).load(image_url).into(user_img);
            }
        });

        logout_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Map<String,Object> tokenMapRemove = new HashMap<>();
                tokenMapRemove.put("token_id",FieldValue.delete());

                mFirestore.collection("users").document(userID).update(tokenMapRemove).addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        mAuth.signOut();
                        startActivity(new Intent(container.getContext(), LoginActivity.class));
                    }
                });
            }
        });
        return view;
    }
}
