package com.example.left;

import android.text.Editable;

public class User {
    private int id;
    private int x,y;
    private String nickname;
    private String signature;
    private String imageresouce;
    private String filename;
    private String image;
    private String description;
    public User(){

    }
    public User(int x,int y){
        super();
        this.x=x;
        this.y=y;
    }
    public User(String imageresouce){
        super();
        this.imageresouce=imageresouce;
    }
    public User(int id,String nickname,String signature){
        super();
        this.id=id;
        this.nickname=nickname;
        this.signature=signature;
    }
    public User(String nickname,String signature){
        super();
        this.nickname=nickname;
        this.signature=signature;
    }

    public User(int id, String signature) {
        super();
        this.id=id;
        this.signature=signature;
    }

    public User(int id, String nickname, String signature, int x) {
        super();
        this.id=id;
        this.nickname=nickname;
        this.signature=signature;
        this.x=x;
    }

    public User(String nickname, String signature, int x) {
        super();
        this.nickname=nickname;
        this.signature=signature;
        this.x=x;
    }
    public User(String nickname, String signature,String filename, String imageresouce,String image,String description){
        super();
        this.nickname=nickname;
        this.signature=signature;
        this.filename=filename;
        this.imageresouce=imageresouce;
        this.image=image;
        this.description=description;
    }
    @Override
    public String toString(){
        return "User [id=" + id + ", nickname ="+ nickname + ", signature=" + signature + "]" ;
    }
    public int getId(){
        return id;
    }
    public void setID(int id){
        this.id=id;
    }
    public void setNickname(String username){
        this.nickname=nickname;
    }

    public void setSignature(String signature){
        this.signature=signature;
    }
    public String getNickname(){
        this.nickname=nickname;
        return nickname;
    }
    public String getFilename(){
        this.filename=filename;
        return filename;
    }
    public void setFilename(String filename){
        this.filename=filename;
    }
    public String getSignature(){
        this.signature=signature;
        return signature;
    }
    public String getImageresouce(){
        this.imageresouce=imageresouce;
        return imageresouce;
    }
    public String getImage(){
        this.image=image;
        return image;
    }
    public String getDescription(){
        this.description=description;
        return description;
    }
    public void setImage(){
      this.image=image;
    }
    public int getX(){
        this.x=x;
        return x;
    }
    public int getY(){
        this.y=y;
        return y;
    }
    public void setX(){
        this.x=x;
    }
    public void setY(){
        this.y=y;
    }
    public void setImage(String imageresouce){
        this.imageresouce=imageresouce;
    }
}
