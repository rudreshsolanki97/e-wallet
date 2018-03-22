package com.hmm.signupprofile;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.SystemClock;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.Calendar;
import java.util.Date;

public class MakePayment extends AppCompatActivity {



    private EditText to;
    private EditText amount;
    private Button confirm;
    private String number;

    DatabaseReference mDatabase;
    FirebaseAuth mAuth;
    double cur1;

    boolean once = true;
    boolean check_once=true;
    static String found = "false";
    boolean two = true;
    boolean dont = true;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_makepayment);

        mAuth = FirebaseAuth.getInstance();
        final FirebaseUser current = mAuth.getCurrentUser();


        Intent i = getIntent();
        String toI = i.getStringExtra("number");
        final String amountI = i.getStringExtra("amount");





        to = (EditText)findViewById(R.id.to);
        amount = (EditText)findViewById(R.id.amount);
        confirm = (Button)findViewById(R.id.confirm);

        if(toI!=null&&amountI!=null)
        {
            to.setText(toI);
            amount.setText(amountI);
        }

        confirm.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        if (!to.getText().toString().matches("") && !amount.getText().toString().matches("")) {
                            boolean flag = false;
                            new AlertDialog.Builder(MakePayment.this)
                                    .setTitle("CONFIRM PAYMENT")
                                   .setMessage("SEND Rs."+amount.getText().toString()+" "+to.getText().toString()+" ?")
                                    .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialogInterface, int i) {
                                            //do nothing



                                            mDatabase = FirebaseDatabase.getInstance().getReference();


                                            mDatabase.child("users").addValueEventListener(
                                                    new ValueEventListener() {
                                                        @Override
                                                        public void onDataChange(DataSnapshot dataSnapshot) {
                                                            if (check_once) {
                                                                for (DataSnapshot data : dataSnapshot.getChildren()) {
                                                                    if (data.hasChild("username"))
                                                                        if (data.child("username").getValue().toString().matches(mAuth.getCurrentUser().getEmail())) {
                                                                            double balance = Double.parseDouble(data.child("balance").getValue().toString());
                                                                            if (balance < Double.parseDouble(amount.getText().toString())) {
                                                                                dont = false;
                                                                                new AlertDialog.Builder(MakePayment.this)
                                                                                        .setTitle("INSUFFICIENT FUND")
                                                                                        .setMessage("YOUR AVAILABLE BALANCE IS  : " + balance)
                                                                                        .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                                                                            @Override
                                                                                            public void onClick(DialogInterface dialogInterface, int i) {
                                                                                                finish();
                                                                                            }
                                                                                        }).create().show();
                                                                            }
                                                                            else
                                                                            {
                                                                                mDatabase.child("users").addValueEventListener(
                                                                                        new ValueEventListener() {
                                                                                            @Override
                                                                                            public void onDataChange(DataSnapshot dataSnapshot) {
                                                                                                if (once) {
                                                                                                    for (DataSnapshot data : dataSnapshot.getChildren()) {
                                                                                                        if (data.hasChild("username")) {
                                                                                                            if (data.child("username").getValue().toString().matches(mAuth.getCurrentUser().getEmail())) {
                                                                                                                cur1 = Double.parseDouble(data.child("balance").getValue().toString());
                                                                                                                cur1 = cur1 - Double.parseDouble(amount.getText().toString());
                                                                                                                number = data.getValue().toString();
                                                                                                                mDatabase.child("users").child(data.child("phonenumber").getValue().toString()).child("balance").setValue(cur1);
                                                                                                                mDatabase.child("users").child(data.child("phonenumber").getValue().toString()).child("debit").push().setValue(to.getText().toString().split("@")[0]);
                                                                                                                mDatabase.child("users").child(data.child("phonenumber").getValue().toString()).child("debit").child(to.getText().toString().split("@")[0]).child("^^"+(new Date()).toString()+"^^").setValue(amount.getText().toString());
                                                                                                                continue;
                                                                                                            }
                                                                                                            if (data.getKey().matches(to.getText().toString())) {
                                                                                                                double cur = Double.parseDouble(data.child("balance").getValue().toString());
                                                                                                                cur = cur + Double.parseDouble(amount.getText().toString());
                                                                                                                mDatabase.child("users").child(data.child("phonenumber").getValue().toString()).child("balance").setValue(cur);
                                                                                                                mDatabase.child("users").child(data.child("phonenumber").getValue().toString()).child("credit").push().setValue(current.getEmail().split("@")[0]);
                                                                                                                mDatabase.child("users").child(data.child("phonenumber").getValue().toString()).child("credit").child(current.getEmail().split("@")[0]).child("^^"+(new Date()).toString()+"^^").setValue(amount.getText().toString());
                                                                                                                found = "true";
                                                                                                                Toast.makeText(getApplicationContext(), found + "", Toast.LENGTH_SHORT).show();
                                                                                                                continue;
                                                                                                            }
                                                                                                        }
                                                                                                    }
                                                                                                }
                                                                                                once = false;
                                                                                                if (found.matches("false")) {
                                                                                                    Toast.makeText(MakePayment.this, "No such account", Toast.LENGTH_SHORT).show();
                                                                                                    mDatabase = FirebaseDatabase.getInstance().getReference();
                                                                                                    mDatabase.child("users").addValueEventListener(
                                                                                                            new ValueEventListener() {
                                                                                                                @Override
                                                                                                                public void onDataChange(DataSnapshot dataSnapshot) {
                                                                                                                    if (two) {
                                                                                                                        for (DataSnapshot data : dataSnapshot.getChildren()) {
                                                                                                                            if (data.hasChild("username")) {
                                                                                                                                if (data.child("username").getValue().toString().matches(mAuth.getCurrentUser().getEmail())) {
                                                                                                                                    cur1 = Double.parseDouble(data.child("balance").getValue().toString());
                                                                                                                                    cur1 = cur1 + Double.parseDouble(amount.getText().toString());
                                                                                                                                    mDatabase.child("users").child(data.child("phonenumber").getValue().toString()).child("balance").setValue(cur1);
                                                                                                                                    mDatabase.child("users").child(data.child("phonenumber").getValue().toString()).child("debit").child(to.getText().toString().split("@")[0]).removeValue();
                                                                                                                                    two = false;
                                                                                                                                }
                                                                                                                            }
                                                                                                                        }
                                                                                                                    }
                                                                                                                    two = false;
                                                                                                                }
                                                                                                                @Override
                                                                                                                public void onCancelled(DatabaseError databaseError) {

                                                                                                                }
                                                                                                            }
                                                                                                    );
                                                                                                }
                                                                                            }
                                                                                            @Override
                                                                                            public void onCancelled(DatabaseError databaseError) {
                                                                                            }
                                                                                        }
                                                                                );
                                                                            }
                                                                        }
                                                                }
                                                            }
                                                            check_once = false;
                                                        }
                                                        @Override
                                                        public void onCancelled(DatabaseError databaseError) {
                                                        }
                                                    }
                                            );


                                        }
                                    })
                                    .setNegativeButton("CANCEL", new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialogInterface, int i) {
                                            startActivity(new Intent(MakePayment.this,mainscreen.class));
                                            finish();
                                        }
                                    })


                            .create().show();
                            //value set


                        }
                        else if(to.getText().length()==0)
                        {
                            to.setError("empty");
                        }
                        else if(amount.getText().length()==0)
                        {
                            amount.setError("empty");
                        }
                    }
                }
        );
    }
}
