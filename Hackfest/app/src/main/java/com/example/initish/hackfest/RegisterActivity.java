  package com.example.initish.hackfest;

import android.content.Intent;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.circularreveal.cardview.CircularRevealCardView;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GetTokenResult;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.net.URI;
import java.util.HashMap;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;

  public class RegisterActivity extends AppCompatActivity {


      private static final int PICK_IMAGE = 1;
      EditText reg_name,reg_pass,reg_mail;
      Button reg_btn;
      CircleImageView reg_img;
      String TAG = "This is a Tag";
      ProgressBar reg_pb;
      FirebaseAuth mAuth;
      StorageReference mStorageRef;
      FirebaseFirestore mFirestore;
      Uri img_uri;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        mAuth = FirebaseAuth.getInstance();
        mStorageRef = FirebaseStorage.getInstance().getReference();
        mFirestore = FirebaseFirestore.getInstance();

        reg_pb = findViewById(R.id.reg_pb);
        reg_name = findViewById(R.id.reg_name);
        reg_pass = findViewById(R.id.reg_pass);
        reg_mail = findViewById(R.id.reg_mail);
        reg_btn = findViewById(R.id.reg_btn);
        reg_img = findViewById(R.id.reg_img);
        img_uri = null;

        reg_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                register(reg_mail.getText().toString(),reg_pass.getText().toString());
            }
        });

        reg_img.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent();
                intent.setType("image/*");
                intent.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(Intent.createChooser(intent,"Select Picture"),PICK_IMAGE);
            }
        }); }

      @Override
      protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
          super.onActivityResult(requestCode, resultCode, data);

          if(requestCode == PICK_IMAGE){

              img_uri = data.getData();
              reg_img.setImageURI(img_uri);
          }
      }

      private void register(String email, String password) {

        if(img_uri != null){

            reg_pb.setVisibility(View.VISIBLE);

            final String name = reg_name.getText().toString();
            if(!TextUtils.isEmpty(name) && !TextUtils.isEmpty(email) && !TextUtils.isEmpty(password));
            {
                mAuth.createUserWithEmailAndPassword(email, password)
                        .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {
                                if (task.isSuccessful()) {

                                    final String userId = mAuth.getCurrentUser().getUid();
                                    final StorageReference imageRef = mStorageRef.child("images");
                                    final StorageReference spaceRef = imageRef.child(userId + ".jpg");

                                    final UploadTask uploadTask = spaceRef.putFile(img_uri);

                                    Task<Uri> urlTask = uploadTask.continueWithTask(new Continuation<UploadTask.TaskSnapshot, Task<Uri>>() {
                                        @Override
                                        public Task<Uri> then(@NonNull Task<UploadTask.TaskSnapshot> task) throws Exception {
                                            if (!task.isSuccessful()) {
                                                throw task.getException();
                                            }
                                            return spaceRef.getDownloadUrl();
                                        }
                                    }).addOnSuccessListener(new OnSuccessListener<Uri>() {
                                        @Override
                                        public void onSuccess(Uri uri) {
                                            String download_url = uri.toString();


                                            final Map<String, Object> userMap = new HashMap<>();
                                            userMap.put("name", name);
                                            userMap.put("image", download_url);

                                            String token_id = FirebaseInstanceId.getInstance().getToken();
                                            userMap.put("token_id", token_id);


                                            mFirestore.collection("users").document(userId).set(userMap).addOnSuccessListener(new OnSuccessListener<Void>() {
                                                @Override
                                                public void onSuccess(Void aVoid) {
                                                    startActivity(new Intent(RegisterActivity.this, MainActivity.class));
                                                    finish();
                                                }
                                            }).addOnFailureListener(new OnFailureListener() {
                                                @Override
                                                public void onFailure(@NonNull Exception e) {
                                                    Toast.makeText(RegisterActivity.this, "User addition to database failed", Toast.LENGTH_SHORT).show();
                                                }
                                            });

                                            Toast.makeText(RegisterActivity.this, "Successfully created account", Toast.LENGTH_SHORT).show();
                                            startActivity(new Intent(RegisterActivity.this, MainActivity.class));
                                        }
                                    });
                                }
                                    else {

                                    reg_pb.setVisibility(View.INVISIBLE);
                                    Log.w(TAG, "createUserWithEmail:failure", task.getException());
                                    Toast.makeText(RegisterActivity.this, "Authentication failed.",
                                            Toast.LENGTH_SHORT).show();
                                } }
                        });

            }
        }
    }
  }
