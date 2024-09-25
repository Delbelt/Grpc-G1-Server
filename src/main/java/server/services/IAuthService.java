package server.services;

import server.dtos.UserDTO;

public interface IAuthService {
	
	public UserDTO login(String username, String password);
}
