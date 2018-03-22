package com.hmm.signupprofile;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class AddBalance extends AppCompatActivity {



    DatabaseReference mDatabase;
    FirebaseAuth mAuth;

    EditText etAmount;
    Spinner spBank;
    Button btnAdd;
    boolean once = true;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.add_balance);
        mAuth = FirebaseAuth.getInstance();

        final FirebaseUser current = mAuth.getCurrentUser();

        btnAdd =(Button) findViewById(R.id.btnAdd);
        spBank = (Spinner) findViewById(R.id.spBank);
        etAmount=(EditText) findViewById(R.id.etAmount);
        ArrayList<String> course = new ArrayList<String>();
        course.add("--SELECT BANK--");
        course.add("UNION BANK OF INDIA");
        course.add("CORPORATION BANK");
        course.add("SBI");
        course.add("HDFC");
        course.add("CANARA");
        ArrayAdapter<String> genderAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, course);

        spBank.setAdapter(genderAdapter);

        spBank.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
              String c = adapterView.getItemAtPosition(i).toString();
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });


        btnAdd.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        mDatabase = FirebaseDatabase.getInstance().getReference();
                        mDatabase.child("users").addValueEventListener(
                                new ValueEventListener() {
                                    @Override
                                    public void onDataChange(DataSnapshot dataSnapshot) {
                                        if(once)
                                        for (DataSnapshot data : dataSnapshot.getChildren()) {
                                            if (data.hasChild("username")) {
                                                if (data.child("username").getValue().toString().matches(current.getEmail())) {
                                                    if (data.hasChild("name")) {
                                                        Double balance = Double.parseDouble(data.child("balance").getValue().toString());
                                                        balance = balance+Double.parseDouble(etAmount.getText().toString());
                                                        mDatabase.child("users").child(data.getKey()).child("balance").setValue(balance);
                                                        Toast.makeText(getApplicationContext(),"SUCCESS",Toast.LENGTH_SHORT).show();
                                                        once=  false;

                                                    }
                                                }
                                            }
                                        }
                                        once=  false;
                                    }

                                    @Override
                                    public void onCancelled(DatabaseError databaseError) {

                                    }
                                }
                        );
                    }
                }
        );

    }
}
