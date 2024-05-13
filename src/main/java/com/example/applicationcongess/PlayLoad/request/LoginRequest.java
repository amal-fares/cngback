package com.example.applicationcongess.PlayLoad.request;

import lombok.Data;

@Data
public class LoginRequest {

     String username;

     String password;

    public String getUsername() {
        return username;
    }

    public String getPassword() {
        return password;
    }
}