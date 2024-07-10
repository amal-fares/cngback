package com.example.applicationcongess.services;

import com.example.applicationcongess.Exception.TokenRefreshException;
import com.example.applicationcongess.models.RefreshToken;
import com.example.applicationcongess.repositories.PersonnelRepository;
import com.example.applicationcongess.repositories.RefreshTokenRepository;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtBuilder;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.time.Instant;
import java.util.Date;
import java.util.Optional;
import java.util.UUID;

@Service
public class RefreshTokenService {
    @Value("${bezkoder.app.jwtRefreshExpirationMs}")
    private Long refreshTokenDurationMs;

    @Autowired
    private RefreshTokenRepository refreshTokenRepository;

    @Autowired
    private PersonnelRepository userRepository;

    public Optional<RefreshToken> findByToken(String token) {
        return refreshTokenRepository.findByToken(token);
    }
    @Value("${bezkoder.app.jwtSecret}")
    private String jwtSecret;
    public RefreshToken createRefreshToken(Long cin) {
        long refreshTokenDurationMs= 5 * 60 * 1000;
        Date expirationDate = new Date(System.currentTimeMillis() + refreshTokenDurationMs);
        Instant expirationInstant = Instant.now().plusMillis(refreshTokenDurationMs);
        Claims claims = Jwts.claims();

        claims.put("userId", cin);
        JwtBuilder builder = Jwts.builder()
                .setClaims(claims)
                .setId(UUID.randomUUID().toString())
                .setExpiration(expirationDate)
                .signWith(SignatureAlgorithm.HS512, jwtSecret);

        String refreshTokenString = builder.compact();
        System.out.print(refreshTokenString);
        RefreshToken refreshToken = new RefreshToken(refreshTokenString);
        refreshToken.setPersonnel(userRepository.findById(cin).get());
        refreshToken.setExpiryDate(expirationInstant);
        RefreshToken refreshtoken =refreshTokenRepository.save(refreshToken);

        return refreshtoken;

    }

    public RefreshToken verifyExpiration(RefreshToken token) {
        if (token.getExpiryDate().compareTo(Instant.now()) < 0) {
            refreshTokenRepository.delete(token);
            throw new TokenRefreshException(token.getToken(), "Refresh token was expired. Please make a new signin request");
        }

        return token;
    }

    @Transactional
    public int deleteByUserId(Long userId) {
        return refreshTokenRepository.deleteByPersonnel(userRepository.findById(userId).get());
    }




}