package com.example.applicationcongess.PlayLoad.Response;


import com.example.applicationcongess.models.Role;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data

@NoArgsConstructor
public class JwtResponse {
    String jwt;





    public JwtResponse(String jwt ) {
        this.jwt = jwt ;


    }

}