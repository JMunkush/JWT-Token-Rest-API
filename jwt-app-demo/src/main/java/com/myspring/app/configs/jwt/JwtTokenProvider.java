package com.myspring.app.configs.jwt;

import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.interfaces.DecodedJWT;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.GrantedAuthority;
import java.util.Collection;
import java.util.Date;
import java.util.stream.Collectors;

@Slf4j
public class JwtTokenProvider {


    @Value("${jwt.token.secret}")
    private static String secret = "secret";

    @Value("${jwt.token.expired}")
    private static int expired;

    public static String createToken(String username, Collection<GrantedAuthority> roles, int time){
        String token = JWT.create()
                .withSubject(username)
                .withExpiresAt(new Date(System.currentTimeMillis() + expired + time))
                .withClaim("roles", roles.stream().map(role -> role.getAuthority())
                        .collect(Collectors.toList()))
                .sign(Algorithm.HMAC256(secret.getBytes()));

        log.info("token created: ",  token);

        return token;
    }


    public static DecodedJWT verifyToken(String token){

        Algorithm algorithm = Algorithm.HMAC256(secret);
        JWTVerifier verifier = JWT.require(algorithm).build();
        return verifier.verify(token);
    }



}
