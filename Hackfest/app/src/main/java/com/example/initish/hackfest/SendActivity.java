package com.example.initish.hackfest;

import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class SendActivity extends AppCompatActivity {

    TextView id;
    String user_id,user_name;
    EditText send_notif;
    Button send_btn;
    ProgressBar pb;
    FirebaseFirestore mStore = FirebaseFirestore.getInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_send);

        id= findViewById(R.id.id);
        user_id = getIntent().getStringExtra("user_id");
        user_name = getIntent().getStringExtra("user_name");
        id.setText("Send Notification to "+ user_name);

        send_notif = findViewById(R.id.send_notif);
        send_btn = findViewById(R.id.send_btn);
        pb = findViewById(R.id.pb);

        send_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                pb.setVisibility(View.VISIBLE);
                final String message = send_notif.getText().toString();
                if(!TextUtils.isEmpty(message)){

                    Map<String,Object> notifs = new HashMap<>();
                    notifs.put("Message",message);
                    notifs.put("Sender",FirebaseAuth.getInstance().getCurrentUser().getUid());
                    mStore.collection("users/"+ user_id + "/Notifications").add(notifs).addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                        @Override
                        public void onSuccess(DocumentReference documentReference) {
                            pb.setVisibility(View.INVISIBLE);
                            send_notif.setText("");
                            Toast.makeText(SendActivity.this, "Notification Sent Successfully", Toast.LENGTH_SHORT).show();
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            pb.setVisibility(View.INVISIBLE);
                            send_notif.setText("");
                            Toast.makeText(SendActivity.this, "Process Failed", Toast.LENGTH_SHORT).show();
                        }
                    });
                }
            }
        });

    }
}
