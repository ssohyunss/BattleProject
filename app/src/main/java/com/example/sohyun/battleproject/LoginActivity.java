package com.example.sohyun.battleproject;

import android.app.ProgressDialog;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.sohyun.battleproject.Util.AgmPrefer;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class LoginActivity extends AppCompatActivity {

    EditText etID;
    EditText etPWD;
    Button btnLogin;
    Button btnJoin;

    private FirebaseAuth mAuth;
    ProgressDialog pd;
    AgmPrefer ap;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        ap=new AgmPrefer(LoginActivity.this);

        pd = new ProgressDialog(LoginActivity.this);

        etID = (EditText)findViewById(R.id.etID);
        etPWD = (EditText)findViewById(R.id.etPWD);
        btnLogin = (Button)findViewById(R.id.btnLogin);
        btnJoin = (Button)findViewById(R.id.btnJoin );

        mAuth = FirebaseAuth.getInstance();

        btnJoin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                userJoin();
            }
        });

        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                userLogin();
            }
        });
    }

    private void userLogin() {

        String email = etID.getText().toString();
        String password = etPWD.getText().toString();

        pd.setMessage("로그인 중입니다. 잠시만 기다려주세요...");
        pd.show();


        mAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {

                            String email = mAuth.getCurrentUser().getEmail(); //e-mail 받아오기

                            if(!email.isEmpty()){
                                ap.setEmail(email);
                                ap.setNickname(email.split("@")[0]);
                            }
                            setResult(RESULT_OK);
                            finish();
                            Toast.makeText(LoginActivity.this, "로그인 되었습니다.",
                                    Toast.LENGTH_SHORT).show();

                        } else {
                            Toast.makeText(LoginActivity.this, "이메일, 비밀번호가 틀렸습니다!",
                            Toast.LENGTH_SHORT).show();
                        }

                        pd.dismiss();
                    }
                });

    }

    private void userJoin() {

        String email = etID.getText().toString();
        String password = etPWD.getText().toString();

        pd.setMessage("등록중입니다. 잠시만 기다려주세요...");
        pd.show();

        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            setResult(RESULT_OK);
                            finish();
                            Toast.makeText(LoginActivity.this, "정상적으로 등록되었습니다.",
                                    Toast.LENGTH_SHORT).show();

                        } else {

                            Toast.makeText(LoginActivity.this, "등록에러! 다시시도해주세요!",
                                    Toast.LENGTH_SHORT).show();

                        }

                        pd.dismiss();
                    }
                });

    }


}
