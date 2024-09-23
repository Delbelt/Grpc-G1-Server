package server.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;

import java.nio.charset.StandardCharsets;
import java.util.Collection;
import java.util.Date;
import java.util.stream.Collectors;

@Component
public class JwtProvider {
	
	private String secret = "clave_secreta_jwt_grupo_1_integrantes_nf_hm_el_ev_sistemas_distribuidos";

	private final SecretKey secretKey = new SecretKeySpec(secret.getBytes(StandardCharsets.UTF_8), SignatureAlgorithm.HS512.getJcaName());
	private final long EXPIRATION_TIME = 86400000; // 1 day

	public String createToken(String username, Collection<? extends GrantedAuthority> authorities) {

		Date now = new Date();
		Date validity = new Date(now.getTime() + EXPIRATION_TIME);

		return Jwts.builder()
				.setSubject(username)
				.claim("authorities",authorities.stream().map(GrantedAuthority::getAuthority).collect(Collectors.toList()))
				.setIssuedAt(now).setExpiration(validity).signWith(secretKey).compact();
	}

	public String getUsernameFromToken(String token) {
		
		try {
			
			Claims claims = Jwts.parserBuilder().setSigningKey(secretKey).build().parseClaimsJws(token).getBody();

			if (claims.getExpiration().before(new Date())) {
				return null;
			}

			return claims.getSubject();

		} 
		
		catch (Exception e) {
			return null;
		}
	}

	public boolean validateToken(String token) {

		var isValidToken = getUsernameFromToken(token);

		return isValidToken != null;
	}
}
