package com.example.sohyun.battleproject;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions;
import com.bumptech.glide.request.RequestOptions;
import com.example.sohyun.battleproject.Util.AgmPrefer;
import com.google.firebase.auth.FirebaseAuth;

public class LeftMenuActivity extends AppCompatActivity {

    DrawerLayout drawerLayout;
    NavigationView naviView;
    FirebaseAuth firebaseAuth;
    TextView leftmenu_header_nickname;
    AgmPrefer ap;
    ImageView leftmenu_header_profile;


    public interface OnLogEventListener{
        void Fire();
    }

    OnLogEventListener mListener;

    public void setOnLogEventListener(OnLogEventListener listener){

        this.mListener = listener;

    }


    @Override
    public void setContentView(int layoutID) {
        super.setContentView(R.layout.activity_left_menu);

        ap = new AgmPrefer(getBaseContext());

        ViewGroup viewgroup = (ViewGroup) findViewById(R.id.naviFrame);
        LayoutInflater.from(this).inflate(layoutID, viewgroup, true);

        firebaseAuth = FirebaseAuth.getInstance();

        getNavigation();

        if (firebaseAuth.getCurrentUser() != null) {
            LoginText(true);
        }

    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    public void getNavigation() {

        drawerLayout = (DrawerLayout) findViewById(R.id.drawerLayout);
        naviView = (NavigationView) findViewById(R.id.naviView);
        naviView.setNavigationItemSelectedListener(naviListener);

        View headerView = naviView.getHeaderView(0);

        leftmenu_header_nickname = (TextView) headerView.findViewById(R.id.leftmenu_header_nickname);
        leftmenu_header_profile = (ImageView)headerView.findViewById(R.id.leftmenu_header_profile);
    }

    public void openDrawer() {
        if (drawerLayout != null) {
            drawerLayout.openDrawer(Gravity.LEFT);
        }
    }

    NavigationView.OnNavigationItemSelectedListener naviListener = new NavigationView.OnNavigationItemSelectedListener() {
        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {

            switch (item.getItemId()) {
                case R.id.menu_login:
                    if (item.getTitle().equals("로그인")) {
                        Intent intent = new Intent(getBaseContext(), LoginActivity.class);
                        startActivityForResult(intent, 100);
                    } else {
                        firebaseAuth.signOut();
                        ap.clear();
                        Toast.makeText(LeftMenuActivity.this, "로그아웃 되었습니다.",
                                Toast.LENGTH_SHORT).show();
                        drawerLayout.closeDrawer(Gravity.LEFT);
                        LoginText(false);
                    }
                    break;

                case R.id.menu_profile:
                    Intent intent = new Intent(getBaseContext(), ProfileActivity.class);
                    startActivityForResult(intent, 110);
                    break;

            }
            return false;
        }
    };

    private void LoginText(boolean isLogin) {
        leftmenu_header_nickname.setText(ap.getNickname());
        MenuItem item = naviView.getMenu().findItem(R.id.menu_login);
        MenuItem item_profile = naviView.getMenu().findItem(R.id.menu_profile);
        if (isLogin) {

            item.setTitle("로그아웃");
            item_profile.setVisible(true);

            leftmenu_header_nickname.setText(ap.getNickname());
            Glide.with(getBaseContext())
                    .load(ap.getProfilelmage())
                    .apply(new RequestOptions()
                            .placeholder(R.drawable.non_user)
                            .centerCrop()
                            .dontTransform())
                    .transition(new DrawableTransitionOptions().crossFade(500))
                    .into(leftmenu_header_profile);


        } else {
            item.setTitle("로그인");
            item_profile.setVisible(false);
            leftmenu_header_nickname.setText(R.string.login_message);
            leftmenu_header_profile.setImageResource(R.drawable.background);
        }

        if(mListener!=null)
            mListener.Fire();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        //로그인
        if (requestCode == 100) {
            if (resultCode == RESULT_OK) {
                LoginText(true);
            }
        }

        //프로필
        if (requestCode == 110) {
            if (resultCode == RESULT_OK) {
                LoginText(true);
            }
        }
    }
}
