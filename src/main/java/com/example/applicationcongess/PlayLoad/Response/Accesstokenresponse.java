package com.example.applicationcongess.PlayLoad.Response;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data

@NoArgsConstructor
public class Accesstokenresponse {
    String accestoken ;
    String refreshtoken;
    public Accesstokenresponse ( String accestoken,String refreshtoken){
    this.accestoken =accestoken;
    this.refreshtoken=refreshtoken;

    }
}