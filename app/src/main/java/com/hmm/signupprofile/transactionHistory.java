package com.hmm.signupprofile;

import android.content.DialogInterface;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.json.JSONException;
import org.json.JSONObject;
import org.w3c.dom.Text;

import java.lang.reflect.Array;
import java.util.ArrayList;




import android.graphics.Color;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.view.Menu;
import android.view.View;

import net.reduls.sanmoku.dic.Char;

import java.util.ArrayList;
import java.util.List;

public class transactionHistory extends AppCompatActivity implements RecyclerItemTouchHelper.RecyclerItemTouchHelperListener{

    DatabaseReference mDatabase;
    FirebaseAuth mAuth;
    EditText email;
    Button ok;
    String name;
    Button all;



    private static final String TAG = MainActivity.class.getSimpleName();
    private RecyclerView recyclerView;
    private List<Item> cartList;
    private CartListAdapter mAdapter;
    private View coordinatorLayout;
    public static boolean credit;


    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_transaction_history);
        final TextView one = (TextView)findViewById(R.id.one);
        mAuth = FirebaseAuth.getInstance();
        final FirebaseUser current = mAuth.getCurrentUser();
        ok = (Button)findViewById(R.id.ok);
        email = (EditText)findViewById(R.id.name);
        mDatabase = FirebaseDatabase.getInstance().getReference();
        all = (Button)findViewById(R.id.all);





        all.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
//                        int size = cartList.size();
//                        for(int i=0;i<size;i++)
//                        {
//                            cartList.remove(i);
//                        }

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
                                                        Iterable<DataSnapshot> all = data.child("debit").getChildren();
                                                        for(DataSnapshot child : all )
                                                        {
                                                            if(child.hasChildren())
                                                            {
                                                                Item x1 = new Item();
                                                                x1.setId(1);
                                                                x1.setName(child.getKey());
                                                                String ko="";
                                                                Iterable<DataSnapshot> childs = child.getChildren();
                                                                for(DataSnapshot one : childs)
                                                                {
                                                                    ko = ko+one.getKey()+one.getValue()+"\n";
                                                                }
                                                                x1.setPrice(ko);
                                                                cartList.add(x1);
                                                                mAdapter.notifyDataSetChanged();
                                                            }
                                                        }

                                                    }
                                                    if (data.hasChild("credit"))
                                                    if(data.child("credit").hasChild(name))
                                                    {
                                                        Iterable<DataSnapshot> all = data.child("credit").getChildren();
                                                        for(DataSnapshot child : all )
                                                        {
                                                            if(child.hasChildren())
                                                            {
                                                                Item x1 = new Item();
                                                                x1.setId(1);
                                                                x1.setName(child.getKey());
                                                                String ko="";
                                                                Iterable<DataSnapshot> childs = child.getChildren();
                                                                for(DataSnapshot one : childs)
                                                                {
                                                                    ko = ko+one.getKey()+one.getValue()+"||||";
                                                                }
                                                                x1.setPrice(ko);
                                                                cartList.add(x1);
                                                                mAdapter.notifyDataSetChanged();
                                                            }
                                                        }                                                    }
                                                }
                                        }
                                    }
                                    @Override
                                    public void onCancelled(DatabaseError databaseError)
                                    {
                                        one.setText(databaseError.toString());
                                    }
                                }
                        );


                    }
                }
        );











        recyclerView = (RecyclerView) findViewById(R.id.recycler_view);
        coordinatorLayout = findViewById(R.id.coordinator_layout);
        cartList = new ArrayList<Item>(10);
        mAdapter = new CartListAdapter(this, cartList);

        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(transactionHistory.this);
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.addItemDecoration(new DividerItemDecoration(this, DividerItemDecoration.VERTICAL));
        recyclerView.setAdapter(mAdapter);

        // adding item touch helper
        // only ItemTouchHelper.LEFT added to detect Right to Left swipe
        // if you want both Right -> Left and Left -> Right
        // add pass ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT as param
        ItemTouchHelper.SimpleCallback itemTouchHelperCallback = new RecyclerItemTouchHelper(0, ItemTouchHelper.LEFT, this);
        new ItemTouchHelper(itemTouchHelperCallback).attachToRecyclerView(recyclerView);


        // making http call and fetching menu json
        prepareCart();







        ok.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {

//                        int size = cartList.size();
//                        if(size>0)for(int i=0;i<size;i++)
//                        {
//                            mAdapter.removeItem(i);
//                        }
//                        mAdapter.notifyItemRangeRemoved(0,size);

                        name = email.getText().toString().split("@")[0];

                        mDatabase.child("users").addValueEventListener(
                                new ValueEventListener() {
                                    @Override
                                    public void onDataChange(DataSnapshot dataSnapshot) {
                                        for (DataSnapshot data : dataSnapshot.getChildren()) {
                                            if (data.hasChild("username"))
                                                if (data.child("username").getValue().toString().matches(mAuth.getCurrentUser().getEmail())) {
                                                    if(data.child("debit").hasChild(name))
                                                    {
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
                                                                   Item x1 = new Item();
                                                                   x1.setId(1);
                                                                   x1.setName(chi.getKey());
                                                                   x1.setPrice(a.split("=")[1]);
                                                                   cartList.add(x1);
                                                                   mAdapter.notifyDataSetChanged();
                                                               }
                                                                }
                                                            }
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
                                                            Iterable<DataSnapshot> many = data.getChildren();
                                                            for(DataSnapshot child : many)
                                                            {
                                                                Item x1 = new Item();
                                                                x1.setId(1);
                                                                x1.setName(name);
                                                                x1.setPrice(child.getValue().toString().split("=")[1]);
                                                                cartList.add(x1);

                                                                mAdapter.notifyDataSetChanged();
                                                            }


                                                    }

                                                }
                                        }
                                    }
                                    @Override
                                    public void onCancelled(DatabaseError databaseError)
                                    {
                                        one.setText(databaseError.toString());
                                    }
                                }
                        );

                    }
                }
        );
    }



    private void prepareCart() {



    }

    /**
     * callback when recycler view is swiped
     * item will be removed on swiped
     * undo option will be provided in snackbar to restore the item
     */
    @Override
    public void onSwiped(RecyclerView.ViewHolder viewHolder, int direction, int position) {
        if (viewHolder instanceof CartListAdapter.MyViewHolder) {
            // get the removed item name to display it in snack bar
            String name = cartList.get(viewHolder.getAdapterPosition()).getName();

            // backup of removed item for undo purpose
            final Item deletedItem = cartList.get(viewHolder.getAdapterPosition());
            final int deletedIndex = viewHolder.getAdapterPosition();

            // remove the item from recycler view
            mAdapter.removeItem(viewHolder.getAdapterPosition());

            // showing snack bar with Undo option
            Snackbar snackbar = Snackbar
                    .make(getWindow().getDecorView(), name + " removed from cart!", Snackbar.LENGTH_LONG);
            snackbar.setAction("UNDO", new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    // undo is selected, restore the deleted item
                    mAdapter.restoreItem(deletedItem, deletedIndex);
                }
            });
            snackbar.setActionTextColor(Color.YELLOW);
            snackbar.show();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds cartList to the action bar if it is present.
        return true;
    }


}
