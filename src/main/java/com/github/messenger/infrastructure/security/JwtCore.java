package com.github.messenger.infrastructure.security;

import com.nimbusds.jose.*;
import com.nimbusds.jose.crypto.MACSigner;
import com.nimbusds.jose.crypto.MACVerifier;
import com.nimbusds.jwt.JWTClaimsSet;
import com.nimbusds.jwt.SignedJWT;
import jakarta.annotation.PostConstruct;
import org.jetbrains.annotations.Nullable;
import org.springframework.security.core.Authentication;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.jetbrains.annotations.NotNull;

import java.text.ParseException;
import java.time.Instant;
import java.util.*;

@Component
public class JwtCore {
    @Value("${jwt.secret}")
    private String jwtSecret;

    @Value("${jwt.lifetime}")
    private int jwtLifetime;

    private byte[] jwtSecretBytes;

    public JwtCore(){}

    @PostConstruct
    public void init(){
        jwtSecretBytes = Base64.getDecoder().decode(jwtSecret);
        if (jwtSecretBytes.length != 32){
            throw new RuntimeException("jwtSecret length not equal to 32");
        }
    }

    public String generateToken(Authentication authentication) {
        UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();

        JWTClaimsSet claimsSet = new JWTClaimsSet.Builder()
                .claim("userId", userDetails.getId())
                .notBeforeTime(Date.from(Instant.now()))
                .expirationTime(Date.from(Instant.now().plusSeconds(jwtLifetime)))
                .build();

        JWSObject jwsObject = new JWSObject(new JWSHeader(JWSAlgorithm.HS256), new Payload(claimsSet.toJSONObject()));

        try {
            jwsObject.sign(new MACSigner(jwtSecretBytes));
        }
        catch (JOSEException e) {
            e.printStackTrace();
            return null;
        }

        return jwsObject.serialize();
    }

    public boolean verifyToken(String token) {
        try {
            SignedJWT jwt = SignedJWT.parse(token);
            JWTClaimsSet jwtClaimsSet = jwt.getJWTClaimsSet();

            Instant now = Instant.now();
            Date date = Date.from(now);

            if (jwtClaimsSet.getExpirationTime().before(date)) return false;
            if (jwtClaimsSet.getNotBeforeTime().after(date)) return false;

            if (jwt.verify(new MACVerifier(jwtSecretBytes))) return true;
        } catch (ParseException | JOSEException e) {
            e.printStackTrace();
        }
        return false;
    }

    public @Nullable Long getUserIdFromToken(@NotNull String token) {
        try {
            JWSObject jws = JWSObject.parse(token);
            return (Long) jws.getPayload().toJSONObject().get("userId");
        }
        catch (ParseException | NullPointerException e) {
            return null;
        }
    }
}
