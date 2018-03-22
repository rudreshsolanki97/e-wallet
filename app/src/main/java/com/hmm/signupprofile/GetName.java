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





public class GetName extends AppCompatActivity {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_get_name);
        final  String numF;
        final EditText pass=(EditText)findViewById(R.id.password);
        final EditText nameField = (EditText) findViewById(R.id.nameInput);



        nameField.setOnFocusChangeListener(
                new View.OnFocusChangeListener() {
                    @Override
                    public void onFocusChange(View view, boolean b) {
                        if (nameField.getText().toString().equals("USERNAME")) {
                            nameField.setText("");
                        }
                    }
                }
        );
        pass.setOnFocusChangeListener(
                new View.OnFocusChangeListener() {
                    @Override
                    public void onFocusChange(View view, boolean b) {
                        if (pass.getText().toString().equals("PASSWORD")) {
                            pass.setText("");
                        }
                    }
                });




        Intent i1;
        i1=getIntent();
        numF = i1.getStringExtra("numF");
        ImageButton aboutUs = (ImageButton) findViewById(R.id.aboutUs);
        Button otp = (Button) findViewById(R.id.otp);
//        Toast.makeText(getApplicationContext(),numF.getClass().toString(), Toast.LENGTH_SHORT).show();

        otp.setOnClickListener(
                new View.OnClickListener() {
                    @RequiresApi(api = Build.VERSION_CODES.CUPCAKE)
                    @Override
                    public void onClick(View view) {
//                        Toast.makeText(getApplicationContext(),numF, Toast.LENGTH_SHORT).show();
                        if(!isNetworkAvailable())
                        {
                            Toast.makeText(getApplicationContext(),"NO INTERNET CONNECTIVITY !",Toast.LENGTH_SHORT).show();
                            return;
                        }
                        String nameF = nameField.getText().toString();
                        String password = pass.getText().toString();
                        if (nameF.equals("")||nameF.equals("Enter your name")) {
                            nameField.requestFocus();
                            InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                            imm.showSoftInput(nameField, InputMethodManager.SHOW_IMPLICIT);
                            Toast toast = Toast.makeText(getApplicationContext(), "FIELD REQUIRED", Toast.LENGTH_SHORT);
                            toast.show();
                            return;
                        }
                        if(password.equals("")||password.equals("Enter your name")) {
                            nameField.requestFocus();
                            InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                            imm.showSoftInput(pass, InputMethodManager.SHOW_IMPLICIT);
                            Toast toast = Toast.makeText(getApplicationContext(), "FIELD REQUIRED", Toast.LENGTH_SHORT);
                            toast.show();
                            return;
                        }
                        Intent next = new Intent(GetName.this,OTPAuth.class);
                        next.putExtra("nameField",nameF);
                        next.putExtra("numberField",numF);
                        next.putExtra("password",password);
                        startActivity(next);
                        finish();
                    }
                }
        );

//        Spinner dropdown = (Spinner) findViewById(R.id.spinner1);
//        String[] items = new String[]{"1", "2", "three"};
//        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, items);
//        dropdown.setAdapter(adapter);
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



