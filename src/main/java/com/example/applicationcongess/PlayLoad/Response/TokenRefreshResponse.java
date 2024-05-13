package com.example.applicationcongess.PlayLoad.Response;

public class TokenRefreshResponse {
    private String accessToken;
    private String refreshToken;


    public TokenRefreshResponse(String accessToken, String refreshToken) {
        this.accessToken = accessToken;
        this.refreshToken = refreshToken;
    }}