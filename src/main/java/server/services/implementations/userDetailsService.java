package server.services.implementations;

import java.util.ArrayList;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import server.repositories.IUserRepository;

@Service("userDetailsService")
public class userDetailsService implements UserDetailsService {

	@Autowired
	private IUserRepository repository;

	@Override
	public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {

		server.entities.User user = repository.findByUserName(username);

		if (user == null) throw new UsernameNotFoundException("User not found: " + user);

		var roles = new ArrayList<GrantedAuthority>();

		roles.add(new SimpleGrantedAuthority(user.getRole().getName()));

		return new User(user.getUserName(), user.getPassword(), user.isActive(), true, true, true, roles);
	}
}
