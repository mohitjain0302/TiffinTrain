package com.example.tiffintrain;

public class User {
    private String name ;
    private int age ;
    private String email ;
    private int isOwner ;

    public User(){

    }

    public User(String name , int age , String email , int isOwner ){
        this.name = name ;
        this.age = age ;
        this.email = email ;
        this.isOwner = isOwner ;
    }

    public User(String name , String email , int isOwner ){
        this.name = name ;
        this.email = email ;
        this.isOwner = isOwner ;
    }

    public String getName() {
        return name;
    }

    public String getEmail() {
        return email;
    }

    public int getAge() {
        return age;
    }

    public int getIsOwner(){
        return  isOwner ;
    }
}
