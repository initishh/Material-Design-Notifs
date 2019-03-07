package com.example.initish.hackfest;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Adapter;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.Nullable;


/**
 * A simple {@link Fragment} subclass.
 */
public class UsersFragment extends Fragment {

    RecyclerView users_rcv;
    UserAdapter adapter;
    List<User> users;
    FirebaseFirestore firebaseFirestore;
    FirebaseAuth mAuth;

    public UsersFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, final ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment

        View view = inflater.inflate(R.layout.fragment_users,container,false);

        users = new ArrayList<>();
        firebaseFirestore = FirebaseFirestore.getInstance();
        mAuth = FirebaseAuth.getInstance();
        users_rcv = view.findViewById(R.id.users_rcv);

        adapter = new UserAdapter(container.getContext(),users);
        users_rcv.setAdapter(adapter);
        users_rcv.setLayoutManager(new LinearLayoutManager(container.getContext()));

//        firebaseFirestore.collection("users").get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
//            @Override
//            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
//
//                if(!queryDocumentSnapshots.isEmpty()){
//
//                    List<DocumentSnapshot> list = queryDocumentSnapshots.getDocuments();
//                    for(DocumentSnapshot d: list){
//
//                        User user =d.toObject(User.class);
//                        users.add(user);
//                    } }
//
//                adapter.notifyDataSetChanged();
//
//            }
//        });

        return view;
    }

    @Override
    public void onStart() {
        super.onStart();

        users.clear();
        firebaseFirestore.collection("users").addSnapshotListener(getActivity(), new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot queryDocumentSnapshots, @Nullable FirebaseFirestoreException e) {

                for(DocumentChange doc: queryDocumentSnapshots.getDocumentChanges()){

                    if(doc.getType() == DocumentChange.Type.ADDED) {

                        String user_id = doc.getDocument().getId();

                        User user = doc.getDocument().toObject(User.class).withId(user_id);
                        users.add(user);

                        adapter.notifyDataSetChanged();
                    }
                }
            }
        });
    }
}
