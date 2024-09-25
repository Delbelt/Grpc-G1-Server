package server.dtos;

import java.util.Collection;

import org.springframework.security.core.GrantedAuthority;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class UserDTO {
	
	private String token;
	private String userName;
	private Collection<? extends GrantedAuthority> roles;
}
