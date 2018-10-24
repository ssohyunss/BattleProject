package com.example.sohyun.battleproject;

import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;

import com.example.sohyun.battleproject.Adapter.MainAdapter;
import com.example.sohyun.battleproject.DataClass.ItemData;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.io.IOException;
import java.util.ArrayList;

public class MainActivity extends LeftMenuActivity {


    MainAdapter adapter;

    FirebaseDatabase database;
    DatabaseReference myRef;
    int index = 0;
    ArrayList<ItemData> al = new ArrayList<ItemData>();
    ImageView toolbar_leftmenu;
    FloatingActionButton btnWrite;

    FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        database = FirebaseDatabase.getInstance();
        myRef = database.getReference("board");
        adapter = new MainAdapter(this, al);
        adapter.setOnContextClickListener(new MainAdapter.OnContextClick() {
            @Override
            public void ContextClick(int menuid, String idx) {
                myRef.child(idx).removeValue();
                finish();
                startActivity(getIntent());
            }
        });

        btnWrite = (FloatingActionButton)findViewById(R.id.btnWrite);



        btnWrite.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent = new Intent(MainActivity.this, WriteActivity.class);
                startActivityForResult(intent , 300);

            }
        });




        toolbar_leftmenu = (ImageView)findViewById(R.id.toolbar_leftmenu);
        toolbar_leftmenu.setVisibility(View.VISIBLE);
        toolbar_leftmenu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openDrawer();
            }
        });

        RecyclerView mainScrollView = (RecyclerView) findViewById(R.id.mainScrollView);
        mainScrollView.setAdapter(adapter);
        mainScrollView.setLayoutManager(new LinearLayoutManager(this));

        myRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                for(DataSnapshot oneSnap : dataSnapshot.getChildren()){
                    ItemData idata = oneSnap.getValue(ItemData.class);
                    al.add(0,idata);
                }

                adapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        myRef.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

                for(ItemData onedata : al){
                    ItemData temp_data = dataSnapshot.getValue(ItemData.class);

                    if(onedata.idx.equals(temp_data.idx)){

                        onedata.title = temp_data.title;
                        onedata.image = temp_data.image;
                        onedata.reg_user = temp_data.reg_user;
                        onedata.reg_date= temp_data.reg_date;
                        onedata.count = temp_data.count;
                        onedata.reply = temp_data.reply;
                        onedata.more = temp_data.more;
                        onedata.profile = temp_data.profile;
                        onedata.heart= temp_data.heart;
                        onedata.summary = temp_data.summary;
                    }
                }

                adapter.notifyDataSetChanged(); //바뀐 데이터 새로고침
            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {

            }// myRef.setValue("Hello, World!");

            @Override
            public void onChildMoved(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        this.setOnLogEventListener(new OnLogEventListener() {
            @Override
            public void Fire() {
                CheckWriteButton();

            }
        });

        CheckWriteButton();

    }

    private void CheckWriteButton() {
        if(firebaseAuth.getCurrentUser()!=null){
            btnWrite.setVisibility(View.VISIBLE);
        }
        else {
            btnWrite.setVisibility(View.INVISIBLE);
        }



    }

    private void Writemessage(String index, ItemData idata) {
        myRef.child(index).setValue(idata);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent intent) {
        super.onActivityResult(requestCode, resultCode, intent);

        if(requestCode==300)
        {
            finish();
            startActivity(getIntent());

        }
    }
}
