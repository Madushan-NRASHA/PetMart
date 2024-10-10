package com.example.petmart;

public class ReadwriteUserDetails {
    public String username , email, password;

    public ReadwriteUserDetails(){};

    public ReadwriteUserDetails(String username , String email , String password){
        this.username=username;
        this.email=email;
        this.password= password;
    }

}