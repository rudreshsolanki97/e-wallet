package com.hmm.signupprofile;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.design.widget.TextInputEditText;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.mikepenz.materialdrawer.AccountHeader;
import com.mikepenz.materialdrawer.AccountHeaderBuilder;
import com.mikepenz.materialdrawer.Drawer;
import com.mikepenz.materialdrawer.DrawerBuilder;
import com.mikepenz.materialdrawer.model.ExpandableDrawerItem;
import com.mikepenz.materialdrawer.model.PrimaryDrawerItem;
import com.mikepenz.materialdrawer.model.ProfileDrawerItem;
import com.mikepenz.materialdrawer.model.SecondaryDrawerItem;
import com.mikepenz.materialdrawer.model.interfaces.IDrawerItem;
import com.mikepenz.materialdrawer.model.interfaces.IProfile;

public class ProfileActivity extends AppCompatActivity {



    private TextView nameTextView,emailTextView,phoneTextView;
    private Button loggout;

    Button update ;
    String name;
    String email;
    String phno;


    Button refresh;
    DatabaseReference mDatabase;
    FirebaseAuth mAuth;
    TextView prevBills;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        final SharedPreferences sf = getSharedPreferences("LaunchAppOnShake",MODE_APPEND);
        final SharedPreferences.Editor sfed = sf.edit();


        mAuth = FirebaseAuth.getInstance();
        FirebaseUser current = mAuth.getCurrentUser();



        if(current!=null)



        nameTextView = (TextView) findViewById(R.id.name);
        emailTextView = (TextView)findViewById(R.id.email);
        phoneTextView = (TextView)findViewById(R.id.phone_number);
//        update = (Button)findViewById(R.id.logout);
        nameTextView.setFocusable(true);
        nameTextView.setEnabled(true);
        prevBills = (TextView)findViewById(R.id.balance);
        loggout = (Button) findViewById(R.id.loggout);
        name=getName();
        email=getEmailName();
        phno=getNo();
Intent i =getIntent();
        nameTextView.setText(i.getStringExtra("name"));   //set username here
        emailTextView.setText(i.getStringExtra("email")); //set email here
        phoneTextView.setText(i.getStringExtra("phno"));  //set phone number here

        prevBills.setText(i.getStringExtra("balance"));

        prevBills.setFocusable(false);



        loggout.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        mAuth.signOut();
                        startActivity(new Intent(ProfileActivity.this,switchActivity.class));
                        finish();
                    }
                }
        );

//            update.setOnClickListener(
//                    new View.OnClickListener() {
//                        @Override
//                        public void onClick(View view) {
//
//                            mDatabase=FirebaseDatabase.getInstance().getReference();
//                            mDatabase.child("users").addValueEventListener(
//                                new ValueEventListener() {
//                                    @Override
//                                    public void onDataChange(DataSnapshot dataSnapshot) {
//                                        for (DataSnapshot data : dataSnapshot.getChildren()) {
//                                            if (data.hasChild("username")) {
//                                                if (data.child("username").getValue().toString().matches(mAuth.getCurrentUser().getEmail())) {
//                                                    if (data.hasChild("name")) {
//                                                        Toast.makeText(getApplication(), data.child("username").getValue().toString() , Toast.LENGTH_SHORT).show();
////                                                        mDatabase.child("users").child(data.child("phonenumber").getValue().toString()).child("medHistory").setValue(medHistory.getText().toString());
////                                                        mDatabase.child("users").child(data.child("phonenumber").getValue().toString()).child("prevBills").setValue(prevBills.getText().toString());
////                                                        if(male)
////                                                            mDatabase.child("users").child(data.child("phonenumber").getValue().toString()).child("gender").setValue("male");
////                                                        else if(female)
////                                                            mDatabase.child("users").child(data.child("phonenumber").getValue().toString()).child("gender").setValue("female");
//                                                    }
//                                                }
//                                            }
//                                        }
//                                    }
//
//                                    @Override
//                                    public void onCancelled(DatabaseError databaseError) {
//
//                                    }
//                                }
//                        );
//                    }
//                }
//        );


        nameTextView.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {


                    }
                }
        );















    }
        String  getName()
{
    mDatabase=FirebaseDatabase.getInstance().getReference();
    mDatabase.child("users").addValueEventListener(
            new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    for (DataSnapshot data : dataSnapshot.getChildren()) {
                        if (data.hasChild("username")) {
                            if (data.child("username").getValue().toString().matches(mAuth.getCurrentUser().getEmail())) {
                                if (data.hasChild("name")) {
                                    name=data.child("name").getValue().toString();
//                                    Toast.makeText(getApplication(), "name"+name , Toast.LENGTH_SHORT).show();
                                    break ;
                                }
                            }
                        }
                    }
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            }
    );
    return name;
}
    String  getEmailName()
    {
        mDatabase=FirebaseDatabase.getInstance().getReference();
        mDatabase.child("users").addValueEventListener(
                new ValueEventListener()
                {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot)
                    {
                        for (DataSnapshot data : dataSnapshot.getChildren())
                        {
                            if (data.hasChild("username"))
                            {
                                if (data.child("username").getValue().toString().matches(mAuth.getCurrentUser().getEmail()))
                                {
                                    if (data.hasChild("name"))
                                    {
                                        name=data.child("username").getValue().toString();
//                                        Toast.makeText(getApplication(), "emal name"+name , Toast.LENGTH_SHORT).show();
                                        break;
                                    }
                                }
                            }
                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                }
        );
        return name;
    }
    String  getNo()
    {
        mDatabase=FirebaseDatabase.getInstance().getReference();
        mDatabase.child("users").addValueEventListener(
                new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        for (DataSnapshot data : dataSnapshot.getChildren()) {
                            if (data.hasChild("username")) {
                                if (data.child("username").getValue().toString().matches(mAuth.getCurrentUser().getEmail())) {
                                    if (data.hasChild("name")) {
                                        name=data.child("phonenumber").getValue().toString();
//                                        Toast.makeText(getApplication(), "phno "+name , Toast.LENGTH_SHORT).show();
                                        break;
                                                                     }
                                }
                            }
                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                }
        );
        return name;

    }
}


