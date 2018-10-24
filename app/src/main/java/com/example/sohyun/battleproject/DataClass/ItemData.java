package com.example.sohyun.battleproject.DataClass;

public class ItemData {

    public String idx=" ";
    public String reg_user= " ";
    public String profile=" ";
    public String title=" ";
    public String image=" ";
    public String summary=" ";
    public String more=" ";
    public int heart= 0;
    public int reply=0;
    public int count=0;
    public String reg_date="";

    public ItemData(){


    }

    public ItemData(String idx, String reg_user, String profile, String title, String image, String summary, int heart, int reply, String more, int count, String reg_date){
        this.idx=idx;
        this.reg_user=reg_user;
        this.profile=profile;
        this.title=title;
        this.image=image;
        this.summary=summary;
        this.heart=heart;
        this.reply=reply;
        this.more=more;
        this.count=count;
        this.reg_date=reg_date;
    }
}
