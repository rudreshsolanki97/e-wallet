package com.hmm.signupprofile;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Build;
import android.os.Bundle;
//import android.support.annotation.NonNull;
import android.support.annotation.RequiresApi;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextWatcher;
//import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;


//firebase
import com.google.firebase.FirebaseException;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
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







public class SignUpOne extends AppCompatActivity {

    private DatabaseReference mDatabase;
    Intent i;


    @RequiresApi(api = Build.VERSION_CODES.CUPCAKE)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.content_sign_up_one);


        i = getIntent();
        if(i.getBooleanExtra("ne",false))
            Snackbar.make(getWindow().getDecorView(),"NUMBER ALREADY EXISTS",Snackbar.LENGTH_LONG).show();



//        final EditText nameField = (EditText) findViewById(R.id.nameInput);
        final EditText numberField = (EditText) findViewById(R.id.numberInput);
//        ImageButton aboutUs = (ImageButton) findViewById(R.id.aboutUs);
        Button otp = (Button) findViewById(R.id.button);


//        nameField.setOnFocusChangeListener(
//                new View.OnFocusChangeListener() {
//                    @Override
//                    public void onFocusChange(View view, boolean b) {
//                        if (nameField.getText().toString().equals("Enter your name")) {
//                            nameField.setText("");
//                        }
//                    }
//                }
//        );

        numberField.setOnFocusChangeListener(
                new View.OnFocusChangeListener() {
                    @Override
                    public void onFocusChange(View view, boolean b) {
                        if (numberField.getText().toString().equals("Enter your number")) {
                            numberField.setText("");
                        }

                    }
                }
        );
        numberField.addTextChangedListener(new TextWatcher() {

            @Override
            public void onTextChanged(CharSequence s, int start, int before,
                                      int count) {
                if(numberField.getText().toString().length()>10 )
                {
                    numberField.setTextColor(Color.RED);
                }
                else
                {
                    numberField.setTextColor(Color.WHITE);
                }
            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count,
                                          int after) {
            }

            @Override
            public void afterTextChanged(Editable s) {

                }
        });
        numberField.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_DONE)
                {
                    String numf=numberField.getText().toString();
                    if(!numf.matches("[0-9]{10}"))
                    {
                        Toast.makeText(getApplicationContext(),"Please enter a valid 10 digit number.",Toast.LENGTH_SHORT).show();
                        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                        imm.showSoftInput(numberField,1);
                        numberField.requestFocus();

                    }
                }
                return false;
            }
        });



//        aboutUs.setOnClickListener(
//                new View.OnClickListener() {
//                    @Override
//                    public void onClick(View view) {
//                        startActivity(new Intent(SignUpOne.this,AboutUs.class));
//                    }
//                }
//        );

        otp.setOnClickListener(
                new View.OnClickListener() {
                    @RequiresApi(api = Build.VERSION_CODES.CUPCAKE)
                    @Override
                    public void onClick(View view) {
//                        String nameF = nameField.getText().toString();


                        if (!isNetworkAvailable()) {
                            Toast.makeText(getApplicationContext(), "start internet nigga", Toast.LENGTH_SHORT).show();
                            return;
                        }
                        final String numF = numberField.getText().toString();
//                        if (nameF.equals("")||nameF.equals("Enter your name")) {
//                            nameField.requestFocus();
//                            InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
//                            imm.showSoftInput(nameField, InputMethodManager.SHOW_IMPLICIT);
//                            Toast toast = Toast.makeText(getApplicationContext(), "FIELD REQUIRED", Toast.LENGTH_SHORT);
//                            toast.show();
//                            return;
//                        }
                        if (numF.equals("") || numF.equals("Enter your number")) {
                            numberField.requestFocus();
                            InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                            imm.showSoftInput(numberField, InputMethodManager.SHOW_IMPLICIT);
                            Toast toast = Toast.makeText(getApplicationContext(), "FIELD REQUIRED", Toast.LENGTH_SHORT);
                            toast.show();
                            return;
                        }
//                        mDatabase = FirebaseDatabase.getInstance().getReference();
//                        mDatabase.addListenerForSingleValueEvent(new ValueEventListener() {
//                            @Override
//                            public void onDataChange(DataSnapshot dataSnapshot) {
//                                for (DataSnapshot data : dataSnapshot.getChildren()) {
//                                    if (data.child(numberField.getText().toString()).exists()) {
//              number existes in database
//                                        startActivity(new Intent(SignUpOne.this, LoginActivity.class));
//                                        Intent i = new Intent(SignUpOne.this, OTPAuth.class);
//                                        i.putExtra("numberField", numF);
//                                        startActivity(i);


//                                    } else {
//              new user need to add data to database.
                                        Intent i = new Intent(SignUpOne.this, OTPAuth.class);
                                        i.putExtra("numberField", numF);
//                                        Toast.makeText(getApplicationContext(), numF, Toast.LENGTH_SHORT).show();
                                        startActivity(i);
                                        finish();
//                                    }
//                                }


//                            @Override
//                            public void onCancelled(DatabaseError e) {
//                            }
//                        });


////                        i.putExtra("nameField", nameF);
//                        Intent i = new Intent(SignUpOne.this,GetName.class);
//                        i.putExtra("numF",numF);
//                        Toast.makeText(getApplicationContext(), numF, Toast.LENGTH_SHORT).show();
//                        startActivity(i);
                    }
                }
        );



    }

//    @SuppressLint("MissingSuperCall")
//    @Override
//    public void onPause()
//    {
//        finish();
//    }


    private boolean isNetworkAvailable()
    {
        ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }
}



