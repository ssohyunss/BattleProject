package com.example.sohyun.battleproject.Adapter;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions;
import com.bumptech.glide.request.RequestOptions;
import com.example.sohyun.battleproject.DataClass.ItemData;
import com.example.sohyun.battleproject.DetailActivity;
import com.example.sohyun.battleproject.ProfileActivity;
import com.example.sohyun.battleproject.R;
import com.example.sohyun.battleproject.Util.AgmPrefer;
import com.google.firebase.auth.FirebaseAuth;


import java.util.ArrayList;

public class MainAdapter extends RecyclerView.Adapter {

    ArrayList<ItemData> al;
    Context ctx;
    AgmPrefer ap;

    public interface OnContextClick{
        void ContextClick(int menuid,String idx);
    }

    OnContextClick onContextClickListener;

    public void setOnContextClickListener(OnContextClick listener){
        this.onContextClickListener = listener;
    }

    public MainAdapter(Context ctx, ArrayList<ItemData> al) {
        this.ctx = ctx;
        this.al = al;
        ap = new AgmPrefer(ctx);
    }

    public final static class ListItemViewHolder extends RecyclerView.ViewHolder {

        ImageView itemProfile;
        TextView itemTitle;
        ImageView itemImage;
        TextView itemSummary;
        //ImageButton itemHeart;
        TextView itemReply;
        ImageButton itemMore;
        TextView itemCount;
        TextView itemDate;

        public ListItemViewHolder(View itemView) {
            super(itemView);

            itemProfile = (ImageView) itemView.findViewById(R.id.itemProfile);
            itemTitle = (TextView) itemView.findViewById(R.id.itemTitle);
            itemImage = (ImageView) itemView.findViewById(R.id.itemImage);
            itemSummary = (TextView) itemView.findViewById(R.id.itemSummary);
            //itemHeart = (ImageButton)itemView.findViewById(R.id.itemHeart);
            itemReply = (TextView) itemView.findViewById(R.id.itemReply);
            itemMore = (ImageButton) itemView.findViewById(R.id.itemMore);
            itemCount = (TextView) itemView.findViewById(R.id.itemCount);
            itemDate = (TextView) itemView.findViewById(R.id.itemDate);

        }

    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View itemView = LayoutInflater.from((parent.getContext())).inflate(R.layout.item_main_row, parent, false);

        return new ListItemViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {

        final ListItemViewHolder iviewholder = (ListItemViewHolder) holder;

        final ItemData idata = al.get(position);

        iviewholder.itemTitle.setText(idata.title); //메인화면 title

        //title 클릭시 detail
        iviewholder.itemTitle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (FirebaseAuth.getInstance().getCurrentUser() != null) {

                    showDetailActivity(idata.idx);
                } else {
                    Toast.makeText(ctx, "정상적으로 수정되었습니다.",
                            Toast.LENGTH_SHORT).show();
                }
            }
        });

        iviewholder.itemSummary.setText(idata.summary);

        iviewholder.itemReply.setText(String.valueOf(idata.reply));
        iviewholder.itemCount.setText(String.valueOf(idata.heart));
        iviewholder.itemDate.setText(idata.reg_date);

        //프로필이미지설정
        Glide.with(ctx)
                .load(idata.profile)
                .apply(new RequestOptions()
                        .placeholder(R.drawable.non_user)
                        .centerCrop()
                        .dontTransform())
                .transition(new DrawableTransitionOptions().crossFade(500))
                .into(iviewholder.itemProfile);


      /**  if (!idata.image.isEmpty() && idata.image.split(",").length > 0) {

            Glide.with(ctx)
                    .load(idata.image.split(",")[0]) //첫번째 사진을 등록
                    .apply(new RequestOptions()
                            .placeholder(R.drawable.background)
                            .centerCrop()
                            .dontTransform())
                    .transition(new DrawableTransitionOptions().crossFade(500))
                    .into(iviewholder.itemImage);
            //iviewholder.itemSummary.setVisibility(View.GONE);
            //iviewholder.itemImage.setVisibility(View.VISIBLE);


        } else {
            //이미지가 없는 경우에는 글이 보이록

            iviewholder.itemSummary.setText(idata.summary);
            //iviewholder.itemSummary.setVisibility(View.VISIBLE);
            //iviewholder.itemImage.setVisibility(View.GONE);
        }**/


        //삭제 팝업 contextmenu
        if(idata.reg_user.equals(ap.getNickname())){

            iviewholder.itemMore.setVisibility(View.VISIBLE);


        iviewholder.itemMore.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //list_context_menu 팝업으로 띄우기
                PopupMenu popupMenu = new PopupMenu(ctx, iviewholder.itemMore);
                popupMenu.inflate(R.menu.list_context_menu);
                popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem item) {
                        switch (item.getItemId()) {
                            case R.id.context_menu_delete:
                                new AlertDialog.Builder(ctx)
                                        .setMessage("삭제하시겠습니까?")
                                        .setCancelable(false) //뒤로가기 버튼을 눌렀을때 popup창이 닫히지 않음
                                        .setPositiveButton("예", new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialog, int which) {
                                                //삭제처리하는부분 ( firebase에 접속하여 데이터를 삭제 -> MainActivity에 인터페이스를 날려줌 )
                                                if(onContextClickListener!=null){
                                                    onContextClickListener.ContextClick(R.id.context_menu_delete,idata.idx);
                                                }

                                            }
                                        })
                                        .setNegativeButton("아니오",null)
                                        .show();
                                break;
                            default:
                                break;
                        }


                        return false;
                    }

                });

                popupMenu.show();

            }
        });
    }

    }

    //title 클릭시 넘어가는 메소드
    private void showDetailActivity(String board_idx) {

        Intent intent = new Intent(ctx, DetailActivity.class);
        intent.putExtra("board_idx", board_idx);
        ctx.startActivity(intent);


    }


    @Override
    public int getItemCount() {
        return al.size();
    }
}
