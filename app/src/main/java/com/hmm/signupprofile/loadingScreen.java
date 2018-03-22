package com.hmm.signupprofile;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Location;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.hmm.signupprofile.helper.LocaleHelper;

import java.util.List;

public class loadingScreen extends AppCompatActivity {

    private static final int ASK_MULTIPLE_PERMISSION_REQUEST_CODE = 1;


    private FirebaseAuth mAuth;
    private TextView appName;
    private TextView unlock;
    private SeekBar sb;
    private TextView slide;
    private TextView disp;
    private String name;
    private DatabaseReference mDatabase;
    private String loggedIn="";
    Intent in;
    boolean ok=false;
    private boolean first=true;
    Animation animFadeOut;

    private static final int ACC_FINE = 0;
    private static final int ACC_COR = 0;

//    @Override
//    public void onStart()
//    {
//        super.onStart();
//        ActivityCompat.requestPermissions(loadingScreen.this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION},1);
//
//
//    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_loading_screen);

        in = new Intent(loadingScreen.this, mainscreen.class);
        final FirebaseAuth mAuth = FirebaseAuth.getInstance();
        final FirebaseUser current = mAuth.getCurrentUser();
        disp = (TextView) findViewById(R.id.disp);
        final SharedPreferences sf = getSharedPreferences("LaunchAppOnShake", MODE_PRIVATE);
        final SharedPreferences.Editor sfed = sf.edit();

        if (sf.getString("language", "").matches("hi"))
            updateViews("hi");
        else
            updateViews("en");

//        if (sf.getBoolean("launchOnShake", false))
//            startService(new Intent(this, shakerService.class));

        Location l = getLastKnownLocation();

//        in.putExtra("lat", l.getLatitude());
//        in.putExtra("lon", l.getLongitude());
//        in.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);

        Intent i = getIntent();
        String status = i.getStringExtra("status");
        i.putExtra("phno",(getIntent()).getStringExtra("phno"));

        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if(user!=null)
        if (!user.getEmail().matches("")) {
            loggedIn="loggedIn";
            status=loggedIn;
        }


        if (status != null)
        {
            if (status.matches("loggedIn"))
            {
                if(mAuth.getCurrentUser()!=null || i.getBooleanExtra("status",false)) {
                    mDatabase = FirebaseDatabase.getInstance().getReference();
                    mDatabase.child("users").addValueEventListener(
                            new ValueEventListener() {
                                @Override
                                public void onDataChange(DataSnapshot dataSnapshot) {
                                    for (DataSnapshot data : dataSnapshot.getChildren()) {
                                        if (data.hasChild("username")) {
                                            if (data.child("username").getValue().toString().matches(current.getEmail())) {
                                                if (data.hasChild("name")) {
                                                    name = data.child("name").getValue().toString();
                                                    Intent in = new Intent(loadingScreen.this, mainscreen.class);
                                                    in.putExtra("name", name);
                                                    in.putExtra("phno", data.child("phonenumber").getValue().toString());
                                                    in.putExtra("email", data.child("username").getValue().toString());
//                                                in.putExtra("medHistory", data.child("medHistory").getValue().toString());
//                                                in.putExtra("prevBills", data.child("prevBills").getValue().toString());
//                                                if (data.child("gender").getValue().toString().matches("male"))
//                                                    in.putExtra("male", true);
//                                                else
//                                                    in.putExtra("female", true);
                                                    startActivity(in);
                                                    finish();
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


                }
            } else {
                startActivity(new Intent(loadingScreen.this, switchActivity.class));
                finish();
            }
        }
        else
        {
            startActivity(new Intent(loadingScreen.this, switchActivity.class));
            finish();
        }



        if(!isNetworkAvailable())
        {
            disp.setTextColor(Color.WHITE);
            disp.setText(R.string.internetError);
        }
        else{
            disp.setText("");
        }
        if(Build.VERSION.SDK_INT >= 23)
        {
            requestPermissions(new String[]{
                        Manifest.permission.CALL_PHONE,
                        Manifest.permission.ACCESS_FINE_LOCATION},
                1);
        }


        animFadeOut = AnimationUtils.loadAnimation(getApplicationContext(),R.anim.fade_in);
//
//        final Handler handler = new Handler();
//        handler.postDelayed(new Runnable() {
//
//            @Override
//            public void run() {
//
//
//                        if(ok)
//                        {
//                            startActivity(in);
//                            finish();
//
//                        }
//                    }
//                //here you can start your Activity B.
//
//        }, 1000);

    }

    //-----------------------METHOD TO CHECK FOR NETWORK CONNECTIVITY---------------------------------------
    private boolean isNetworkAvailable()
    {
        ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }

    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(LocaleHelper.onAttach(base));
    }

    public void updateViews(String languageCode) {
        Context context = LocaleHelper.setLocale(this, languageCode);

    }

    private Location getLastKnownLocation()
    {
        LocationManager locationManager = (LocationManager)getApplicationContext().getSystemService(LOCATION_SERVICE);
        List<String> providers = locationManager.getProviders(true);
        Location bestLocation = null;
        for (String provider : providers)
        {
            if(ActivityCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED)
            {
                ActivityCompat.requestPermissions(loadingScreen.this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION},1);
            }
            Location l = locationManager.getLastKnownLocation(LocationManager.PASSIVE_PROVIDER);
            if (l == null)
            {
                continue;
            }
            if (bestLocation == null || l.getAccuracy() < bestLocation.getAccuracy())
            {
                // Found best last known location: %s", l);
                bestLocation = l;
            }
        }
        return bestLocation;
    }

    public void onBackPressed()
    {
        super.onBackPressed();
        finish();
    }

    @Override
    public void onStart()
    {
        super.onStart();
        if (ActivityCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED || ActivityCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED || ActivityCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.INTERNET) != PackageManager.PERMISSION_GRANTED  || ActivityCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.READ_CONTACTS) != PackageManager.PERMISSION_GRANTED || ActivityCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED)
        {
            ActivityCompat.requestPermissions(loadingScreen.this, new String[]{Manifest.permission.CALL_PHONE,Manifest.permission.ACCESS_FINE_LOCATION,Manifest.permission.INTERNET,Manifest.permission.READ_CONTACTS},ASK_MULTIPLE_PERMISSION_REQUEST_CODE);

//            ActivityCompat.requestPermissions(loadingScreen.this, new String[]{},1);
//            ActivityCompat.requestPermissions(loadingScreen.this, new String[]{},1);
//            ActivityCompat.requestPermissions(loadingScreen.this, new String[]{}, 1);
        }

    }
}
