package com.example.sohyun.battleproject;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.example.sohyun.battleproject.Util.AgmPrefer;
import com.google.android.gms.tasks.OnCompleteListener;


import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;


import java.io.ByteArrayOutputStream;
import java.io.IOException;


public class ProfileActivity extends AppCompatActivity {

    ImageView ivProfile;
    EditText etNickname;
    Button btnSave;
    Button btnCancel;

    FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
    FirebaseStorage storage = FirebaseStorage.getInstance();

    AgmPrefer ap;
    ProgressDialog pd;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);
        ap=new AgmPrefer(ProfileActivity.this);
        pd=new ProgressDialog(ProfileActivity.this);

        ivProfile = (ImageView)findViewById(R.id.ivProfile);
        //갤러리에서 가져오기
        ivProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_PICK);
                intent.setType(android.provider.MediaStore.Images.Media.CONTENT_TYPE);
                intent.setData(android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                startActivityForResult(intent,200);
            }
        });


        etNickname = (EditText)findViewById(R.id.etNickname);
        btnSave = (Button)findViewById(R.id.btnSave);
        btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Save();
            }
        });

        btnCancel = (Button)findViewById(R.id.btnCancel);

        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

    }

    private void Save() {


        pd.setMessage("프로필을 업데이트 중입니다. 잠시만 기다려주세요...");
        pd.show();

        //프로필 이미지를 저장하는 부분
        StorageReference storageRef = storage.getReference();
        StorageReference mountainsRef = storageRef.child("profile/"+ap.getNickname()+".jpg");


        ivProfile.setDrawingCacheEnabled(true);
        ivProfile.buildDrawingCache();
        Bitmap bitmap = ((BitmapDrawable) ivProfile.getDrawable()).getBitmap();
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
        byte[] data = baos.toByteArray();

        UploadTask uploadTask = mountainsRef.putBytes(data);

        uploadTask.addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception exception) {
                // Handle unsuccessful uploads
            }
        }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {

                Task<Uri> urlTask = taskSnapshot.getStorage().getDownloadUrl();
                while (!urlTask.isSuccessful());
                Uri downloadUrl = urlTask.getResult();

                ap.setProfileImage(downloadUrl.toString());
                ap.setNickname(etNickname.getText().toString());

                UserProfileChangeRequest profileUpdates = new UserProfileChangeRequest.Builder()
                        .setDisplayName(etNickname.getText().toString())
                        .setPhotoUri(downloadUrl)
                        .build();

                user.updateProfile(profileUpdates)
                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                if (task.isSuccessful()) {
                                    setResult(RESULT_OK);
                                    finish();
                                    Toast.makeText(ProfileActivity.this, "정상적으로 수정되었습니다.",
                                            Toast.LENGTH_SHORT).show();
                                }

                                else{
                                    Toast.makeText(ProfileActivity.this, "오류가 발생했습니다. 잠시후 다시 시도해주세요.",
                                            Toast.LENGTH_SHORT).show();
                                }

                                pd.dismiss();
                            }
                        });
            }
        });

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent intent) {
        super.onActivityResult(requestCode, resultCode, intent);

        if(requestCode==200)
        {

            Uri imageUri = intent.getData(); //데이터받음
            Bitmap bitmap = null;

            try {
                bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(),imageUri);
                ivProfile.setImageBitmap(bitmap);
            } catch (IOException e) {
                e.printStackTrace();
            }


        }
    }
}
