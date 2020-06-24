package com.brianandjim.fourddata.utils;

import com.brianandjim.fourddata.entity.dtos.FourDDataUserDTO;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.impl.crypto.MacProvider;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import java.security.*;
import java.util.Base64;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

@Service
@Slf4j
public class JwtUtil {

    private static final Key SECRET_KEY = MacProvider.generateKey(SignatureAlgorithm.HS256);
    private static final byte[] secretBytes = SECRET_KEY.getEncoded();
    private static final String base64SecretBytes = Base64.getEncoder().encodeToString(secretBytes);

    public JwtUtil() throws NoSuchAlgorithmException {
        KeyPairGenerator keyGenerator = KeyPairGenerator.getInstance("RSA");
        keyGenerator.initialize(1024);

        KeyPair kp = keyGenerator.genKeyPair();
        PublicKey publicKey = kp.getPublic();
        PrivateKey privateKey = kp.getPrivate();

        String encodedPublicKey = Base64.getEncoder().encodeToString(publicKey.getEncoded());
        String encodedPrivateKey = Base64.getEncoder().encodeToString(privateKey.getEncoded());
        log.info("Public key: ");
        log.info(encodedPublicKey);
        log.info("Private key: ");
        log.info(encodedPrivateKey);
    }

    public String extractUsername(String token) {
        return extractClaim(token, Claims::getSubject);
    }

    private Date extractExpiration(String token) {
        return extractClaim(token, Claims::getExpiration);
    }

    private <T> T extractClaim(String token, Function<Claims, T> claimsResolver) {
        final Claims claims = extractAllClaims(token);
        return claimsResolver.apply(claims);
    }

    private Claims extractAllClaims(String token) {
        return Jwts.parser().setSigningKey(SECRET_KEY).parseClaimsJws(token).getBody();
    }

    private Boolean isTokenExpired(String token) {
        return extractExpiration(token).before(new Date());
    }

    public String generateToken(FourDDataUserDTO userDetails) {
        Map<String, Object> claims = new HashMap<>();
        claims.put(userDetails.getUsername(), userDetails);
        return createToken(claims, userDetails.getUsername());
    }

    private String createToken(Map<String, Object> user, String username) {
        return Jwts.builder().setClaims(user).setSubject(username).setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + (1000 * 60 * 60)))
                .signWith(SignatureAlgorithm.HS256, base64SecretBytes).compact();
    }

    public Boolean validateToken(String token, UserDetails userDetails) {
        final String username = extractUsername(token);
        return (username.equals(userDetails.getUsername()) && !isTokenExpired(token));
    }
}
