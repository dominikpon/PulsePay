package com.pulsepay.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.util.Date;

@Component
public class JwtUtil {

    //the secret password used to sign the token
    private static final String SECRET_KEY_STRING = "8f39b1a54c67d82f019e34c5678a9012b345c678d90e1234f567890123456789";

    //helper to turn the String into Cryptographic Key
    private SecretKey getSigningKey(){
        byte[] keyBytes = Decoders.BASE64.decode(SECRET_KEY_STRING);
        return Keys.hmacShaKeyFor(keyBytes);
    }

    //1. Generate Token
    public String generateToken (String username){
        return Jwts.builder()
                .subject(username) //save the username as "subject"
                .issuedAt(new Date()) //issues right now
                .expiration(new Date(System.currentTimeMillis() + 3600000)) //expires in 1 hour
                .signWith(getSigningKey()) //Sign it
                .compact();
    }

    //2. Read the Token Data
    public Claims readTokenData(String token){
        //this parses automatically checks the signature
        //if a hacker changed the token ,it will throw an error immediately
        return Jwts.parser()
                .verifyWith(getSigningKey())
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }

    //3. Extract just the username
    public String extractUsername(String token) {
        Claims data = readTokenData(token);
        return data.getSubject(); // pull the username
    }

    //4. Check if the token is expired
    public Boolean isTokenExpired(String token){
        Claims data = readTokenData(token);
        Date expirationDate = data.getExpiration();

        //return true if expiration date is before the current time
        return expirationDate.before(new Date());
    }
}
