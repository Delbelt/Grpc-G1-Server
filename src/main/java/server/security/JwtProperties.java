package server.security;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import lombok.Data;

@Data
@Component
public class JwtProperties {
	
	@Value("${jwt.secret}")
	private String secret;
	
	@Value("${jwt.expiration.time}")
    private long expirationTime;    
}
