package com.example.sohyun.battleproject;

import android.content.SharedPreferences;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.example.sohyun.battleproject.Adapter.ReplyAdapter;
import com.example.sohyun.battleproject.DataClass.ItemData;
import com.example.sohyun.battleproject.DataClass.ReplyData;
import com.example.sohyun.battleproject.Util.AgmPrefer;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

public class DetailActivity extends AppCompatActivity {

    String board_idx;
    DatabaseReference myRef;
    FirebaseDatabase database;
    AgmPrefer ap;

    TextView detail_title, detail_user_date, detail_content;

    EditText reply_editTxt;
    ImageButton reply_sendBtn;
    ListView reply_ListView;

    ReplyAdapter adapter;

    LinearLayout llHeart;
    TextView itemCount;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);

        ap = new AgmPrefer(DetailActivity.this);


        board_idx = getIntent().getStringExtra("board_idx");
        database = FirebaseDatabase.getInstance();
        myRef = database.getReference();

        detail_title = (TextView) findViewById(R.id.detail_title);
        detail_user_date = (TextView) findViewById(R.id.detail_user_date);
        detail_content = (TextView) findViewById(R.id.detail_content);

        itemCount = (TextView) findViewById(R.id.itemCount);

        llHeart = (LinearLayout) findViewById(R.id.llHeart);
        llHeart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //하트클릭시 카운트 증가

                myRef.child("board").child(board_idx).addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        if (dataSnapshot.hasChildren()) {
                            int heart = dataSnapshot.getValue(ItemData.class).heart;
                            myRef.child("board").child(board_idx).child("heart").setValue(heart + 1);
                            itemCount.setText(heart+"Heart");
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });

            }
        });


        setDetailListen(); //firebase 에서 data를 가져옴
        reply_editTxt = (EditText) findViewById(R.id.reply_editTxt);
        reply_sendBtn = (ImageButton) findViewById(R.id.reply_sendBtn);
        reply_ListView = (ListView) findViewById(R.id.reply_ListView);

        reply_sendBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //댓글 전송되는 부분
                ReplyData newData = new ReplyData();
                String write_time = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.US).format(new Date());
                String reg_date = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.US).format(new Date());
                newData.idx = write_time;
                newData.reg_user_profile = ap.getProfilelmage();
                newData.reg_user_nickname = ap.getNickname();
                newData.content = reply_editTxt.getText().toString();
                newData.reg_date = reg_date;

                myRef.child("reply").child(board_idx).push().setValue(newData);
                reply_editTxt.setText("");

                //댓글등록 갯수에 따라서 카운트 증가
                myRef.child("board").child(board_idx).addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        if (dataSnapshot.hasChildren()) {
                            int reply = dataSnapshot.getValue(ItemData.class).reply;
                            myRef.child("board").child(board_idx).child("reply").setValue(reply + 1);
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });

            }
        });


        //바인딩
        adapter = new ReplyAdapter(DetailActivity.this, new ArrayList<ReplyData>());
        reply_ListView.setAdapter(adapter);
        setReplyListen();

    }


    private void setReplyListen() {

        myRef.child("reply").child(board_idx).addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                if (dataSnapshot.hasChildren()) {

                    ReplyData newData = new ReplyData();

                    //받은데이터
                    ReplyData revData = dataSnapshot.getValue(ReplyData.class);
                    newData.reg_user_nickname = revData.reg_user_nickname;
                    newData.content = revData.content;
                    newData.reg_date = revData.reg_date;
                    newData.isME = revData.reg_user_nickname.equals(ap.getNickname());

                    adapter.add(newData);
                    adapter.notifyDataSetChanged();
                    reply_ListView.setSelection(reply_ListView.getCount() - 1); //스크롤을 항상 아래로 보내는 코드


                }
            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {

            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });


    }


    //firebase 에서 data를 가져옴
    private void setDetailListen() {

        //board_idx 를 조회하는 부분
        myRef.child("board").orderByChild("idx").equalTo(board_idx).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                for (DataSnapshot oneSnap : dataSnapshot.getChildren()) {
                    ItemData idata = oneSnap.getValue(ItemData.class);

                    detail_title.setText(idata.title);
                    detail_content.setText(idata.summary);
                    detail_user_date.setText("[" + idata.reg_user + "]" + "   " + idata.reg_date);
                    itemCount.setText(idata.heart + "Heart");

                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }
}
