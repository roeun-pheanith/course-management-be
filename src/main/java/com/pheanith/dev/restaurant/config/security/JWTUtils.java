package com.pheanith.dev.restaurant.config.security;


import java.security.Key;
import java.util.Date;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.UnsupportedJwtException;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
public class JWTUtils {

	@Value("${app.jwtSecret}")
	private String jWTSecret;

	@Value("${app.jwtExpirationMs}")
	private int jWTExpirationMS;

	public String generateJWTToken(String username) {
		return Jwts.builder().setSubject(username).setIssuedAt(new Date())
				.setExpiration(new Date((new Date()).getTime() + jWTExpirationMS))
				.signWith(key(), SignatureAlgorithm.HS256).compact();
	}

	private Key key() {
		return Keys.hmacShaKeyFor(Decoders.BASE64.decode(jWTSecret));
	}

	public String getUsernameFromJWTToken(String token) {
		return Jwts.parserBuilder().setSigningKey(key()).build().parseClaimsJws(token).getBody().getSubject();
	}

	public boolean validationJWTToken(String authToken) {
		try {
			Jwts.parserBuilder().setSigningKey(key()).build().parse(authToken);
			return true;
		} catch (MalformedJwtException ex) {
			log.error("Invalid JWT Token: {}", ex.getMessage());
		} catch (ExpiredJwtException ex) {
			log.error("JWT Token Is Expired: {}", ex.getMessage());
		} catch (UnsupportedJwtException ex) {
			log.error("JWT Token Is Unsupported: {}", ex.getMessage());
		} catch (IllegalArgumentException ex) {
			log.error("JWT Claims String Is Empty: {}", ex.getMessage());
		}
		return false;

	}

}
