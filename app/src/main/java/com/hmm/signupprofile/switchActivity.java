package com.hmm.signupprofile;

import android.*;
import android.annotation.SuppressLint;
import android.app.Service;
import android.content.Intent;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;


public class switchActivity extends AppCompatActivity {

    private String name;

    private DatabaseReference mDatabase;
    public static final int ASK_MULTIPLE_PERMISSION_REQUEST_CODE =1;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_switch);

        final FirebaseAuth mAuth = FirebaseAuth.getInstance();

        final FirebaseUser current = mAuth.getCurrentUser();

//        if(current !=null)
//        {
//            Intent in = new Intent(switchActivity.this,ProfileActivity.class);
//            startActivity(in);finish();
//        }

        ActivityCompat.requestPermissions(switchActivity.this, new String[]{android.Manifest.permission.WRITE_EXTERNAL_STORAGE, android.Manifest.permission.MOUNT_UNMOUNT_FILESYSTEMS, android.Manifest.permission.INTERNET, android.Manifest.permission.READ_CONTACTS}, ASK_MULTIPLE_PERMISSION_REQUEST_CODE);

//        Intent i = getIntent();
//        String status=i.getStringExtra("status");
//            startActivity(new Intent(switchActivity.this,MainActivity.class));
//            if(mAuth.getCurrentUser()!=null || i.getBooleanExtra("status",false)) {
//                mDatabase = FirebaseDatabase.getInstance().getReference();
//                mDatabase.child("users").addValueEventListener(
//                        new ValueEventListener() {
//                            @Override
//                            public void onDataChange(DataSnapshot dataSnapshot) {
//                                for (DataSnapshot data : dataSnapshot.getChildren()) {
//                                    if (data.hasChild("username")) {
//                                        if (data.child("username").getValue().toString().matches(current.getEmail())) {
//                                            if (data.hasChild("name")) {
//                                                name = data.child("name").getValue().toString();
//                                                Toast.makeText(getApplication(), data.child("username").getValue().toString() + name, Toast.LENGTH_SHORT).show();
//                                                Intent in = new Intent(switchActivity.this, mainscreen.class);
//                                                in.putExtra("name", name);
//                                                in.putExtra("phno", data.child("phonenumber").getValue().toString());
//                                                in.putExtra("email", data.child("username").getValue().toString());
////                                                in.putExtra("medHistory", data.child("medHistory").getValue().toString());
////                                                in.putExtra("prevBills", data.child("prevBills").getValue().toString());
////                                                if (data.child("gender").getValue().toString().matches("male"))
////                                                    in.putExtra("male", true);
////                                                else
////                                                    in.putExtra("female", true);
//                                                startActivity(in);
//                                                finish();
//                                            }
//                                        }
//                                    }
//                                }
//                            }
//
//                            @Override
//                            public void onCancelled(DatabaseError databaseError) {
//
//                            }
//                        }
//                );
//
//
//            }



               Button signup = (Button)findViewById(R.id.signup);
         Button login = (Button)findViewById(R.id.login);

        signup.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        startActivity(new Intent(switchActivity.this,SignUpOne.class));
                        finish();
                    }
                }
        );

        login.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        startActivity(new Intent(switchActivity.this,LoginActivity.class));
                        finish();
                    }
                }
        );
    }
    @Override
    public void onBackPressed(){

        finish();
        super.onBackPressed();
    }



//    @SuppressLint("MissingSuperCall")
//    @Override
//    public void onPause()
//    {
//        finish();
//    }
}
