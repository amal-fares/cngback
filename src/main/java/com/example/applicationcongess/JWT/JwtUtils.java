package com.example.applicationcongess.JWT;
import java.util.Date;

import com.example.applicationcongess.models.Personnel;
import com.example.applicationcongess.repositories.PersonnelRepository;
import com.example.applicationcongess.services.UserDetailsImpl;

import io.jsonwebtoken.*;
import io.jsonwebtoken.SignatureAlgorithm;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.keygen.KeyGenerators;
import org.springframework.stereotype.Component;

@Component
public class JwtUtils {
    private static final Logger logger = LoggerFactory.getLogger(JwtUtils.class);
    @Value("${bezkoder.app.jwtSecret}")
    private String jwtSecret;
    @Value("${bezkoder.app.jwtExpirationMs}")
    private int jwtExpirationMs;
    public String generateJwtToken(Authentication authentication) {
        UserDetailsImpl userPrincipal = (UserDetailsImpl) authentication.getPrincipal();
        return Jwts.builder()
                .setSubject((userPrincipal.getUsername()))
                .setIssuedAt(new Date())
                .setExpiration(new Date((new Date()).getTime() + jwtExpirationMs))
                .signWith(SignatureAlgorithm.HS512, jwtSecret)
                .compact();
    }
    public String getUserNameFromJwtToken(String token) {
        return Jwts.parser().setSigningKey(jwtSecret).parseClaimsJws(token).getBody().getSubject();
    }
    public boolean validateJwtToken(String authToken) {
        try {
            Jwts.parser().setSigningKey(jwtSecret).parseClaimsJws(authToken);
            return true;
        } catch (SignatureException e) {
            logger.error("Invalid JWT signature: {}", e.getMessage());
        } catch (MalformedJwtException e) {
            logger.error("Invalid JWT token: {}", e.getMessage());
        } catch (ExpiredJwtException e) {
            logger.error("JWT token is expired: {}", e.getMessage());
        } catch (UnsupportedJwtException e) {
            logger.error("JWT token is unsupported: {}", e.getMessage());
        } catch (IllegalArgumentException e) {
            logger.error("JWT claims string is empty: {}", e.getMessage());
        }
        return false;
    }


    public String generateTokenFromUsername(String username) {
        return Jwts.builder().setSubject(username).setIssuedAt(new Date())
                .setExpiration(new Date((new Date()).getTime() + jwtExpirationMs)).signWith(SignatureAlgorithm.HS512, jwtSecret)
                .compact();
    }
@Autowired
    PersonnelRepository personnelRepository;
    public Personnel getUserFromAccessToken(String accessToken) {
        try {
            System.out.println("ici");
            Claims claims = Jwts.parser()
                    .setSigningKey(jwtSecret)
                    .parseClaimsJws(accessToken)
                    .getBody();
            System.out.println(claims);
            Long userId = claims.get("userId", Long.class);
            System.out.println(userId);
            Personnel personnel=personnelRepository.findById(userId).orElse(null);
        String username= personnel.getUsername();

            System.out.println(username);


            Personnel personnel1 = new Personnel(userId, username);
            System.out.println(personnel);
            System.out.println(personnel);

            return personnel;
        } catch (ExpiredJwtException ex) {
            //si le jeton est exipre
            throw ex;
        } catch (Exception ex) {
            // Gérer d'autres exceptions liées au jeton d'accès
            throw new RuntimeException("Erreur lors de l'extraction des informations utilisateur à partir du jeton d'accès", ex);
        }
    }
    ///generation d'access token qui remplacera  le refreshtoken
    public String generateAccessToken(Long cin) {
        Personnel personnel=personnelRepository.findById(cin).orElse(null);

        // Durée de validité du jeton d'accès (en millisecondes)
        long accessTokenValidity = 3600000; // 1 heure

        // Générer le jeton d'accès
        String accessToken = Jwts.builder()
                .setSubject(personnel.getUsername())

                .setExpiration(new Date(System.currentTimeMillis() + accessTokenValidity))
                .signWith(SignatureAlgorithm.HS512, jwtSecret)
                .compact();

        return accessToken;
    }
}
