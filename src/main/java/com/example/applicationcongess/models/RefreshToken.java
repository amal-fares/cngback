package com.example.applicationcongess.models;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.Instant;

import javax.persistence.*;

@Entity(name = "refreshtoken")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class RefreshToken {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
     long id;

    @OneToOne
     Personnel personnel;

    @Column(nullable = false, unique = true)
     String token;

    @Column(nullable = false)
     Instant expiryDate;
public RefreshToken (String token ){
    this.token=token ;

}
}