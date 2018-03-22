package com.hmm.signupprofile;

import android.Manifest;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.content.res.AssetManager;
import android.os.Bundle;
import android.os.Environment;
import android.speech.RecognizerIntent;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.Menu;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;

import org.alicebot.ab.AIMLProcessor;
import org.alicebot.ab.Bot;
import org.alicebot.ab.Chat;
import org.alicebot.ab.Graphmaster;
import org.alicebot.ab.MagicBooleans;
import org.alicebot.ab.MagicStrings;
import org.alicebot.ab.PCAIMLProcessorExtension;
import org.alicebot.ab.Timer;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Locale;


import android.util.DisplayMetrics;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;


public class MainActivity extends AppCompatActivity {


    private ListView mListView;
    private FloatingActionButton mButtonSend;
    private EditText mEditTextMessage;
    private ImageView mImageView;
    public Bot bot;
    public static Chat chat;
    private ChatMessageAdapter mAdapter;
    public static final int ASK_MULTIPLE_PERMISSION_REQUEST_CODE = 1;
    DatabaseReference mDatabase;
    FirebaseAuth mAuth;
    String response = "";
    String message = "";
    Boolean found;
    ImageView speak;


    boolean makePaymentSet = false;
    private final int REQ_CODE_SPEECH_INPUT = 100;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mListView = (ListView) findViewById(R.id.listView);
        mButtonSend = (FloatingActionButton) findViewById(R.id.btn_send);
        mEditTextMessage = (EditText) findViewById(R.id.et_message);
        mImageView = (ImageView) findViewById(R.id.iv_image);
        mAdapter = new ChatMessageAdapter(this, new ArrayList<ChatMessage>());
        mListView.setAdapter(mAdapter);

        speak = (ImageView) findViewById(R.id.speak);


        DisplayMetrics dm = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(dm);

        mAuth = FirebaseAuth.getInstance();
        FirebaseUser current = mAuth.getCurrentUser();


        double width = dm.widthPixels;
        double height = dm.heightPixels;
        int w1 = (int) (width * 0.95);
        int h1 = (int) (height * 0.7);
        getWindow().setLayout(w1, h1);
        getWindow().setGravity(Gravity.BOTTOM);

    }

    @Override
    public void onStart() {

        super.onStart();


        speak.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        speak = (ImageView) findViewById(R.id.speak);

                        // hide the action bar
//                        getActionBar().hide();

                        speak.setOnClickListener(new View.OnClickListener() {

                            @Override
                            public void onClick(View v) {
                                promptSpeechInput();
                            }
                        });

                    }

                    /**
                     * Showing google speech input dialog
                     * */
                    private void promptSpeechInput() {
                        Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
                        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL,
                                RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
                        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale.getDefault());
                        intent.putExtra(RecognizerIntent.EXTRA_PROMPT,
                                "Speak Now");
                        try {

                            startActivityForResult(intent, REQ_CODE_SPEECH_INPUT);
                        } catch (ActivityNotFoundException a) {

                        }
                    }

                    /**
                     * Receiving speech input
                     * */





                });


        ActivityCompat.requestPermissions(MainActivity.this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.MOUNT_UNMOUNT_FILESYSTEMS, Manifest.permission.INTERNET, Manifest.permission.READ_CONTACTS}, ASK_MULTIPLE_PERMISSION_REQUEST_CODE);
        mimicOtherMessage("Welcome to chatbot!!\nThese are some of the basic commands :\ncheck balance \n pay 100 to xyz/10 Digit Number \n show transfers to 10 Digit Mobile Number \n get my wallet statement\n etc...");
        mButtonSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                message = mEditTextMessage.getText().toString();
                //bot

                response = chat.multisentenceRespond(mEditTextMessage.getText().toString());
                if(makePaymentSet)
                {
                    response = "ok wait!!!!!";
                }
                if(response.matches("Ok!I am checking your balance"))
                {
                    mDatabase= FirebaseDatabase.getInstance().getReference();
                    mDatabase.child("users").addValueEventListener(
                            new ValueEventListener() {
                                @Override
                                public void onDataChange(DataSnapshot dataSnapshot) {
                                    for (DataSnapshot data : dataSnapshot.getChildren()) {
                                        if (data.hasChild("username")) {
                                            if (data.child("username").getValue().toString().matches(mAuth.getCurrentUser().getEmail())) {
                                                if (data.hasChild("name")) {
                                                 response =  "Your balance is : "+data.child("balance").getValue().toString();
                                                    sendMessage(message);
                                                    mimicOtherMessage(response);
                                                    mEditTextMessage.setText("");
                                                    mListView.setSelection(mAdapter.getCount() - 1);
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
                else if(response.startsWith("Paying"))
                {
                    found=false;
                    mDatabase = FirebaseDatabase.getInstance().getReference();
                    sendMessage(message);
                    mDatabase.child("users").addValueEventListener(
                            new ValueEventListener() {
                                @Override
                                public void onDataChange(DataSnapshot dataSnapshot) {
                                        for (final DataSnapshot data : dataSnapshot.getChildren()) {
                                            if (data.hasChild("name")) {
                                                String name="";
                                                final String[] arr = message.split(" ");
                                                for(int i =0;i<arr.length-1;i++)
                                                {
                                                    if(arr[i].matches("to"));
                                                    {
                                                        name=arr[i+1];
                                                        if(data.child("name").getValue().toString().matches(name.split("@")[0]))
                                                        {
                                                            response = "Make payment to "+name+" ("+data.getKey()+") ??(yes/no)";
                                                            found=true;
                                                            makePaymentSet = true;
//                                                            sendMessage(message);
                                                            if(!response.startsWith("Make payment")){response="Not found user";}

                                                            mimicOtherMessage(response);
                                                            mEditTextMessage.setText("");
                                                            mListView.setSelection(mAdapter.getCount() - 1);
                                                            mButtonSend.setOnClickListener(
                                                                    new View.OnClickListener() {
                                                                        @Override
                                                                        public void onClick(View view) {
                                                                            message = mEditTextMessage.getText().toString();
//                                                                   Toast.makeText(getApplicationContext(),message,Toast.LENGTH_SHORT).show();
                                                                            if(message.matches("yes")||message.matches("Yes")||message.matches("YES")||message.matches("ok"))
                                                                            {
                                                                                Intent i = new Intent(MainActivity.this,MakePayment.class);
                                                                                i.putExtra("number",data.getKey().toString());
                                                                                for(int k =0;k<arr.length;k++)
                                                                                {
                                                                                    if(arr[k].matches("pay")||arr[k].matches("Pay"))
                                                                                    {
                                                                                        i.putExtra("amount",arr[k+1]);
                                                                                    }
                                                                                }
                                                                                startActivity(i);finish();
                                                                            }
                                                                            else
                                                                            {
                                                                                mimicOtherMessage("payment canceled!! having second thoughts are we?");
                                                                                mEditTextMessage.setText("");
                                                                                mListView.setSelection(mAdapter.getCount() - 1);
                                                                                finish();
                                                                            }
                                                                        }
                                                                    }
                                                            );

                                                            break;
                                                        }
                                                        else if(data.getKey().matches(name))
                                                        {
                                                            response = "Make payment to "+name+" ( "+data.child("name")+" ) ??";
                                                            found=true;
                                                            makePaymentSet = true;
                                                            sendMessage(message);
                                                            if(!response.startsWith("Make payment")){response="Not found user";}

                                                            mimicOtherMessage(response);
                                                            mEditTextMessage.setText("");
                                                            mListView.setSelection(mAdapter.getCount() - 1);
                                                            break;
                                                        }
                                                        else
                                                        {
                                                            if(!found) {
                                                                response = "No such users!!";
//                                                                sendMessage(message);
//                                                                mimicOtherMessage(response);
                                                            }
                                                        }
                                                    }

                                                }

                                            }
                                        }
//                                    mimicOtherMessage(response);
                                    }

                                @Override
                                public void onCancelled(DatabaseError databaseError) {

                                }

                            }

                    );

                }

                else  if (response.startsWith("Ok!Getting transfers to"))
                {
                    String[] arr = response.split("to ");
                    final String name = arr[1];
                    sendMessage(message);
                    response = "";
                    mDatabase = FirebaseDatabase.getInstance().getReference();
                    mDatabase.child("users").addValueEventListener(
                            new ValueEventListener() {
                                @Override
                                public void onDataChange(DataSnapshot dataSnapshot) {
                                    for (DataSnapshot data : dataSnapshot.getChildren()) {
                                        if (data.hasChild("username"))
                                            if (data.child("username").getValue().toString().matches(mAuth.getCurrentUser().getEmail())) {

                                                if(data.child("debit").hasChild(name))
                                                {
                                                    response = response+"DEBIT  :  ";
//                                                        Iterable<DataSnapshot> all = data.child("debit").getChildren();
//                                                        for(DataSnapshot child : all )
//                                                        {
//                                                            if(child.hasChildren())
//                                                            {
//                                                                Item x1 = new Item();
//                                                                x1.setId(1);
//                                                                x1.setName(child.getKey());
//                                                                String ko="";
//                                                                Iterable<DataSnapshot> childs = child.getChildren();
//                                                                for(DataSnapshot one : childs)
//                                                                {
//                                                                    ko = ko+one.getKey()+one.getValue()+"||||";
//                                                                }
//                                                                x1.setPrice(ko);
//                                                                cartList.add(x1);
//                                                                mAdapter.notifyDataSetChanged();
//                                                            }
//                                                        }



                                                    Iterable<DataSnapshot> many = data.getChildren();

                                                    for(DataSnapshot child : many)
                                                    {
                                                        for (DataSnapshot chi : child.getChildren())
                                                        {
                                                            if(chi.getKey().matches(name))
                                                                if(chi.hasChildren())
                                                                {
                                                                    String[] all = chi.getValue().toString().split(",");

                                                                    for(String a : all) {
                                                                        response += a;
                                                                    }
                                                                }
                                                        }
                                                        mimicOtherMessage(response);
                                                        mEditTextMessage.setText("");
                                                        mListView.setSelection(mAdapter.getCount() - 1);
                                                    }



//                                                Iterable<DataSnapshot> arr =  data.child("debit").getChildren();
//                                                for(DataSnapshot  i : arr)
//                                                {
//                                                    if(i.hasChildren()) {
//                                                        try {
//                                                            JSONObject reader = new JSONObject(i.toString());
//                                                            one.setText(reader.get("9867376117").toString());
//                                                        } catch (JSONException e) {
//                                                            e.printStackTrace();
//                                                        }
//                                                      String [] w=i.toString().split("^^");
//
//                                                        one.setText(one.getText()+w[1]);
//
//                                                        Item x1 = new Item();
//                                                        x1.setId(1);
//                                                        x1.setName(name);
//                                                        x1.setPrice(i.getValue().toString());
//                                                        mAdapter.notifyDataSetChanged();
//
//
//
//                                                        mAdapter.notifyDataSetChanged();

//                                                    Item x1 = new Item();
//                                                    x1.setId(1);
//                                                    x1.setName(name);
//                                                    x1.setPrice((i.split("=")[1]));
//                                                    mAdapter.notifyDataSetChanged();
//                                                    }
//                                                }

                                                }
                                                if(data.child("credit").hasChild(name))
                                                {
                                                    response = "CREDIT  :  ";

                                                    Iterable<DataSnapshot> many = data.getChildren();
                                                    for(DataSnapshot child : many)
                                                    {
                                                        for (DataSnapshot chi : child.getChildren())
                                                        {
                                                            if(chi.getKey().matches(name))
                                                                if(chi.hasChildren())
                                                                {
                                                                    String[] all = chi.getValue().toString().split(",");

                                                                    for(String a : all) {
                                                                        response += a;
                                                                    }
                                                                }
                                                        }
                                                    }
                                                    mimicOtherMessage(response);
                                                    mEditTextMessage.setText("");
                                                    mListView.setSelection(mAdapter.getCount() - 1);


                                                }
//                                                mimicOtherMessage(response);

                                            }
//                                        mimicOtherMessage(response);

                                    }
                                }
                                @Override
                                public void onCancelled(DatabaseError databaseError)
                                {
                                }
                            }

                    );

                }
                else if(response.startsWith("OK!Getting your wallet statement"))
                {
                    response = "";
                    mDatabase  = FirebaseDatabase.getInstance().getReference();
                    mDatabase.child("users").addValueEventListener(
                            new ValueEventListener() {
                                @Override
                                public void onDataChange(DataSnapshot dataSnapshot) {
                                    for (DataSnapshot data : dataSnapshot.getChildren()) {
                                        if (data.hasChild("username"))
                                            if (data.child("username").getValue().toString().matches(mAuth.getCurrentUser().getEmail())) {
                                                if(data.hasChild("debit"))
                                                    if(data.child("debit").hasChildren())
                                                    {
                                                        response+=" DEBIT :  {";
                                                        Iterable<DataSnapshot> all = data.child("debit").getChildren();
                                                        for(DataSnapshot child : all )
                                                        {
                                                            if(child.hasChildren())
                                                            {

//                                                                Item x1 = new Item();
//                                                                x1.setId(1);
//                                                                x1.setName(child.getKey());
//                                                                String ko="";
                                                                Iterable<DataSnapshot> childs = child.getChildren();
                                                                for(DataSnapshot one : childs)
                                                                {
                                                                    response+=one.getKey()+" ₹ "+one.getValue()+"  ";
                                                                }
//                                                                x1.setPrice(ko);
//                                                                cartList.add(x1);
//                                                                mAdapter.notifyDataSetChanged();
                                                            }
                                                        }
                                                    response+=" } //END OF DEBIT .";
                                                        mimicOtherMessage(response);
                                                    response="";
                                                    }
                                                if (data.hasChild("credit"))
//                                                    if(data.child("credit").hasChild(name))
                                                {
                                                    response = "CREDIT   :  ";
                                                        Iterable<DataSnapshot> all = data.child("credit").getChildren();
                                                        for(DataSnapshot child : all ) {
                                                            if (child.hasChildren()) {
//                                                                Item x1 = new Item();
//                                                                x1.setId(1);
//                                                                x1.setName(child.getKey());
//                                                                String ko="";
//                                                                Iterable<DataSnapshot> childs = child.getChildren();
//                                                                for(DataSnapshot one : childs)
//                                                                {
//                                                                    ko = ko+one.getKey()+one.getValue()+"||||";
//                                                                }
//                                                                x1.setPrice(ko);
//                                                                cartList.add(x1);
//                                                                mAdapter.notifyDataSetChanged();

                                                                Iterable<DataSnapshot> childs = child.getChildren();
                                                                for(DataSnapshot one : childs)
                                                                {
                                                                    response+=one.getKey()+" ₹ "+one.getValue()+". ";
                                                                }
                                                            }
                                                        }


                                                    response+=" } //CREDIT .";
                                                    mimicOtherMessage(response);
                                                    mEditTextMessage.setText("");
                                                    mListView.setSelection(mAdapter.getCount() - 1);
                                                }
                                            }
                                    }
                                }
                                @Override
                                public void onCancelled(DatabaseError databaseError)
                                {
//                                    one.setText(databaseError.toString());
                                }
                            }
                    );
                }
                else
                {
                    response = chat.multisentenceRespond(mEditTextMessage.getText().toString());
                    sendMessage(message);
                    mimicOtherMessage(response);
                    mEditTextMessage.setText("");
                    mListView.setSelection(mAdapter.getCount() - 1);
                }
                if (TextUtils.isEmpty(message)) {
                    return;
                }

                mEditTextMessage.setText("");
                mListView.setSelection(mAdapter.getCount() - 1);
            }
        });
        //checking SD card availablility
//        boolean a = isSDCARDAvailable();
        //receiving the assets from the app directory
        AssetManager assets = getResources().getAssets();
        File jayDir = new File(Environment.getExternalStorageDirectory().toString() + "/hari/bots/Hari");
        boolean b = jayDir.mkdirs();
        if (jayDir.exists()) {
            //Reading the file
            try {
                for (String dir : assets.list("Hari")) {
                    File subdir = new File(jayDir.getPath() + "/" + dir);
                    boolean subdir_check = subdir.mkdirs();
                    for (String file : assets.list("Hari/" + dir)) {
                        File f = new File(jayDir.getPath() + "/" + dir + "/" + file);
                        if (f.exists()) {
                            continue;
                        }
                        InputStream in = null;
                        OutputStream out = null;
                        in = assets.open("Hari/" + dir + "/" + file);
                        out = new FileOutputStream(jayDir.getPath() + "/" + dir + "/" + file);
                        //copy file from assets to the mobile's SD card or any secondary memory
                        copyFile(in, out);
                        in.close();
//                        in = null;
                        out.flush();
                        out.close();
//                        out = null;
                    }
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        //get the working directory
        MagicStrings.root_path = Environment.getExternalStorageDirectory().toString() + "/hari";
        System.out.println("Working Directory = " + MagicStrings.root_path);
        AIMLProcessor.extension =  new PCAIMLProcessorExtension();
        //Assign the AIML files to bot for processing
        bot = new Bot("Hari", MagicStrings.root_path, "chat");
        chat = new Chat(bot);
        String[] args = null;
        mainFunction(args);




    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult( requestCode,  resultCode, data);
        switch (requestCode) {
            case REQ_CODE_SPEECH_INPUT: {
                if (resultCode == RESULT_OK && null != data) {

                    ArrayList<String> result = data
                            .getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);
                    String speech = result.get(0);
                    mEditTextMessage.setText(speech);
                    break;
                }
            }

        }
    }

    private void sendMessage(String message) {

        ChatMessage chatMessage = new ChatMessage(message, true, false);
        mAdapter.add(chatMessage);

        //mimicOtherMessage(message);
    }

    private void mimicOtherMessage(String message) {
        ChatMessage chatMessage = new ChatMessage(message, false, false);
        mAdapter.add(chatMessage);
    }

    private void sendMessage() {
        ChatMessage chatMessage = new ChatMessage(null, true, true);
        mAdapter.add(chatMessage);

        mimicOtherMessage();
    }

    private void mimicOtherMessage() {
        ChatMessage chatMessage = new ChatMessage(null, false, true);
        mAdapter.add(chatMessage);
    }
    //check SD card availability
    public static boolean isSDCARDAvailable(){
        return Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED);
    }
    //copying the file
    private void copyFile(InputStream in, OutputStream out) throws IOException {
        byte[] buffer = new byte[1024];
        int read;
        while((read = in.read(buffer)) != -1){
            out.write(buffer, 0, read);
        }
    }
    //Request and response of user and the bot
    public static void mainFunction (String[] args) {
        MagicBooleans.trace_mode = false;
        System.out.println("trace mode = " + MagicBooleans.trace_mode);
        Graphmaster.enableShortCuts = true;
        Timer timer = new Timer();
        String request = "Hello.";
        String response = chat.multisentenceRespond(request);

        System.out.println("Human: "+request);
        System.out.println("Robot: " + response);
    }



}