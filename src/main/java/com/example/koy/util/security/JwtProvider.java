package com.example.koy.util.security;

import java.io.UnsupportedEncodingException;
import java.nio.charset.StandardCharsets;
import java.util.Date;

import javax.crypto.SecretKey;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Component;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
public class JwtProvider {
	@Value("${spring.data.redis.jwt-token.key}")
	String key;

	private final UserDetailsService userDetailsService;

	static final String JWT_CLAIM_ID = "id";
	static final String JWT_CLAIM_EMPLOYEE_NUMBER = "employee_number";

	@Autowired
	public JwtProvider(UserDetailsService userDetailsService) {
		this.userDetailsService = userDetailsService;
	}

	public String createToken(String adminId, String employeeNumber, Integer adminSessionExpireTime) throws
		UnsupportedEncodingException {
		long currentTime = System.currentTimeMillis();
		Date expiration = new Date(currentTime + adminSessionExpireTime * 60 * 1000);
		Date issuedAt = new Date(currentTime);
		SecretKey secretKey = createSecretKey();

		return Jwts.builder()
			.setSubject(adminId)
			.setHeaderParam("typ", "JWT")
			.setExpiration(expiration)
			.setIssuedAt(issuedAt)
			.claim(JWT_CLAIM_ID, adminId)
			.claim(JWT_CLAIM_EMPLOYEE_NUMBER, employeeNumber)
			.signWith(secretKey, SignatureAlgorithm.HS256)
			.compact();
	}

	private SecretKey createSecretKey() throws UnsupportedEncodingException {
		byte[] secretKey = key.getBytes(StandardCharsets.UTF_8);

		return Keys.hmacShaKeyFor(secretKey);
	}

	public Boolean validateToken(String token) {
		try {
			Jws<Claims> claims = Jwts.parserBuilder().setSigningKey(key.getBytes()).build().parseClaimsJws(token);

			boolean expired = claims.getBody().getExpiration().before(new Date());

			return !expired;
		} catch (Exception err) {
			log.error(err.getMessage());
			return false;
		}
	}

	public Authentication getAuthentication(String token) {
		Claims claims = Jwts.parserBuilder().setSigningKey(key.getBytes()).build().parseClaimsJws(token).getBody();

		UserDetails userDetails = userDetailsService.loadUserByUsername(
			claims.get(JWT_CLAIM_EMPLOYEE_NUMBER).toString());

		return new UsernamePasswordAuthenticationToken(userDetails, claims.get(JWT_CLAIM_EMPLOYEE_NUMBER),
			userDetails.getAuthorities());
	}
}
