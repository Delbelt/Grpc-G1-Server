package server.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;

import java.nio.charset.StandardCharsets;
import java.util.Collection;
import java.util.Date;
import java.util.stream.Collectors;

@Component
public class JwtProvider {
	
    private final JwtProperties jwtProperties;
    private SecretKey secretKey;
    private long expirationTime;

    @Autowired
    public JwtProvider(JwtProperties jwtProperties) {
        this.jwtProperties = jwtProperties;
    }

    @PostConstruct
    private void init() {
        this.secretKey = new SecretKeySpec(jwtProperties.getSecret().getBytes(StandardCharsets.UTF_8), SignatureAlgorithm.HS512.getJcaName());
        this.expirationTime = jwtProperties.getExpirationTime();
    }

    public String createToken(String username, Collection<? extends GrantedAuthority> authorities) {
    	
        Date now = new Date();
        Date validity = new Date(now.getTime() + expirationTime);

        return Jwts.builder()
                .setSubject(username)
                .claim("authorities", authorities.stream().map(GrantedAuthority::getAuthority).collect(Collectors.toList()))
                .setIssuedAt(now)
                .setExpiration(validity)
                .signWith(secretKey)
                .compact();
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
    	
        return getUsernameFromToken(token) != null;
    }
}
