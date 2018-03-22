package com.hmm.signupprofile;

import android.annotation.SuppressLint;
import android.app.Service;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseException;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthException;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthProvider;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.IgnoreExtraProperties;
import com.google.firebase.database.ValueEventListener;

import java.util.concurrent.TimeUnit;

public class OTPAuth extends AppCompatActivity {

    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;

    private String phoneVerificationId;
    private PhoneAuthProvider.OnVerificationStateChangedCallbacks verificationCallbacks;
    private PhoneAuthProvider.ForceResendingToken resendToken;

    Button verifyotp;
    String phno ;
    TextView status ;
    EditText otp ;
    String name;
    String password;

    private DatabaseReference mDatabase;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_otpauth);

        verifyotp = (Button)findViewById(R.id.verify);
        Intent i ;
        i = getIntent();
        mAuth = FirebaseAuth.getInstance();
        phno = i.getStringExtra("numberField");
        name=i.getStringExtra("nameField");
        password=i.getStringExtra("password");
        status = (TextView)findViewById(R.id.status);
        otp = (EditText)findViewById(R.id.OTPInput);
        /*if(mAuth.getCurrentUser()==null)
        {
            //Not logged in
        }*/


        otp.setOnFocusChangeListener(
                new View.OnFocusChangeListener() {
                    @Override
                    public void onFocusChange(View view, boolean b) {
                        if (otp.getText().toString().equals("OTP")) {
                            otp.setText("");
                        }

                    }
                }
        );

        if(name!=null&&password!=null) {
            mAuth.createUserWithEmailAndPassword(name, password)
                    .addOnCompleteListener(OTPAuth.this, new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {

                            if (task.isSuccessful()) {
                                DatabaseReference mDatabase = FirebaseDatabase.getInstance().getReference();


                                Log.d("asas", "createUserWithEmail:onComplete:" + task.isSuccessful());
//                                .makeText(OTPAuth.this, "succesToasts",
//                                        Toast.LENGTH_SHORT).show();

                                mDatabase.child("users").push().setValue(phno);
                                mDatabase.child("users").child(phno).child("phonenumber").setValue(phno);
                                mDatabase.child("users").child(phno).child("username").setValue(name);
                                mDatabase.child("users").child(phno).child("name").setValue(name.split("@")[0]);
                                mDatabase.child("users").child(phno).child("balance").setValue("50.0");
                                mDatabase.child("users").child(phno).push().setValue("debit");
                                mDatabase.child("users").child(phno).push().setValue("credit");

                                Intent i = new Intent(OTPAuth.this, mainscreen.class);
                                i.putExtra("status",true);
                                startActivity(i);
                                finish();
                            }
                            // If sign in fails, display a message to the user. If sign in succeeds
                            // the auth state listener will be notified and logic to handle the
                            // signed in user can be handled in the listener.
                            if (!task.isSuccessful()) {
                                FirebaseAuthException e = (FirebaseAuthException) task.getException();
                                Toast.makeText(OTPAuth.this, "Failed Registration: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                                Toast.makeText(OTPAuth.this, R.string.registrationerror,
                                        Toast.LENGTH_SHORT).show();
                            }

                            // ...
                        }
                    });
        }
        else {
            verificationCallbacks = new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
                @Override
                public void onVerificationCompleted(PhoneAuthCredential credential) {
                    status.setText("Signed in");
                    signInWithPhoneAuthCredential(credential);

                }

                @Override
                public void onCodeSent(String verificationId, PhoneAuthProvider.ForceResendingToken forceResendingToken) {
                    super.onCodeSent(verificationId, forceResendingToken);

                    phoneVerificationId = verificationId;

                }


                @Override
                public void onVerificationFailed(FirebaseException e) {
                    if (e instanceof FirebaseAuthInvalidCredentialsException) {
                        Toast.makeText(getApplicationContext(), "Please ender a valid Phone number", Toast.LENGTH_SHORT).show();
                        startActivity(new Intent(OTPAuth.this, GetName.class));
                        finish();
                    } else {
                        Toast.makeText(OTPAuth.this, "server is down", Toast.LENGTH_SHORT).show();//makeToast()
                        startActivity(new Intent(OTPAuth.this, GetName.class));
                        finish();
                    }
                }
            };
            sendCode();
            verifyotp.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (otp.getText().toString().equals("") || otp.getText().toString().equals("OTP")) {
                        Toast.makeText(OTPAuth.this, "Please enter a valid otp", Toast.LENGTH_SHORT).show();//makeToast()
                        startActivity(new Intent(OTPAuth.this, GetName.class));
                        finish();
                    } else {
                        verifyCode(otp.getText().toString());
                    }
                }
            });
        }
    }

    protected void sendCode()
    {
        String phoneno = phno;

        PhoneAuthProvider.getInstance().verifyPhoneNumber(
                phoneno,
                60,
                TimeUnit.SECONDS,
                this,
                verificationCallbacks
        );
    }




    public void verifyCode(String code)
    {

        PhoneAuthCredential credential = PhoneAuthProvider.getCredential(phoneVerificationId,code);
        signInWithPhoneAuthCredential(credential);
    }

    private void signInWithPhoneAuthCredential(PhoneAuthCredential credential)
    {
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if(task.isSuccessful())
                        {
                            status.setText("SignedIn successfully");
                            FirebaseUser user = task.getResult().getUser();
                            mDatabase = FirebaseDatabase.getInstance().getReference();
                            User u1 = new User(name,phno);
                            mDatabase.child("users").addValueEventListener(new ValueEventListener() {
                                @Override
                                public void onDataChange(DataSnapshot dataSnapshot) {
                                    for(DataSnapshot data: dataSnapshot.getChildren()){
//                                        Toast.makeText(OTPAuth.this, data.getValue().toString(), Toast.LENGTH_LONG).show();
//                                        if (data.child("").getValue().toString().matches(phno)) {
//                                            Toast.makeText(OTPAuth.this, data.getChildren().toString(), Toast.LENGTH_LONG).show();
//                                            status.setText("number exists.");
//                                            Intent i = new Intent(OTPAuth.this,SignUpOne.class);
//                                            i.putExtra("ne",true);
//                                            startActivity(i);
//                                            finish();
//                                        } else {
//                                        }
//                                        Toast.makeText(OTPAuth.this, data.getChildren().toString(), Toast.LENGTH_LONG).show();
                                    }
                                }

                                @Override
                                public void onCancelled(DatabaseError e)   {
                                }
                            });
//                            Toast.makeText(OTPAuth.this, name+password, Toast.LENGTH_SHORT).show();
                            if(name!=null&&password!=null) {
                                mAuth.createUserWithEmailAndPassword(name, password)
                                        .addOnCompleteListener(OTPAuth.this, new OnCompleteListener<AuthResult>() {
                                            @Override
                                            public void onComplete(@NonNull Task<AuthResult> task) {

                                                if (task.isSuccessful()) {

                                                    status.setText("new number added");
                                                    Log.d("asas", "createUserWithEmail:onComplete:" + task.isSuccessful());
//                                                    Toast.makeText(OTPAuth.this, "success",
//                                                            Toast.LENGTH_SHORT).show();
                                                    startActivity(new Intent(OTPAuth.this, mainscreen.class));
                                                    finish();
                                                }
                                                // If sign in fails, display a message to the user. If sign in succeeds
                                                // the auth state listener will be notified and logic to handle the
                                                // signed in user can be handled in the listener.
                                                if (!task.isSuccessful()) {
                                                    FirebaseAuthException e = (FirebaseAuthException) task.getException();
                                                    Toast.makeText(OTPAuth.this, "Failed Registration: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                                                    Toast.makeText(OTPAuth.this, R.string.registrationerror,
                                                            Toast.LENGTH_SHORT).show();
                                                }

                                                // ...
                                            }
                                        });
                            }
                            else
                            {
                                Intent i = new Intent(OTPAuth.this,GetName.class);
                                i.putExtra("numF",phno);
                                startActivity(i);
                                finish();
                            }


                        }
                        else
                        {
                            status.setText("This user might be blocked!");
                        }
                    }
                });
    }

//    @SuppressLint("MissingSuperCall")
//    @Override
//    public void onPause()
//    {
//        finish();
//    }

}

 class User {


    public String name;
    public String phno;

    public User() {
        // Default constructor required for calls to DataSnapshot.getValue(User.class)
    }

    public User(String username,String phno) {
        this.name = username;
        this.phno=phno;
    }

}



