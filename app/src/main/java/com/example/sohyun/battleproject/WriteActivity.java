package com.example.sohyun.battleproject;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.provider.MediaStore;
import android.provider.OpenableColumns;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.example.sohyun.battleproject.Adapter.WriteAdapter;
import com.example.sohyun.battleproject.DataClass.ItemData;
import com.example.sohyun.battleproject.Util.AgmPrefer;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import kr.co.otilla.agmeditlist.ContentData;
import kr.co.otilla.agmeditlist.EditRecyclerView;

public class WriteActivity extends AppCompatActivity {

    EditText etTitle, etContent;
    ImageButton btnGallery, btnCamera;
    Button btnSubmit;
    WriteAdapter mAdapter;
    ArrayList<ContentData> mList = new ArrayList<>(); //이미지들
    private File photoFile;

    //상수선언
    static final int REQUEST_IMAGE_CAPTURE = 1;
    static final int REQUEST_SELECT_PHOTO = 2;

    AgmPrefer ap;
    FirebaseStorage storage = FirebaseStorage.getInstance();
    FirebaseDatabase database;
    DatabaseReference myRef;

    ProgressDialog pd;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_write);

        ap = new AgmPrefer(WriteActivity.this);
        pd=new ProgressDialog(WriteActivity.this);

        etTitle = (EditText) findViewById(R.id.etTitle);
        etContent = (EditText) findViewById(R.id.etContent);

        btnGallery = (ImageButton) findViewById(R.id.btnGallery);
        btnGallery.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                getPhoto();

            }
        });


        btnCamera = (ImageButton) findViewById(R.id.btnCamera);
        btnCamera.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getCamera();
            }
        });

        btnSubmit = (Button) findViewById(R.id.btnSubmit);
        btnSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Write();

            }
        });

        EditRecyclerView mRecyclerView = (EditRecyclerView) findViewById(R.id.recycle_view);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        mRecyclerView.setLayoutManager(layoutManager);
        mRecyclerView.addItemDecoration(new DividerItemDecoration(this, LinearLayout.VERTICAL));
        mAdapter = new WriteAdapter(this, mList);
        mAdapter.setEdit(true);
        mRecyclerView.setAdapter(mAdapter);

    }

    //갤러리에서 사진 가져오기
    private void getPhoto() {

               /* mList.add("Photo Add List");
                mAdapter.setEdit(true);
                mAdapter.notifyDataSetChanged();
                */

        Intent intent = new Intent(Intent.ACTION_PICK);
        intent.setType(android.provider.MediaStore.Images.Media.CONTENT_TYPE);
        intent.setData(android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(intent, REQUEST_SELECT_PHOTO); //선언한 상수로 값을 가져옴

    }


    //카메라 촬영
    private void getCamera() {

        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);


        if (intent.resolveActivity(getPackageManager()) != null) {
            photoFile = createImageFile();
        }

        if (photoFile != null) {

            //버전이 높을때
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                intent.putExtra(MediaStore.EXTRA_OUTPUT, FileProvider.getUriForFile(WriteActivity.this, "com.example.sohyun.battleproject.fileprovider", photoFile));
                intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
            }

            //버전이 낮을때
            else {
                intent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(photoFile));
            }

            startActivityForResult(intent, REQUEST_IMAGE_CAPTURE);

        } else {
            //파일생성이 실패했을 경우
            Toast.makeText(WriteActivity.this, "이미지 생성실패! 다시 시도해주세요.", Toast.LENGTH_SHORT);

        }
    }


    private File createImageFile() {

        //이미지를 중복되지 않게 하기위해 TimeStamp 가져옴
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.US).format(new Date());

        //현재시간으로 파일명을 만든다
        String imageFileName = "JPEG" + timeStamp + "_";

        File storageDir = getExternalFilesDir(null);


        try {
            return File.createTempFile(imageFileName, ".jpg", storageDir);

        } catch (IOException e) {

            return null;
        }

    }

    //FloatingActionButton -> 등록버튼 -> 등록하기
    private void Write() {

        pd.setMessage("등록중입니다. 잠시만 기다려주세요...");
        pd.show();


        //프로필 이미지를 저장하는 부분
        StorageReference storageRef = storage.getReference();
        StorageReference mountainsRef; // 파일명 만듬

        if (etTitle.getText().toString().isEmpty() || etContent.getText().toString().isEmpty()) {
            Toast.makeText(WriteActivity.this, "제목과 내용을 입력해주세요.", Toast.LENGTH_SHORT);
        } else {

            final ItemData idata = new ItemData();

            //고유값을 날짜로 만든다
            String image_time = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.US).format(new Date());

            idata.idx = image_time;
            idata.reg_user = ap.getNickname();
            idata.profile = ap.getProfilelmage();
            idata.title = etTitle.getText().toString();
            idata.summary = etContent.getText().toString();

            //public String image = " ";

            idata.heart = 0;
            idata.reply = 0;
            idata.more = "0";
            idata.count = 0;
            idata.reg_date = new SimpleDateFormat("yyyy-MM-dd", Locale.US).format(new Date());

            if (mList.size() > 0) {
                //이미지를 업로드 할 경우
                int imageCount = 0;
                for (ContentData cdata : mList) {

                    ByteArrayOutputStream baos = new ByteArrayOutputStream();
                    cdata.Bmp.compress(Bitmap.CompressFormat.JPEG, 100, baos);
                    byte[] data = baos.toByteArray();

                    mountainsRef = storageRef.child("board/" + image_time + "_" + imageCount + ".jpg");

                    UploadTask uploadTask = mountainsRef.putBytes(data);
                    uploadTask.addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception exception) {
                            idata.image += "none,";
                        }
                    }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {

                            Task<Uri> urlTask = taskSnapshot.getStorage().getDownloadUrl();
                            while (!urlTask.isSuccessful()) ;
                            Uri downloadUrl = urlTask.getResult();
                            idata.image += downloadUrl.toString() + ",";

                            doInsert(idata, true);

                        }
                    });

                    imageCount++;
                }
            } else {
                //이미지 업로드 x content만 업로드 할 경우

                doInsert(idata, false);
            }
        }

    }

    //filebase 에 database를 insert 하는부분
    private void doInsert(ItemData idata, boolean isImageUpload) {

        //filebase는 배열을 지원 x String을 배열처럼 사용

        if (isImageUpload) {
            //이미지 갯수를 검사
            if (idata.image.split(",").length == mList.size()) {
                database = FirebaseDatabase.getInstance();
                myRef = database.getReference("board");
                myRef.child(idata.idx).setValue(idata); // 등록이 될때 고유의 Uri + idata 값으로 업로드
                pd.dismiss();
                setResult(RESULT_OK);
                finish();
            }
        }
            else {
                //이미지가 없는 상태 업로드
                database = FirebaseDatabase.getInstance();
                myRef = database.getReference("board");
                myRef.child(idata.idx).setValue(idata); // 등록이 될때 고유의 Uri + idata 값으로 업로드
                pd.dismiss();
                setResult(RESULT_OK);
                finish();
            }



    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent intent) {
        super.onActivityResult(requestCode, resultCode, intent);

        ContentData cdata;

        switch (requestCode) {
            case REQUEST_SELECT_PHOTO://btnGallery 클릭시 갤러리에서 이미지 받아오기
                Uri imageUri = intent.getData(); //데이터받음
                cdata = new ContentData();


                try {
                    Cursor c = getContentResolver().query(imageUri, null, null, null, null);
                    c.moveToNext();
                    String path = c.getString(c.getColumnIndex(OpenableColumns.DISPLAY_NAME));

                    cdata.Bmp = MediaStore.Images.Media.getBitmap(this.getContentResolver(), imageUri);
                    cdata.FileName = path;
                    mList.add(cdata);
                    mAdapter.notifyDataSetChanged();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                break;

            case REQUEST_IMAGE_CAPTURE:
                cdata = new ContentData();

                Bitmap bmp = BitmapFactory.decodeFile(photoFile.getAbsolutePath());

                cdata.Bmp = bmp;
                cdata.FileName = photoFile.getName();
                mList.add(cdata);
                mAdapter.notifyDataSetChanged();

                break;
        }

    }
}
