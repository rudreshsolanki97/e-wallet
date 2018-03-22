package com.hmm.signupprofile;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
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



import com.hmm.signupprofile.helper.LocaleHelper;


public class mainscreen extends AppCompatActivity {


    private AccountHeader headerResult = null;
    private Drawer result = null;
    private static final int PROFILE_SETTING = 100000;





    DatabaseReference mDatabase;
    FirebaseAuth mAuth;
    TextView loading;
    ImageView btnBalance,btnTransfer,btnHistory,btnFaqs;

    String name1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mainscreen);
        loading = (TextView) findViewById(R.id.loading);

        mAuth = FirebaseAuth.getInstance();

        final FirebaseUser current = mAuth.getCurrentUser();




        btnBalance = (ImageView) findViewById(R.id.btnBalance);
        btnTransfer = (ImageView) findViewById(R.id.btnTransfer);
        btnHistory = (ImageView) findViewById(R.id.btnHistory);
        btnFaqs = (ImageView) findViewById(R.id.btnFaqs);

        btnBalance.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i =new Intent(mainscreen.this,ProfileActivity.class);
                mDatabase = FirebaseDatabase.getInstance().getReference();
                final String[] name = new String[1];
                mDatabase.child("users").addValueEventListener(
                        new ValueEventListener() {
                            @Override
                            public void onDataChange(DataSnapshot dataSnapshot) {
                                for (DataSnapshot data : dataSnapshot.getChildren()) {
                                    if (data.hasChild("username")) {
                                        if (data.child("username").getValue().toString().matches(current.getEmail())) {
                                            if (data.hasChild("name")) {
                                                name[0] = data.child("name").getValue().toString();
//                                                                Toast.makeText(getApplication(), data.child("username").getValue().toString() + name[0], Toast.LENGTH_SHORT).show();
                                                Intent in = new Intent(mainscreen.this, ProfileActivity.class);
                                                in.putExtra("name", name[0]);
                                                in.putExtra("phno", data.child("phonenumber").getValue().toString());
                                                in.putExtra("email", data.child("username").getValue().toString());
//                                                                in.putExtra("medHistory", data.child("medHistory").getValue().toString());
//                                                                in.putExtra("prevBills", data.child("prevBills").getValue().toString());
                                                in.putExtra("balance",data.child("balance").getValue().toString());
//                                                                if (data.child("gender").getValue().toString().matches("male"))
//                                                                    in.putExtra("male", true);
//                                                                else
//                                                                    in.putExtra("female", true);
                                                startActivity(in);
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
        });

        btnTransfer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i =new Intent(mainscreen.this,MakePayment.class);
                startActivity(i);
            }
        });
        btnHistory.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i =new Intent(mainscreen.this,transactionHistory.class);
                startActivity(i);
            }
        });

        btnFaqs.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i =new Intent(mainscreen.this,MainActivity.class);
                loading.setVisibility(View.VISIBLE);
                startActivity(i);
            }
        });





        final SharedPreferences sf = getSharedPreferences("LaunchAppOnShake", MODE_APPEND);
        final SharedPreferences.Editor sfed = sf.edit();
        loading.setVisibility(View.INVISIBLE);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                loading.setVisibility(View.VISIBLE);
                startActivity(new Intent(mainscreen.this,MainActivity.class));

            }
        });





        android.support.v7.widget.Toolbar toolbar = (android.support.v7.widget.Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);


//        mDatabase= FirebaseDatabase.getInstance().getReference();
//        mDatabase.child("users").addValueEventListener(
//        new ValueEventListener() {
//            @Override
//            public void onDataChange(DataSnapshot dataSnapshot) {
//                for(DataSnapshot data: dataSnapshot.getChildren()) {
//                    if(data.child("username").getValue().toString().matches(current.getEmail())) {
//                        if (data.hasChild("name")) {
//                            name1 = data.child("name").getValue().toString();
//                            Toast.makeText(getApplication(), data.child("username").getValue().toString() + name1, Toast.LENGTH_SHORT).show();
//                        }
//                    }
//                }
//            }
//
//            @Override
//            public void onCancelled(DatabaseError databaseError) {
//
//            }
//        }
//);


        final FirebaseAuth mAuth = FirebaseAuth.getInstance();

//        DatabaseReference mdatabase = FirebaseDatabase.getInstance().getReference();


        if (current == null) {
            startActivity(new Intent(mainscreen.this, switchActivity.class));
            finish();
        } else {
//            String parts[] = current.getEmail().toString().split("@");
        }
        name1 = getIntent().getStringExtra("name");
//        Toast.makeText(getApplication(), "one" + name1, Toast.LENGTH_SHORT).show();
        final IProfile profile = new ProfileDrawerItem().withName(name1).withEmail(current.getEmail()).withIdentifier(100);
        headerResult = new AccountHeaderBuilder()
                .withActivity(this)
                .withTranslucentStatusBar(true)
                .withHeaderBackground(R.color.colorPrimary)
                .addProfiles(profile)
                .withOnAccountHeaderListener(new AccountHeader.OnAccountHeaderListener() {
                    @Override
                    public boolean onProfileChanged(View view, IProfile profile, boolean current) {
                        return false;
                    }
                })
                .withSavedInstance(savedInstanceState)
                .build();

        result = new DrawerBuilder()
                .withActivity(this)
                .withToolbar(toolbar)
                .withHasStableIds(true)
                .withAccountHeader(headerResult) //set the AccountHeader we created earlier for the header
                .addDrawerItems(
                        new PrimaryDrawerItem().withName("Home").withIdentifier(1).withSetSelected(true),
                        new ExpandableDrawerItem().withName("My Account").withIdentifier(2).withSelectable(false).withSubItems(
                                new SecondaryDrawerItem().withName("Profile(Balance)").withLevel(2).withIdentifier(2001)
                        ),
                        new PrimaryDrawerItem().withName("Make Payment").withIdentifier(6).withSelectable(true),
                        new PrimaryDrawerItem().withName("Transaction").withIdentifier(3).withSelectable(true),
                        new PrimaryDrawerItem().withName("Reminders").withIdentifier(4).withSelectable(true),

                        new PrimaryDrawerItem().withName("Add Balance").withIdentifier(5).withSelectable(true),

                        new ExpandableDrawerItem().withName("Help").withIdentifier(9).withSelectable(false).withSubItems(
                                new SecondaryDrawerItem().withName("FAQ").withLevel(2).withIdentifier(9001)
                        ),

                        new PrimaryDrawerItem().withName("Logout").withIdentifier(10).withSelectable(true).withOnDrawerItemClickListener(new Drawer.OnDrawerItemClickListener() {
                            @Override
                            public boolean onItemClick(View view, int position, IDrawerItem drawerItem) {
                                mAuth.signOut();
                                startActivity(new Intent(mainscreen.this, switchActivity.class));
                                finish();
                                return true;
                            }
                        })

                )
                .withOnDrawerItemClickListener(new Drawer.OnDrawerItemClickListener() {
                    @Override
                    public boolean onItemClick(View view, int position, IDrawerItem drawerItem) {
                        //check if the drawerItem is set.
                        //there are different reasons for the drawerItem to be null
                        //--> click on the header
                        //--> click on the footer
                        //those items don't contain a drawerItem
//                        Toast.makeText(getApplicationContext(), drawerItem.getIdentifier() + "", Toast.LENGTH_SHORT).show();
                        long identifier = drawerItem.getIdentifier();


                        if (drawerItem != null) {
//                            Toast.makeText(getApplicationContext(), identifier + "", Toast.LENGTH_SHORT).show();

                            if (identifier == 1) {
                            }


                            if (identifier == 2) {

                            }
                            if (identifier == 2001) {
                                Intent i = new Intent(mainscreen.this, ProfileActivity.class);
//                                Toast.makeText(getApplicationContext(), identifier + "", Toast.LENGTH_SHORT).show();
                                mDatabase = FirebaseDatabase.getInstance().getReference();
                                final String[] name = new String[1];
                                mDatabase.child("users").addValueEventListener(
                                        new ValueEventListener() {
                                            @Override
                                            public void onDataChange(DataSnapshot dataSnapshot) {
                                                for (DataSnapshot data : dataSnapshot.getChildren()) {
                                                    if (data.hasChild("username")) {
                                                        if (data.child("username").getValue().toString().matches(current.getEmail())) {
                                                            if (data.hasChild("name")) {
                                                                name[0] = data.child("name").getValue().toString();
//                                                                Toast.makeText(getApplication(), data.child("username").getValue().toString() + name[0], Toast.LENGTH_SHORT).show();
                                                                Intent in = new Intent(mainscreen.this, ProfileActivity.class);
                                                                in.putExtra("name", name[0]);
                                                                in.putExtra("phno", data.child("phonenumber").getValue().toString());
                                                                in.putExtra("email", data.child("username").getValue().toString());
//                                                                in.putExtra("medHistory", data.child("medHistory").getValue().toString());
//                                                                in.putExtra("prevBills", data.child("prevBills").getValue().toString());
                                                                in.putExtra("balance",data.child("balance").getValue().toString());
//                                                                if (data.child("gender").getValue().toString().matches("male"))
//                                                                    in.putExtra("male", true);
//                                                                else
//                                                                    in.putExtra("female", true);
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
                            if (identifier == 2002) {

                            }
                            if (identifier == 3) {
                                startActivity(new Intent(mainscreen.this,transactionHistory.class));
                            }
                            if (identifier == 3001) {

                            }
                            if (identifier == 3002) {

                            }
                            if (identifier == 3003) {

                            }
                            if (identifier == 3004) {

                            }
                            if (identifier == 4) {
                                startActivity(new Intent(mainscreen.this,AlarmMe.class));
                            }
                            if (identifier == 5) {

                                startActivity(new Intent(mainscreen.this,AddBalance.class));
                            }
                            if (identifier == 6) {

                                startActivity(new Intent(mainscreen.this,MakePayment.class));


                            }
                            if (identifier == 7) {
                            }
                            if (identifier == 7001) {
                                String st = sf.getString("language", "");
                                int pos;
                                if (st.matches("en"))
                                    pos = 0;
                                else if (st.matches("hi"))
                                    pos = 1;
                                else
                                    pos = 0;
                                new AlertDialog.Builder(mainscreen.this)
                                        .setTitle("SET APPLICATION LANGUAGE")
                                        .setSingleChoiceItems(new String[]{"ENGLISH", "HINDI"}, pos, new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialogInterface, int i) {
                                                if (i == 0) {
                                                    sfed.putString("language", "en");
                                                    updateViews("en");
//                                                    Toast.makeText(getApplicationContext(), i + "", Toast.LENGTH_SHORT).show();
                                                } else if (i == 1) {
                                                    sfed.putString("language", "hi");
                                                    updateViews("hi");
//                                                    Toast.makeText(getApplicationContext(), i + "", Toast.LENGTH_SHORT).show();
                                                }
                                            }

                                        }).setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialogInterface, int i) {
                                        sfed.commit();
                                        startActivity(new Intent(mainscreen.this, switchActivity.class));
                                        finish();
                                    }
                                }).create().show();


                            }
                            if (identifier == 7002) {
                            }
                            if (identifier == 7003) {
//                                new AlertDialog.Builder(mainscreen.this)
//                                        .setTitle("LAUNCH ON SHAKE")
//                                        .setMessage("WANT TO LAUNCH APPLICATION ON LAUNCH?")
//                                        .setNegativeButton("NO", new DialogInterface.OnClickListener() {
//                                                    public void onClick(DialogInterface arg0, int arg1) {
//                                                        sfed.clear();
//                                                        sfed.putBoolean("launchOnShake", false);
//                                                        sfed.commit();
//                                                        Intent x = new Intent(Services_map.this, shakerService.class);
//                                                        x.putExtra("stop", true);
//                                                        stopService(x);
//                                                    }
//                                                }
//                                        )
//                                        .setPositiveButton("YES", new DialogInterface.OnClickListener() {
//                                            public void onClick(DialogInterface arg0, int arg1) {
//                                                sfed.clear();
//                                                sfed.putBoolean("launchOnShake", true);
//                                                sfed.commit();
//                                                startService(new Intent(Services_map.this, shakerService.class));
//
//                                            }
//                                        }).create().show();
                            }
                            if (identifier == 8) {
                            }
                            if (identifier == 8001) {
                            }
                            if (identifier == 8002) {
                            }
                            if (identifier == 8003) {
                            }
                            if (identifier == 8004) {
                            }
                            if (identifier == 9) {
                            }
                            if (identifier == 9001) {
                                startActivity(new Intent(mainscreen.this,MainActivity.class));

                            }
                            if (identifier == 10001) {
//                                startActivity(new Intent(main.this, AboutUs.class));
                            }
                        }
                        return false;
                    }

                })
                .withSavedInstance(savedInstanceState)
                .withShowDrawerOnFirstLaunch(false)
                .build();

        if (savedInstanceState == null) {
            // set the selection to the item with the identifier 11
            result.setSelection(1, true);

            //set the active profile
            headerResult.setActiveProfile(profile);
        }

        Intent i = getIntent();
        int currentItem = i.getIntExtra("currentItem", 0);
        if (currentItem != 0) {
            result.setSelection(currentItem, true);
        }


    }
    @Override
    public void onResume()
    {
        super.onResume();
        loading.setVisibility(View.INVISIBLE);
    }


    public void updateViews(String languageCode) {
        Context context = com.hmm.signupprofile.helper.LocaleHelper.setLocale(this, languageCode);
        Resources resources = context.getResources();


    }

}
