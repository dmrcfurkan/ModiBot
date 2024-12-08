package com.example.modibot.database

class UserSign {

    var userName:String="";
    var userSurName:String="";
    var userEmail:String="";
    var userPassword:String="";
    var isPremium:Boolean=false;
    var isActive:Boolean=true;
    var userId: String=""

    constructor(userName:String,userSurName:String,userEmail:String,userPassword:String,isPremium:Boolean,isActive:Boolean,userId:String){

        this.userName=userName;
        this.userSurName=userSurName;
        this.userEmail=userEmail;
        this.userPassword=userPassword;
        this.isPremium=isPremium;
        this.isActive=isActive;
        this.userId=userId

    }



}